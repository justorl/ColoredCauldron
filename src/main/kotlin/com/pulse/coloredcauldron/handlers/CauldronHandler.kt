package com.pulse.coloredcauldron.handlers

import com.pulse.coloredcauldron.CCInstance.configManager
import com.pulse.coloredcauldron.CCInstance.foliaLib
import com.pulse.coloredcauldron.config.ConfigKeys.ANIM_DELAY
import com.pulse.coloredcauldron.config.ConfigKeys.ANIM_ENABLED
import com.pulse.coloredcauldron.config.ConfigKeys.DYE_ITEMS_ENABLED
import com.pulse.coloredcauldron.config.ConfigKeys.SPLASH_SOUND
import com.pulse.coloredcauldron.config.ConfigKeys.DYE_WATER_ENABLED
import com.pulse.coloredcauldron.config.ConfigKeys.EMPTYING_SOUND
import com.pulse.coloredcauldron.config.ConfigKeys.FILL_SOUND
import com.pulse.coloredcauldron.config.ConfigKeys.POTION_ENABLED
import com.pulse.coloredcauldron.logic.DyeUtil
import com.pulse.coloredcauldron.logic.Util
import com.pulse.coloredcauldron.logic.Util.getShift
import com.pulse.coloredcauldron.logic.Util.playConfigSound
import com.pulse.coloredcauldron.logic.DyeUtil.dyeColor
import com.pulse.coloredcauldron.water.WCManager
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class CauldronHandler(private val plugin: JavaPlugin) : Listener {

    fun register() = Bukkit.getPluginManager().registerEvents(this, plugin)

    @EventHandler
    fun onCauldronPlace(e: BlockPlaceEvent) {
        if (e.block.type == Material.CAULDRON) WCManager.spawn(e.block.location)
    }

    @EventHandler
    fun onCauldronRemove(e: BlockBreakEvent) {
        if (WCManager.get(e.block.location) != null)
            WCManager.remove(e.block.location)
    }

    @EventHandler
    fun onCauldronClick(e: PlayerInteractEvent) {
        val player = e.player.takeIf { it.gameMode != GameMode.SPECTATOR } ?: return
        val block = e.clickedBlock?.takeIf { e.action == Action.RIGHT_CLICK_BLOCK && (it.type == Material.WATER_CAULDRON || it.type == Material.CAULDRON) } ?: return
        val item = e.item ?: return

        val wc = WCManager.get(block.location) ?: return
        wc.update()

        when (item.type) {
            in DyeUtil.dyeColors.keys -> {
                if (!configManager.getBoolean(DYE_WATER_ENABLED) || wc.level == 0) return

                e.isCancelled = true
                player.swingMainHand()
                player.playConfigSound(FILL_SOUND)

                if (player.gameMode != GameMode.CREATIVE) player.inventory.removeItem(ItemStack(item.type, 1))

                if (configManager.getBoolean(ANIM_ENABLED)) {
                    val dropped = block.world.dropItem(block.location.clone().add(0.5, plugin.config.getInt("DyeAnimation.height").toDouble(), 0.5), ItemStack(item.type)).apply {
                        pickupDelay = Int.MAX_VALUE
                        velocity = Vector(0, 0, 0)
                    }

                    Util.runLater(plugin.config.getInt(ANIM_DELAY).toLong()) {
                        player.playConfigSound(SPLASH_SOUND)

                        foliaLib.scheduler.runAtLocation(block.location) {
                            dropped.remove()
                            wc.mix(item.type.dyeColor()!!)

                            block.world.spawnParticle(
                                Particle.DUST,
                                block.location.clone().add(0.5, 0.95, 0.5),
                                7,
                                0.15,
                                0.15,
                                0.15,
                                Particle.DustOptions(wc.color, 0.95f)
                            )
                        }
                    }

                } else wc.mix(item.type.dyeColor()!!)
            }

            Material.GLASS_BOTTLE -> {
                if (!plugin.config.getBoolean(POTION_ENABLED) || wc.level == 0) return

                e.isCancelled = true
                player.swingMainHand()
                player.playConfigSound(EMPTYING_SOUND)
                wc.level -= 1

                val potion = DyeUtil.getColoredPotion(wc.color)
                if (player.inventory.itemInMainHand.amount > 1 || player.gameMode == GameMode.CREATIVE) {
                    player.inventory.addItem(potion)
                    if (player.gameMode != GameMode.CREATIVE) player.inventory.removeItem(ItemStack(Material.GLASS_BOTTLE, 1))
                } else player.inventory.setItem(player.inventory.heldItemSlot, potion)
            }

            Material.POTION -> {
                if (!plugin.config.getBoolean(POTION_ENABLED) || wc.level == 3) return

                e.isCancelled = true
                player.swingMainHand()
                player.playConfigSound(FILL_SOUND)

                wc.mix(DyeUtil.getPotionColor(item))
                wc.level += 1

                if (player.gameMode != GameMode.CREATIVE) player.inventory.setItem(player.inventory.heldItemSlot, ItemStack(Material.GLASS_BOTTLE))
            }

            Material.LEATHER_HORSE_ARMOR -> {
                if (!plugin.config.getBoolean(DYE_ITEMS_ENABLED) || wc.level == 0) return

                e.isCancelled = true
                player.swingMainHand()
                player.playConfigSound(EMPTYING_SOUND)
                wc.level -= 1

                player.inventory.setItem(player.inventory.heldItemSlot, DyeUtil.adjustItemColor(wc.color, item))
            }

            Material.BUCKET -> {
                if (block.type != Material.WATER_CAULDRON) return
                wc.level = 0
            }

            else -> {}
        }
    }

    @EventHandler
    fun onCauldronExtend(e: BlockPistonExtendEvent) {
        e.blocks
            .filter { it.type == Material.CAULDRON || it.type == Material.WATER_CAULDRON }
            .filter { WCManager.get(it.location) != null }
            .forEach { block ->
                val wc = WCManager.get(block.location) ?: return@forEach

                val color = wc.color
                val level = wc.level

                val newLocation = block.getShift(e.direction)
                WCManager.remove(block.location)

                WCManager.spawn(newLocation)
                WCManager.get(newLocation)?.apply {
                    this.color = color
                    this.level = level
                }
            }
    }

    @EventHandler
    fun onCauldronRetract(e: BlockPistonRetractEvent) {
        e.blocks
            .filter { it.type == Material.CAULDRON || it.type == Material.WATER_CAULDRON }
            .filter { WCManager.get(it.location) != null }
            .forEach { block ->
                val wc = WCManager.get(block.location) ?: return@forEach

                val color = wc.color
                val level = wc.level

                val newLocation = block.getShift(e.direction)
                WCManager.remove(block.location)

                WCManager.spawn(newLocation)
                WCManager.get(newLocation)?.apply {
                    this.color = color
                    this.level = level
                }
            }
    }
}
