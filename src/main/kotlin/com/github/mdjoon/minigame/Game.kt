package com.github.mdjoon.minigame

import org.bukkit.entity.Player

class Game {
    val players = mutableListOf<Player>()
    var isStarted = false
    var isFull = false

    fun join(player: Player) {
        players.add(player)

        if(players.size == GameManager.limit) {
            isFull = true
        }
    }

    fun start() {
        isStarted = true
        players.forEach {
            it.teleport(GameManager.snowLocation)
            it.sendMessage("시작!")
        }
    }

    fun stop() {
        isStarted = false
        players.clear()
    }

    fun removePlayer(player: Player) {
        players.remove(player)

        if(players.size < GameManager.limit) {
            isFull = false
        }
    }


}