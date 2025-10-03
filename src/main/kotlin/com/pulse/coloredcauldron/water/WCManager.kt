package com.pulse.coloredcauldron.water

import com.pulse.coloredcauldron.CCInstance.configManager
import com.pulse.coloredcauldron.CCInstance.waterStorage
import com.pulse.coloredcauldron.config.ConfigKeys.DEFAULT_COLOR
import com.pulse.coloredcauldron.logic.Util
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay

object WCManager {

    fun spawn(location: Location): WaterCauldron {
        val world = location.world
        val displayEntity = world.spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay
        val defaultColor = configManager.getString(DEFAULT_COLOR)

        displayEntity.backgroundColor = Util.hex2rgb(defaultColor, 0)

        val cauldron = WaterCauldron(displayEntity, location.block)
        waterStorage.add(displayEntity)

        return cauldron
    }

    fun get(location: Location): WaterCauldron? {
        val storedEntity = waterStorage.getWater(location) as? TextDisplay ?: return null
        return WaterCauldron(storedEntity, location.block)
    }

    fun remove(location: Location) {
        get(location)?.remove()
    }
}