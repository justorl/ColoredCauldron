package com.pulse.coloredCauldron.logic

import com.pulse.coloredCauldron.CCInstance.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Levelled
import org.bukkit.entity.TextDisplay

class WaterCauldron(private val entity: TextDisplay, private val block: Block) {

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
                entity.backgroundColor = Util.hex2rgb(
                    plugin.config.getString("ColoredWater.default-color")!!,
                    0
                )
            }
            update()
        }

    init {
        prepare()
    }

    private fun prepare() {
        entity.text(Component.text("   "))

        entity.transformation = entity.transformation.apply {
            scale.set(2.85f, 3.45f, 1f)
            translation.set(0.45f, -0.90f, 0.939f)
        }

        entity.teleport(entity.location.setRotation(0F, -90F))
    }

    fun remove() {
        plugin.waterStorage.removeWater(entity)
        entity.remove()
    }

    fun mix(dye: Color) {
        val currentColor = entity.backgroundColor ?: Util.hex2rgb(plugin.config.getString("ColoredWater.default-color")!!, 200)
        var newColor = dye

        if (newColor == (Color.WHITE)) newColor = Util.hex2rgb(plugin.config.getString("ColoredWater.default-color")!!, 200)
        else if (level != 0) newColor = currentColor.mixColors(dye)

        color = Color.fromARGB(
            plugin.config.getInt("ColoredWater.alpha"),
            newColor.red,
            newColor.green,
            newColor.blue
        )
    }

    fun mix(dye: DyeColor) {
        val currentColor = entity.backgroundColor ?: Util.hex2rgb(plugin.config.getString("ColoredWater.default-color")!!, 200)
        val newColor = currentColor.mixDyes(dye)

        color = Color.fromARGB(
            plugin.config.getInt("ColoredWater.alpha"),
            newColor.red,
            newColor.green,
            newColor.blue
        )
    }

    fun update() {
        val waterHeight = listOf(0.2f, 0.58f, 0.755f, 0.939f)

        entity.transformation = entity.transformation.apply {
            translation.set(0.45f, -0.90F, waterHeight.getOrElse(level) { 0.2f })
        }
    }
}
