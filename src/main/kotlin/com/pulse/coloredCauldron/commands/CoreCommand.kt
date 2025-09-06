package com.pulse.coloredCauldron.commands

import com.pulse.coloredCauldron.CCInstance.plugin
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.Component

object CoreCommand {
    fun register() {
        registerCoreCommand()

    }

    private fun registerCoreCommand() {
        CommandAPICommand("coloredcauldron")
            .withAliases("cc", "cauldron")
            .withSubcommand(
                CommandAPICommand("reload")
                    .withPermission("cc.command.reload")
                    .executes(CommandExecutor { sender, _ ->
                        plugin.configManager.reload()
                        plugin.langManager.reload()
                        plugin.waterStorage.reload()

                        sender.sendMessage(plugin.langManager.getMessage("reload"))
                    })
            )
            .withSubcommand(
                CommandAPICommand("reset")
                    .withPermission("cc.command.reset")
                    .executes(CommandExecutor { sender, _ ->
                        plugin.waterStorage.resetWater()

                        sender.sendMessage(plugin.langManager.getMessage("reset"))
                    })
            )
            .withSubcommand(
                CommandAPICommand("version")
                    .withPermission("cc.command.version")
                    .executes(CommandExecutor { sender, _ ->
                        plugin.waterStorage.resetWater()

                        sender.sendMessage(plugin.langManager.getMessage("version",
                            "version" to Component.text(plugin.pluginMeta.version)
                        ))
                    })
            )
            .register()
    }
}