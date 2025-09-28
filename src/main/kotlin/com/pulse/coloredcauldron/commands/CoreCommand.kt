package com.pulse.coloredcauldron.commands

import com.pulse.coloredcauldron.CCInstance.configManager
import com.pulse.coloredcauldron.CCInstance.langManager
import com.pulse.coloredcauldron.CCInstance.plugin
import com.pulse.coloredcauldron.CCInstance.waterStorage
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
                        configManager.reload()
                        langManager.reload()
                        waterStorage.reload()

                        sender.sendMessage(langManager.getMessage("reload"))
                    })
            )
            .withSubcommand(
                CommandAPICommand("reset")
                    .withPermission("cc.command.reset")
                    .executes(CommandExecutor { sender, _ ->
                        waterStorage.reset()

                        sender.sendMessage(langManager.getMessage("reset"))
                    })
            )
            .withSubcommand(
                CommandAPICommand("version")
                    .withPermission("cc.command.version")
                    .executes(CommandExecutor { sender, _ ->
                        sender.sendMessage(langManager.getMessage("version",
                            "version" to Component.text(plugin.pluginMeta.version)
                        ))
                    })
            )
            .register()
    }
}