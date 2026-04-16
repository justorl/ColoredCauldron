package com.pulse.coloredcauldron.config

import com.pulse.coloredcauldron.utils.hex2rgb
import com.pulse.utilslib.paper.plugin.PluginContext.plugin
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Color
import org.bukkit.entity.Player

object Config {

    val getter = ConfigGetter()

    fun <T> get(path: String, loader: (String) -> T) = getter.get(path, loader)
    fun set(path: String, value: Any) = getter.set(path, value)

    object DyeWaterMechanic {
        var enabled: Boolean
            get() = get("DyeWaterMechanic.enabled") { plugin.config.getBoolean(it) }
            set(v) = set("DyeWaterMechanic.enabled", v)

        var animEnabled: Boolean
            get() = get("DyeWaterMechanic.animation.enabled") { plugin.config.getBoolean(it) }
            set(v) = set("DyeWaterMechanic.animation.enabled", v)

        var animDelay: Int
            get() = get("DyeWaterMechanic.animation.delay") { plugin.config.getInt(it) }
            set(v) = set("DyeWaterMechanic.animation.delay", v)

        var animHeight: Double
            get() = get("DyeWaterMechanic.animation.height") { plugin.config.getDouble(it) }
            set(v) = set("DyeWaterMechanic.animation.delay", v)
    }

    object ColoredPotionMechanic {
        var enabled: Boolean
            get() = get("ColoredPotionMechanic.enabled") { plugin.config.getBoolean(it) }
            set(v) = set("ColoredPotionMechanic.enabled", v)
    }

    object DyeItemsMechanic {
        var enabled: Boolean
            get() = get("DyeItemsMechanic.enabled") { plugin.config.getBoolean(it) }
            set(v) = set("DyeItemsMechanic.enabled", v)
    }

    object Sounds {
        var splash: String
            get() = get("Sounds.splash-sound") { plugin.config.getString(it) ?: "" }
            set(v) = set("Sounds.splash-sound", v)

        var emptying: String
            get() = get("Sounds.emptying-sound") { plugin.config.getString(it) ?: "" }
            set(v) = set("Sounds.emptying-sound", v)

        var fill: String
            get() = get("Sounds.fill-sound") { plugin.config.getString(it) ?: "" }
            set(v) = set("Sounds.fill-sound", v)
    }

    object ColoredWater {
        var defaultColor: Color
            get() = get("ColoredWater.default-color") {
                hex2rgb(plugin.config.getString(it) ?: "#FFFFFF", 255)
            }
            set(v) = set("ColoredWater.default-color", v)

        var alpha: Int
            get() = get("ColoredWater.alpha") { plugin.config.getInt(it) }
            set(v) = set("ColoredWater.alpha", v)

        var rateLimit: Int
            get() = get("ColoredWater.ratelimit") { plugin.config.getInt(it) }
            set(v) = set("ColoredWater.ratelimit", v)
    }

    fun Player.configSound(key: String) = playSound(Sound.sound(
        Key.key(key),
        Sound.Source.MASTER,
        1f,
        1f
    ))

    fun save() = plugin.saveConfig()

    fun reload() {
        plugin.reloadConfig()
        getter.cache.clear()
    }

    fun init() {
        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()
        plugin.reloadConfig()

        getter.cache.clear()
    }
}