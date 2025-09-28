package com.pulse.coloredcauldron.water

import com.pulse.coloredcauldron.CCInstance.configManager
import com.pulse.coloredcauldron.CCInstance.foliaLib
import com.pulse.coloredcauldron.CCInstance.waterStorage
import com.pulse.coloredcauldron.config.ConfigKeys.DEFAULT_ALPHA
import com.pulse.coloredcauldron.config.ConfigKeys.DEFAULT_COLOR
import com.pulse.coloredcauldron.logic.Util
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
                    configManager.getString(DEFAULT_COLOR),
                    0
                )
            }
            update()
        }

    init {
        init()
    }

    private fun init() {
        entity.text(Component.text("   "))

        entity.transformation = entity.transformation.apply {
            scale.set(2.85f, 3.45f, 1f)
            translation.set(0.45f, -0.90f, 0.939f)
        }

        foliaLib.scheduler.teleportAsync(entity, entity.location.setRotation(0F, -90F))
    }

    fun remove() {
        waterStorage.remove(entity)
        entity.remove()
    }

    fun mix(dye: Color) {
        val currentColor = entity.backgroundColor ?: Util.hex2rgb(configManager.getString(DEFAULT_COLOR), 200)
        var newColor = dye

        if (newColor == (Color.WHITE)) newColor = Util.hex2rgb(configManager.getString(DEFAULT_COLOR), 200)
        else if (level != 0) newColor = currentColor.mixColors(dye)

        color = Color.fromARGB(
            configManager.getInt(DEFAULT_ALPHA),
            newColor.red,
            newColor.green,
            newColor.blue
        )
    }

    fun mix(dye: DyeColor) {
        val currentColor = entity.backgroundColor ?: Util.hex2rgb(configManager.getString(DEFAULT_COLOR), 200)
        val newColor = currentColor.mixDyes(dye)

        color = Color.fromARGB(
            configManager.getInt(DEFAULT_ALPHA),
            newColor.red,
            newColor.green,
            newColor.blue
        )
    }

    fun update() {
        val waterHeight = listOf(0.2f, 0.58f, 0.755f, 0.939f)

        entity.transformation = entity.transformation.apply {
            translation.set(0.45f, -0.90F, waterHeight.getOrElse(level) { waterHeight[0] })
        }
    }
}