package com.pulse.coloredCauldron.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class LangManager(private val plugin: JavaPlugin) {
    private val langFile = File(plugin.dataFolder, "lang.yml")
    private var langConfig: YamlConfiguration = loadLang()
    private val miniMessage = MiniMessage.miniMessage()

    private fun loadLang(): YamlConfiguration {
        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false)
        }

        val defaultConfig = plugin.getResource("lang.yml")
            ?.reader(Charsets.UTF_8)
            ?.use { YamlConfiguration.loadConfiguration(it) }

        val userConfig = YamlConfiguration.loadConfiguration(langFile)

        if (defaultConfig != null) {
            userConfig.setDefaults(defaultConfig)
            userConfig.options().copyDefaults(true)
            save(userConfig)
        }

        return userConfig
    }

    private fun save(config: YamlConfiguration) {
        try {
            config.save(langFile)
        } catch (ex: IOException) {
            plugin.logger.severe("Can't save lang.yml: ${ex.message}")
        }
    }

    fun reload() {
        langConfig = loadLang()
    }

    fun getMessage(path: String, vararg placeholders: Pair<String, Component>): Component =
        miniMessage.deserialize(
            langConfig.getString("messages.$path") ?: "<red>Message not found: $path",
            TagResolver.resolver(
                placeholders.map { (key, value) ->
                    Placeholder.component(key, value)
                }
            )
        )
}