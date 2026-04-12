package com.pulse.coloredcauldron.config

import com.pulse.utilslib.paper.plugin.PluginContext.plugin

class ConfigGetter {
    val cache = HashMap<String, Any>()

    @Suppress("UNCHECKED_CAST")
    fun <T> get(path: String, loader: (String) -> T): T {
        return cache.getOrPut(path) {
            loader(path) as Any
        } as T
    }

    fun set(path: String, value: Any) {
        cache[path] = value
        plugin.config.set(path, value)
    }
}