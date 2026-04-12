package com.pulse.coloredcauldron

import com.jeff_media.customblockdata.CustomBlockData
import com.pulse.coloredcauldron.config.Config
import com.pulse.coloredcauldron.config.LangManager
import com.pulse.utilslib.paper.plugin.PaperPlugin

class ColoredCauldron : PaperPlugin("coloredcauldron") {
    override fun enable() {
        Config.reload()
        LangManager.reload()
        CustomBlockData.registerListener(this)
    }
}