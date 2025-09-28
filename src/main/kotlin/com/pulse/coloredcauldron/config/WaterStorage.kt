package com.pulse.coloredcauldron.config

import com.pulse.coloredcauldron.CCInstance.plugin
import com.pulse.coloredcauldron.logic.Util
import com.pulse.coloredcauldron.water.WCManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.TextDisplay
import java.io.File
import java.io.IOException
import java.util.UUID

class WaterStorage() {
    private val waterFile = File(plugin.dataFolder, "waterdb.yml")
    private var waterConfig: YamlConfiguration = YamlConfiguration.loadConfiguration(waterFile)

    fun reload() {
        Util.reloadYaml(waterFile, "waterdb.yml")
    }

    fun save(config: YamlConfiguration = waterConfig) {
        try {
            config.save(waterFile)
        } catch (ex: IOException) {
            plugin.logger.severe("Can't save waterdb.yml: ${ex.message}")
        }
    }

    fun remove(entity: TextDisplay) {
        val key = entity.uniqueId.toString()

        if (waterConfig.contains(key)) {
            waterConfig.set(key, null)
            save()
        }
    }

    fun add(entity: TextDisplay) {
        val loc = entity.location
        val key = entity.uniqueId.toString()

        waterConfig.set("$key.world", loc.world?.name)
        waterConfig.set("$key.x", loc.x)
        waterConfig.set("$key.y", loc.y)
        waterConfig.set("$key.z", loc.z)

        save()
    }

    fun reset() {
        waterConfig.getKeys(false).forEach { k ->
            val location = getLocation(k) ?: return@forEach
            WCManager.remove(location)
        }

        waterConfig = YamlConfiguration()
        save()
    }

    fun getLocation(k: String): Location? {
        val worldName = waterConfig.getString("$k.world")
        val x = waterConfig.getDouble("$k.x")
        val y = waterConfig.getDouble("$k.y")
        val z = waterConfig.getDouble("$k.z")
        if (x.isNaN() || y.isNaN() || z.isNaN() || worldName == null) { return null }

        return Location(Bukkit.getWorld(worldName), x, y, z)
    }

    fun getWater(location: Location): Entity? {
        val key = waterConfig.getKeys(false).firstOrNull { k ->
            location == getLocation(k)
        } ?: return null

        return location.world?.getEntity(UUID.fromString(key))
    }
}