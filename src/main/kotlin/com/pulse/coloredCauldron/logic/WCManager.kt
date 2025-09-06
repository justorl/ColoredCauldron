package com.pulse.coloredCauldron.logic

import com.pulse.coloredCauldron.CCInstance.plugin
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay

object WCManager {

    fun spawn(location: Location): WaterCauldron {
        val world = location.world ?: throw IllegalArgumentException("World cannot be null")
        val displayEntity = world.spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay

        val defaultColorHex = plugin.config.getString("ColoredWater.default-color") ?: "#FFFFFF"
        displayEntity.backgroundColor = Util.hex2rgb(defaultColorHex, 0)

        val cauldron = WaterCauldron(displayEntity, location.block)
        plugin.waterStorage.saveWater(displayEntity)
        return cauldron
    }

    fun get(location: Location): WaterCauldron? {
        val storedEntity = plugin.waterStorage.getWater(location) as? TextDisplay ?: return null
        return WaterCauldron(storedEntity, location.block)
    }

    fun remove(location: Location) {
        get(location)?.remove()
    }

    fun removeAll() {

    }
}