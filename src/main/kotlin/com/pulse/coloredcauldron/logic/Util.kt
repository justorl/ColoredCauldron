package com.pulse.coloredcauldron.logic

import com.pulse.coloredcauldron.CCInstance.foliaLib
import com.pulse.coloredcauldron.CCInstance.plugin
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object Util {
    fun reloadYaml(file: File, name: String): YamlConfiguration {
        if (!file.exists()) {
            plugin.saveResource(name, false)
        }

        val userYaml = YamlConfiguration.loadConfiguration(file)

        val defaultYaml = YamlConfiguration.loadConfiguration(
            plugin.getResource(name)?.reader() ?: return userYaml
        )

        defaultYaml.getKeys(true)
            .filterNot { userYaml.contains(it) }
            .forEach { key -> userYaml.set(key, defaultYaml.get(key)) }

        userYaml.save(file)
        return userYaml
    }
    
    fun runLater(delay: Long, task: () -> Unit) {
        foliaLib.scheduler.runLater(task, delay)
    }

    fun Block.getShift(face: BlockFace): Location {
        return this.location.add(face.direction)
    }

    fun Player.playConfigSound(path: String) {
        val soundKey = plugin.config.getString(path) ?: return

        this.playSound(Sound.sound(Key.key(soundKey), Sound.Source.MASTER, 1f, 1f))
    }

    fun hex2rgb(hex: String, alpha: Int): Color {
        val clean = hex.removePrefix("#")
        val r = clean.substring(0, 2).toInt(16)
        val g = clean.substring(2, 4).toInt(16)
        val b = clean.substring(4, 6).toInt(16)

        return Color.fromARGB(alpha, r, g, b)
    }
}
