package com.pulse.coloredcauldron.listener

import com.pulse.coloredcauldron.config.Config
import com.pulse.coloredcauldron.config.Config.configSound
import com.pulse.coloredcauldron.utils.DyeUtil
import com.pulse.coloredcauldron.utils.DyeUtil.dyeColor
import com.pulse.coloredcauldron.water.CauldronManager
import com.pulse.utilslib.core.extension.rateLimit
import com.pulse.utilslib.paper.extension.getShift
import com.pulse.utilslib.paper.extension.runLater
import com.pulse.utilslib.paper.listener.auto.AutoListener
import com.pulse.utilslib.paper.plugin.PluginContext.foliaLib
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class CauldronListener : AutoListener() {

    @EventHandler
    fun onCauldronPlace(e: BlockPlaceEvent) {
        if (e.block.type == Material.CAULDRON)
            CauldronManager.spawn(e.block.location)
    }

    @EventHandler
    fun onCauldronRemove(e: BlockBreakEvent) {
        val cauldron = CauldronManager.get(e.block.location) ?: return
        cauldron.remove()
    }

    @EventHandler
    fun onCauldronClick(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        val player = e.player

        if (player.gameMode == GameMode.SPECTATOR ||
            player.gameMode == GameMode.ADVENTURE) return

        val block = e.clickedBlock ?: return
        val item = e.item ?: return

        val cauldron = CauldronManager.get(block.location) ?: return

        if (!rateLimit("cauldron_use:${player.uniqueId}", 100)) return

        when (item.type) {
            in DyeUtil.dyeColors.keys -> {
                if (!Config.DyeWaterMechanic.enabled || cauldron.level == 0) return

                e.isCancelled = true
                player.swingMainHand()
                player.configSound(Config.Sounds.fill)

                if (player.gameMode != GameMode.CREATIVE) player.inventory.removeItem(
                    ItemStack(item.type, 1)
                )

                if (Config.DyeWaterMechanic.animEnabled) {
                    val dropped = block.world.dropItem(
                        block.location.clone().add(0.5, Config.DyeWaterMechanic.animHeight, 0.5),
                        ItemStack(item.type)
                    ).apply {
                        pickupDelay = Int.MAX_VALUE
                        velocity = Vector(0, 0, 0)
                    }

                    runLater(Config.DyeWaterMechanic.animDelay.toLong()) {
                        player.configSound(Config.Sounds.splash)

                        foliaLib.scheduler.runAtLocation(block.location) {
                            dropped.remove()
                            cauldron.mix(item.type.dyeColor()!!)

                            block.world.spawnParticle(
                                Particle.DUST,
                                block.location.clone().add(0.5, 0.95, 0.5),
                                7,
                                0.15,
                                0.15,
                                0.15,
                                Particle.DustOptions(cauldron.color, 0.95f)
                            )
                        }
                    }

                } else cauldron.mix(item.type.dyeColor()!!)
            }

            Material.GLASS_BOTTLE -> {
                if (!Config.ColoredPotionMechanic.enabled || cauldron.level == 0) return

                e.isCancelled = true
                player.swingMainHand()
                player.configSound(Config.Sounds.emptying)
                cauldron.level -= 1

                val potion = DyeUtil.getColoredPotion(cauldron.color)
                if (player.inventory.itemInMainHand.amount > 1 || player.gameMode == GameMode.CREATIVE) {
                    player.inventory.addItem(potion)
                    if (player.gameMode != GameMode.CREATIVE) player.inventory.removeItem(
                        ItemStack(Material.GLASS_BOTTLE, 1)
                    )
                } else player.inventory.setItem(player.inventory.heldItemSlot, potion)
            }

            Material.POTION -> {
                if (!Config.ColoredPotionMechanic.enabled || cauldron.level == 3) return

                e.isCancelled = true
                player.swingMainHand()
                player.configSound(Config.Sounds.fill)

                cauldron.mix(DyeUtil.getPotionColor(item))
                cauldron.level += 1

                if (player.gameMode != GameMode.CREATIVE) player.inventory.setItem(
                    player.inventory.heldItemSlot, ItemStack(Material.GLASS_BOTTLE)
                )
            }

            Material.LEATHER_HORSE_ARMOR -> {
                if (!Config.DyeItemsMechanic.enabled || cauldron.level == 0) return

                e.isCancelled = true
                player.swingMainHand()
                player.configSound(Config.Sounds.emptying)
                cauldron.level -= 1

                player.inventory.setItem(
                    player.inventory.heldItemSlot,
                    DyeUtil.adjustItemColor(cauldron.color, item)
                )
            }

            Material.BUCKET -> {
                if (block.type != Material.WATER_CAULDRON) return
                cauldron.level = 0
            }

            else -> return
        }
    }

    @EventHandler
    fun onCauldronExtend(e: BlockPistonExtendEvent) {
        e.blocks.forEach { block ->
            val cauldron = CauldronManager.get(block.location)
                ?: return@forEach

            val newLoc = block.getShift(e.direction)
            cauldron.move(newLoc)
        }
    }

    @EventHandler
    fun onCauldronRetract(e: BlockPistonRetractEvent) {
        e.blocks.forEach { block ->
            val cauldron = CauldronManager.get(block.location)
                ?: return@forEach

            val newLoc = block.getShift(e.direction)
            cauldron.move(newLoc)
        }
    }
}
