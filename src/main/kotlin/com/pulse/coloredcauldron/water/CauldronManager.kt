package com.pulse.coloredcauldron.water

import com.jeff_media.morepersistentdatatypes.DataType
import com.pulse.coloredcauldron.config.Config
import com.pulse.coloredcauldron.utils.DyeUtil.alpha
import com.pulse.coloredcauldron.utils.Util.pdc
import com.pulse.utilslib.adventure.extension.cmp
import com.pulse.utilslib.paper.plugin.PluginContext
import com.pulse.utilslib.paper.plugin.PluginContext.foliaLib
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.persistence.PersistentDataType

object CauldronManager {

    val PDC_ENTITY = NamespacedKey.fromString("cauldron_entity", PluginContext.plugin)!!

    fun spawn(location: Location): Cauldron {
        val entity = location.world
            .spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay
        val block = location.block

        val cauldron = Cauldron(entity, block)

        entity.backgroundColor = Config.ColoredWater.defaultColor.alpha(0)
        entity.text("   ".cmp())
        entity.transformation = entity.transformation.apply {
            scale.set(2.85f, 3.45f, 1f)
            translation.set(0.45f, -0.90f, 0.939f)
        }
        cauldron.move(entity.location)

        block.pdc().set(
            PDC_ENTITY,
            DataType.UUID,
            entity.uniqueId
        )

        return cauldron
    }

    fun get(location: Location): Cauldron? {
        val block = location.block

        if (block.type != Material.CAULDRON &&
            block.type != Material.WATER_CAULDRON) return null

        val uuid = block.pdc().get(
            PDC_ENTITY,
            DataType.UUID,
        ) ?: return null
        val entity = Bukkit.getEntity(uuid) as? TextDisplay? ?: return null

        return Cauldron(entity, location.block)
    }

    fun remove(location: Location) {
        get(location)?.remove()
    }
}