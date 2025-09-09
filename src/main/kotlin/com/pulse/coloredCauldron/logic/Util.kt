package com.pulse.coloredCauldron.logic

import com.pulse.coloredCauldron.CCInstance.foliaLib
import com.pulse.coloredCauldron.CCInstance.plugin
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionType

object Util {

    val dyeColors = mapOf(
       Material.WHITE_DYE to org.bukkit.DyeColor.WHITE,
       Material.LIGHT_GRAY_DYE to org.bukkit.DyeColor.LIGHT_GRAY,
       Material.GRAY_DYE to org.bukkit.DyeColor.GRAY,
       Material.BLACK_DYE to org.bukkit.DyeColor.BLACK,
       Material.RED_DYE to org.bukkit.DyeColor.RED,
       Material.BLUE_DYE to org.bukkit.DyeColor.BLUE,
       Material.BROWN_DYE to org.bukkit.DyeColor.BROWN,
       Material.ORANGE_DYE to org.bukkit.DyeColor.ORANGE,
       Material.YELLOW_DYE to org.bukkit.DyeColor.YELLOW,
       Material.LIME_DYE to org.bukkit.DyeColor.LIME,
       Material.GREEN_DYE to org.bukkit.DyeColor.GREEN,
       Material.LIGHT_BLUE_DYE to org.bukkit.DyeColor.LIGHT_BLUE,
       Material.CYAN_DYE to org.bukkit.DyeColor.CYAN,
       Material.PURPLE_DYE to org.bukkit.DyeColor.PURPLE,
       Material.MAGENTA_DYE to org.bukkit.DyeColor.MAGENTA,
       Material.PINK_DYE to org.bukkit.DyeColor.PINK
    )

    fun runLater(delay: Long, task: () -> Unit) {
        foliaLib.scheduler.runLater(task, delay)
    }

    fun Block.getShift(face: BlockFace): Location {
        return this.location.add(face.direction)
    }

    fun playConfigSound(player: Player, path: String) {
        val soundKey = plugin.config.getString(path) ?: return

        player.playSound(Sound.sound(Key.key(soundKey), Sound.Source.MASTER, 1f, 1f))
    }

    fun hex2rgb(hex: String, alpha: Int): Color {
        val clean = hex.removePrefix("#")
        val r = clean.substring(0, 2).toInt(16)
        val g = clean.substring(2, 4).toInt(16)
        val b = clean.substring(4, 6).toInt(16)

        return Color.fromARGB(alpha, r, g, b)
    }

    fun adjustItemColor(color: Color, item: ItemStack): ItemStack {
        return item.apply {
            val meta = itemMeta as? LeatherArmorMeta ?: return@apply
            meta.setColor(color)
            itemMeta = meta
        }
    }

    fun getColoredPotion(color: Color): ItemStack {
        return ItemStack(Material.POTION).apply {
            val meta = itemMeta as PotionMeta
            meta.basePotionType = PotionType.WATER
            meta.color = color
            itemMeta = meta
        }
    }

    fun getPotionColor(item: ItemStack?): Color {
        val meta = (item?.itemMeta ?: return Color.WHITE) as? PotionMeta ?: return Color.WHITE
        return meta.color ?: Color.WHITE
    }
}
