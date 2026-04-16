package com.pulse.coloredcauldron.commands

import com.pulse.coloredcauldron.config.Config
import com.pulse.coloredcauldron.config.LangManager
import com.pulse.coloredcauldron.config.LangManager.send
import com.pulse.utilslib.adventure.extension.cmp
import com.pulse.utilslib.paper.command.auto.AutoCommand
import com.pulse.utilslib.paper.plugin.PluginContext
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument

class CoreCommand: AutoCommand() {
    override fun register() {
        commandTree("coloredcauldron") {
            withAliases("cc", "cauldron")
            literalArgument("reload") {
                anyExecutor { sender, args ->
                    Config.reload()
                    LangManager.reload()

                    sender.send("messages.reload")
                }
            }

            literalArgument("version") {
                anyExecutor { sender, args ->
                    sender.send("messages.version") {
                        "version" by PluginContext.plugin.pluginMeta.version.cmp()
                    }
                }
            }
        }
    }
}