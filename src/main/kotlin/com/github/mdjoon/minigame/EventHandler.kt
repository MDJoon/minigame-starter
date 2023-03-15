package com.github.mdjoon.minigame

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class EventHandler : Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val game = GameManager.getGame(player)

        game?.removePlayer(player)
    }
}