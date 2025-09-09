package com.pulse.coloredCauldron

import com.pulse.coloredCauldron.commands.CoreCommand
import com.pulse.coloredCauldron.config.ConfigManager
import com.pulse.coloredCauldron.config.LangManager
import com.pulse.coloredCauldron.handlers.CauldronHandler
import com.pulse.coloredCauldron.config.WaterStorage
import com.tcoded.folialib.FoliaLib
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class ColoredCauldron : JavaPlugin() {
    lateinit var configManager: ConfigManager
        private set

    lateinit var langManager: LangManager
        private set

    lateinit var waterStorage: WaterStorage
        private set

    override fun onEnable() {
        CommandAPI.onEnable()

        // lateinit
        CCInstance.plugin = this
        CCInstance.foliaLib = FoliaLib(this)

        configManager = ConfigManager(this)
        langManager = LangManager(this)
        waterStorage = WaterStorage(this)

        // configs
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