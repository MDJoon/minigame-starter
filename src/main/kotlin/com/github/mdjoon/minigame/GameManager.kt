package com.github.mdjoon.minigame

import org.bukkit.Location
import org.bukkit.entity.Player

object GameManager {
    val games = mutableListOf<Game>()

    const val limit = 10

    lateinit var snowLocation : Location

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
}