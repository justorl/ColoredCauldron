package com.pulse.coloredcauldron

import com.pulse.coloredcauldron.config.ConfigManager
import com.pulse.coloredcauldron.config.LangManager
import com.pulse.coloredcauldron.config.WaterStorage
import com.tcoded.folialib.FoliaLib

object CCInstance {
    lateinit var plugin: ColoredCauldron
    lateinit var foliaLib: FoliaLib
    lateinit var waterStorage: WaterStorage
    lateinit var configManager: ConfigManager
    lateinit var langManager: LangManager
}