package com.pulse.coloredcauldron.water

import com.pulse.coloredcauldron.config.Config
import com.pulse.coloredcauldron.utils.DyeUtil.alpha
import com.pulse.utilslib.paper.plugin.PluginContext.foliaLib
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Levelled
import org.bukkit.entity.TextDisplay

data class Cauldron(
    val entity: TextDisplay,
    val block: Block
) {

    var color: Color
        get() = entity.backgroundColor ?: Color.WHITE
        set(value) {
            update()
            entity.backgroundColor = value
        }

    var level: Int
        get() = (block.blockData as? Levelled)?.level ?: 0
        set(value) {
            if (value > 0) {
                if (block.type == Material.CAULDRON) {
                    block.type = Material.WATER_CAULDRON
                }

                val cauldronData = block.blockData as? Levelled ?: return

                cauldronData.level = value.coerceAtMost(cauldronData.maximumLevel)
                block.blockData = cauldronData
            } else {
                block.type = Material.CAULDRON
                entity.backgroundColor = Config.ColoredWater.defaultColor.alpha(0)
            }
            update()
        }

    fun move(to: Location) {
        foliaLib.scheduler.teleportAsync(
            entity,
            to.setRotation(0F, -90F)
        )
    }

    fun remove() {
        entity.remove()
    }

    fun mix(dye: Color) {
        val currentColor = entity.backgroundColor
            ?: Config.ColoredWater.defaultColor.alpha(Config.ColoredWater.alpha)

        var newColor = dye

        if (newColor == (Color.WHITE))
            newColor = Config.ColoredWater.defaultColor.alpha(Config.ColoredWater.alpha)
        else if (level != 0)
            newColor = currentColor.mixColors(dye)

        color = Color.fromARGB(
            Config.ColoredWater.alpha,
            newColor.red,
            newColor.green,
            newColor.blue
        )
    }

    fun mix(dye: DyeColor) {
        val currentColor = entity.backgroundColor
            ?: Config.ColoredWater.defaultColor.alpha(Config.ColoredWater.alpha)

        val newColor = currentColor.mixDyes(dye)

        color = Color.fromARGB(
            Config.ColoredWater.alpha,
            newColor.red,
            newColor.green,
            newColor.blue
        )
    }

    fun update() {
        val waterHeight = listOf(0.2f, 0.58f, 0.755f, 0.939f)

        entity.transformation = entity.transformation.apply {
            translation.set(
                0.45f,
                -0.90F,
                waterHeight.getOrElse(level) { waterHeight[0] }
            )
        }
    }
}