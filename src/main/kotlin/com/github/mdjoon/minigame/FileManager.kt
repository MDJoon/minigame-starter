package com.github.mdjoon.minigame

import org.bukkit.Bukkit
import org.bukkit.Location

object FileManager {
    private val plugin = GameManager.plugin
    private val config = plugin.config

    fun write(key : String, value : Any) {
        config.set(key, value)
        config.options().copyDefaults(true)
        plugin.saveConfig()
    }

    fun get(key: String) : Any?{
        return config.get(key)
    }

    fun writeLocation(key: String, location: Location) {
        val world = location.world
        val x = location.x
        val y = location.y
        val z = location.z

        write(key, "${world.name},$x,$y,$z")
    }

    fun getLocation(key: String) : Location? {
        val string = config.getString(key)
        val list = string?.split(",")

        if(list != null) {
            val world = Bukkit.getWorld(list[0])
            return Location(world, list[1].toDouble(), list[2].toDouble(), list[3].toDouble())
        }

        return null
    }
}