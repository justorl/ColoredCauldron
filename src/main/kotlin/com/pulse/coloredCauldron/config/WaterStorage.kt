package com.pulse.coloredCauldron.config

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.TextDisplay
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.UUID

class WaterStorage(private val plugin: JavaPlugin) {
    private val waterFile = File(plugin.dataFolder, "waterdb.yml")
    private var waterConfig: YamlConfiguration = loadWater()

    private fun loadWater(): YamlConfiguration {
        if (!waterFile.exists()) {
            plugin.dataFolder.mkdirs()
            plugin.saveResource("waterdb.yml", false)
            waterFile.createNewFile()
        }

        val config = YamlConfiguration.loadConfiguration(waterFile)

        config.getKeys(false).forEach { key ->
            config.getString("$key.world")?.let { worldName ->
                plugin.server.getWorld(worldName)?.getEntity(UUID.fromString(key))?.let { it as? TextDisplay }?.also { entity ->
                    entity.backgroundColor?.let { color ->
                        entity.backgroundColor = Color.fromARGB(
                            plugin.config.getInt("ColoredWater.alpha"),
                            color.red,
                            color.green,
                            color.blue
                        )
                    }
                }
            }
        }

        return config
    }

    private fun save(config: YamlConfiguration = waterConfig) {
        try {
            config.save(waterFile)
        } catch (ex: IOException) {
            plugin.logger.severe("Can't save waterdb.yml: ${ex.message}")
        }
    }

    fun removeWater(entity: TextDisplay) {
        val key = entity.uniqueId.toString()

        if (waterConfig.contains(key)) {
            waterConfig.set(key, null)
            save()
        }
    }

    fun saveWater(entity: TextDisplay) {
        val loc = entity.location
        val key = entity.uniqueId.toString()

        waterConfig.set("$key.world", loc.world?.name)
        waterConfig.set("$key.x", loc.x)
        waterConfig.set("$key.y", loc.y)
        waterConfig.set("$key.z", loc.z)

        save()
    }

    fun getWater(location: Location): Entity? {
        val key = waterConfig.getKeys(false).firstOrNull { k ->
            val worldName = waterConfig.getString("$k.world") ?: return@firstOrNull false
            val x = waterConfig.getDouble("$k.x")
            val y = waterConfig.getDouble("$k.y")
            val z = waterConfig.getDouble("$k.z")

            worldName == location.world?.name && x == location.x && y == location.y && z == location.z
        } ?: return null

        return location.world?.getEntity(UUID.fromString(key))
    }


    fun resetWater() {
        waterConfig = YamlConfiguration()
        save()
    }

    fun reload() {
        waterConfig = loadWater()
    }
}