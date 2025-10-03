package com.pulse.coloredcauldron.logic

import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionType

object DyeUtil {
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

    val dyeColors = mapOf(
        Material.WHITE_DYE to DyeColor.WHITE,
        Material.LIGHT_GRAY_DYE to DyeColor.LIGHT_GRAY,
        Material.GRAY_DYE to DyeColor.GRAY,
        Material.BLACK_DYE to DyeColor.BLACK,
        Material.RED_DYE to DyeColor.RED,
        Material.BLUE_DYE to DyeColor.BLUE,
        Material.BROWN_DYE to DyeColor.BROWN,
        Material.ORANGE_DYE to DyeColor.ORANGE,
        Material.YELLOW_DYE to DyeColor.YELLOW,
        Material.LIME_DYE to DyeColor.LIME,
        Material.GREEN_DYE to DyeColor.GREEN,
        Material.LIGHT_BLUE_DYE to DyeColor.LIGHT_BLUE,
        Material.CYAN_DYE to DyeColor.CYAN,
        Material.PURPLE_DYE to DyeColor.PURPLE,
        Material.MAGENTA_DYE to DyeColor.MAGENTA,
        Material.PINK_DYE to DyeColor.PINK
    )

    fun Material.dyeColor(): DyeColor? {
        return dyeColors[this]
    }
}