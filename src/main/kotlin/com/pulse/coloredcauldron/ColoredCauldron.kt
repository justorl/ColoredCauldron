package com.pulse.coloredcauldron

import com.pulse.coloredcauldron.CCInstance.configManager
import com.pulse.coloredcauldron.CCInstance.langManager
import com.pulse.coloredcauldron.CCInstance.waterStorage
import com.pulse.coloredcauldron.commands.CoreCommand
import com.pulse.coloredcauldron.config.ConfigManager
import com.pulse.coloredcauldron.config.LangManager
import com.pulse.coloredcauldron.handlers.CauldronHandler
import com.pulse.coloredcauldron.config.WaterStorage
import com.tcoded.folialib.FoliaLib
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class ColoredCauldron : JavaPlugin() {

    override fun onEnable() {
        // api
        CommandAPI.onEnable()

        // lateinit
        CCInstance.plugin = this
        CCInstance.foliaLib = FoliaLib(this)

        // configs
        configManager = ConfigManager()
        langManager = LangManager()
        waterStorage = WaterStorage()

        configManager.reload()
        langManager.reload()
        waterStorage.reload()

        // events
        val cauldronHandler = CauldronHandler(this)
        cauldronHandler.register()

        // commands
        CoreCommand.register()
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true).setNamespace("cc").verboseOutput(true))
    }

    override fun onDisable() {
        CommandAPI.onDisable()
        CCInstance.foliaLib.scheduler.cancelAllTasks();
    }
}