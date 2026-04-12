package com.pulse.coloredcauldron.config

import com.pulse.utilslib.paper.extension.reloadYaml
import com.pulse.utilslib.paper.plugin.PluginContext
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object LangManager {

    val langFile = File(PluginContext.plugin.dataFolder, "lang.yml")
    var langYaml = YamlConfiguration.loadConfiguration(langFile)

    fun reload() {
        langYaml = reloadYaml(PluginContext.plugin, langFile, "lang.yml")
    }

    class PlaceholderBuilder {
        val list = mutableListOf<Pair<String, Any>>()

        infix fun String.by(value: Any) {
            list.add(this to value)
        }
    }

    fun Audience.send(
        path: String,
        block: PlaceholderBuilder.() -> Unit = {}
    ) {
        val builder = PlaceholderBuilder().apply(block)
        sendMessage(getMessage(path, *builder.list.toTypedArray()))
    }

    fun Audience.sendAction(
        path: String,
        block: PlaceholderBuilder.() -> Unit = {}
    ) {
        val builder = PlaceholderBuilder().apply(block)
        sendActionBar(getMessage(path, *builder.list.toTypedArray()))
    }

    fun getMessage(
        path: String,
        vararg placeholders: Pair<String, Any>
    ): Component {
        var rawMessage = langYaml.getString(path)
            ?: "<red>Message is not found: $path"

        val resolvers = mutableListOf<TagResolver.Single>()

        placeholders.forEach { (key, value) ->
            when (value) {

                is ComponentLike -> resolvers.add(Placeholder.component(key, value))

                is String -> {
                    if (key.startsWith("_") && key.endsWith("_"))
                        rawMessage = rawMessage.replace(key, value)
                    else
                        resolvers.add(Placeholder.unparsed(key, value))
                }

                else -> error("Unsupported placeholder type for '$key': ${value::class}")
            }
        }

        return MiniMessage.miniMessage().deserialize(
            rawMessage,
            TagResolver.resolver(resolvers)
        )
    }
}