package com.pulse.coloredCauldron.handlers

import com.pulse.coloredCauldron.logic.Util
import com.pulse.coloredCauldron.logic.Util.getShift
import com.pulse.coloredCauldron.logic.WCManager
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
        e.block.takeIf { it.type == Material.CAULDRON }?.let { WCManager.spawn(it.location) }
    }

    @EventHandler
    fun onCauldronClick(e: PlayerInteractEvent) {
        val player = e.player.takeIf { it.gameMode != GameMode.SPECTATOR } ?: return
        val block = e.clickedBlock?.takeIf { e.action == Action.RIGHT_CLICK_BLOCK && (it.type == Material.WATER_CAULDRON || it.type == Material.CAULDRON) } ?: return
        val wc = WCManager.get(block.location) ?: return
        val item = e.item ?: return

        when (item.type) {
            in Util.dyeColors.keys -> {
                e.isCancelled = true
                player.swingMainHand()
                Util.playConfigSound(player, "ColoredWater.sound")

                if (player.gameMode != GameMode.CREATIVE) player.inventory.removeItem(ItemStack(item.type, 1))
                if (plugin.config.getBoolean("DyeAnimation.enabled")) {
                    val dropped = block.world.dropItem(block.location.add(0.5, plugin.config.getInt("DyeAnimation.height").toDouble(), 0.5), ItemStack(item.type)).apply {
                        pickupDelay = Int.MAX_VALUE
                        velocity = Vector(0, 0, 0)
                    }

                    Util.runLater(plugin.config.getInt("DyeAnimation.delay").toLong()) {
                        dropped.remove()
                        WCManager.get(block.location)?.let { updated ->
                            updated.mix(Util.dyeColors[item.type]!!)
                            Util.playConfigSound(player, "DyeAnimation.sound")

                            block.world.spawnParticle(
                                Particle.DUST,
                                block.location.add(0.5, 0.95, 0.5),
                                7,
                                0.15,
                                0.15,
                                0.15,
                                Particle.DustOptions(updated.color, 0.95f)
                            )
                        }
                    }

                } else wc.mix(Util.dyeColors[item.type]!!)
            }

            Material.GLASS_BOTTLE -> {
                if (!plugin.config.getBoolean("ColoredPotionMechanic.enabled") || wc.level == 0) return

                e.isCancelled = true
                player.swingMainHand()

                Util.playConfigSound(player, "ColoredPotionMechanic.sound")
                wc.level -= 1

                val potion = Util.getColoredPotion(wc.color)
                if (player.inventory.itemInMainHand.amount > 1 || player.gameMode == GameMode.CREATIVE) {
                    player.inventory.addItem(potion)
                    if (player.gameMode != GameMode.CREATIVE) player.inventory.removeItem(ItemStack(Material.GLASS_BOTTLE, 1))
                } else player.inventory.setItem(player.inventory.heldItemSlot, potion)
            }

            Material.POTION -> {
                if (!plugin.config.getBoolean("ColoredPotionMechanic.enabled") || wc.level == 3) return

                e.isCancelled = true
                player.swingMainHand()

                Util.playConfigSound(player, "ColoredWater.sound")

                wc.mix(Util.getPotionColor(item))
                wc.level += 1

                if (player.gameMode != GameMode.CREATIVE) player.inventory.setItem(player.inventory.heldItemSlot, ItemStack(Material.GLASS_BOTTLE))
            }

            Material.LEATHER_HORSE_ARMOR -> {
                if (!plugin.config.getBoolean("DyeItemsMechanic.enabled") || wc.level == 0) return

                e.isCancelled = true
                player.swingMainHand()

                Util.playConfigSound(player, "ColoredPotionMechanic.sound")
                wc.level -= 1

                player.inventory.setItem(player.inventory.heldItemSlot, Util.adjustItemColor(wc.color, item))
            }

            Material.WATER_BUCKET -> {
                if (block.type != Material.CAULDRON) return
                wc.update()
            }

            Material.BUCKET -> {
                if (block.type != Material.WATER_CAULDRON) return
                wc.level = 0
            }
            else -> {}
        }
    }

    @EventHandler
    fun onCauldronRemove(e: BlockBreakEvent) {
        e.block.takeIf {
            (it.type == Material.WATER_CAULDRON || it.type == Material.CAULDRON) && WCManager.get(it.location) != null }?.let {
            WCManager.remove(it.location)
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
