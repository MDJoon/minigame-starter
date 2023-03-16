package com.github.mdjoon.minigame

import io.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin

class GamePlugin : JavaPlugin() {

    override fun onEnable() {
        kommand {
            Command.register(this)
        }

        saveDefaultConfig()
        GameManager.plugin = this
    }
}