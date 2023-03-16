package com.github.mdjoon.minigame

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object GameManager {
    val games = mutableListOf<Game>()
    lateinit var plugin: JavaPlugin

    var limit : Int
        get() {
            return FileManager.get("limit") as Int
        }
        set(value) {
            FileManager.write("limit", value)
        }

    var snowLocation : Location
        get() {
            return FileManager.getLocation("location")!!
        }
        set(value) {
            FileManager.writeLocation("location", value)
        }

    fun isInPlayer(player: Player) : Boolean {
        games.forEach {
            val bool = it.players.contains(player)
            if(bool) {
                return bool
            }
        }

        return false
    }

    fun getGame(player: Player) : Game? {
        games.forEach {
            if(it.players.contains(player)) {
                return it
            }
        }

        return null
    }

    fun addGame() : Game{
        val game = Game()
        games.add(game)
        return game
    }

    fun startGame(game: Game) {
        game.start()
        Bukkit.getPluginManager().registerEvents(game, plugin)
        val task = Bukkit.getScheduler().runTaskTimer(plugin, game, 0L, 1L)

        game.taskId = task.taskId
    }

}