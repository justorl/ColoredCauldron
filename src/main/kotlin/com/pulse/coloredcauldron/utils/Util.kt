package com.pulse.coloredcauldron.utils

import com.jeff_media.customblockdata.CustomBlockData
import com.pulse.utilslib.paper.plugin.PluginContext
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

object Util {

    fun Block.pdc() = CustomBlockData(this, PluginContext.plugin)

    fun hex2rgb(hex: String, alpha: Int): Color {
        val clean = hex.removePrefix("#")
        val r = clean.substring(0, 2).toInt(16)
        val g = clean.substring(2, 4).toInt(16)
        val b = clean.substring(4, 6).toInt(16)

        return Color.fromARGB(alpha, r, g, b)
    }
}
