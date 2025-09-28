package com.pulse.coloredcauldron.config

import com.pulse.coloredcauldron.CCInstance.plugin
import com.pulse.coloredcauldron.logic.Util
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class LangManager() {

    private val langFile = File(plugin.dataFolder, "lang.yml")
    private var langYaml: YamlConfiguration = YamlConfiguration.loadConfiguration(langFile)

    fun reload() {
        langYaml = Util.reloadYaml(langFile, "lang.yml")
    }

    fun getMessage(
        path: String,
        vararg placeholders: Pair<String, Component>
    ): Component {
        val rawMessage = langYaml.getString("messages.$path") ?: "<red>Message not found: messages.$path"

        return MiniMessage.miniMessage().deserialize(
            rawMessage,
            TagResolver.resolver(
                placeholders.map { (key, value) ->
                    Placeholder.component(key, value)
                }
            )
        )
    }
}