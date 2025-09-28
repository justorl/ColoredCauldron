package com.pulse.coloredcauldron.config

import com.pulse.coloredcauldron.CCInstance.plugin
import com.pulse.coloredcauldron.logic.Util
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigManager() {

    private val configFile = File(plugin.dataFolder, "config.yml")
    private var configYaml: YamlConfiguration = YamlConfiguration.loadConfiguration(configFile)

    fun reload() {
        configYaml = Util.reloadYaml(configFile, "config.yml")
    }

    fun getBoolean(key: String) : Boolean {
        return configYaml.getBoolean(key)
    }

    fun getInt(key: String) : Int {
        return configYaml.getInt(key)
    }

    fun getString(key: String) : String {
        return configYaml.getString(key) ?: ""
    }
}