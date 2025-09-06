package com.pulse.coloredCauldron.config

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ConfigManager(private val plugin: JavaPlugin) {
    private val configFile = File(plugin.dataFolder, "config.yml")

    fun reload() {
        if (!configFile.exists()) {
            plugin.saveDefaultConfig()
        }

        plugin.reloadConfig()
    }
}