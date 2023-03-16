package com.github.mdjoon.minigame

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

class Game : Runnable, Listener {
    val players = mutableListOf<Player>()
    var isProcessing = false
    var isFull = false

    var taskId = 0

    fun join(player: Player) {
        players.add(player)

        if(players.size == GameManager.limit) {
            isFull = true
        }
    }

    fun start() {
        isProcessing = true
        players.forEach {
            it.teleport(GameManager.snowLocation)
            it.sendMessage("시작!")
        }
    }

    private fun stop() {
        isProcessing = false
        players.clear()

        Bukkit.getScheduler().cancelTask(taskId)

        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)

        GameManager.games.remove(this)
    }

    private fun removePlayer(player: Player) {
        players.remove(player)

        if(players.size < GameManager.limit) {
            isFull = false
        }
    }

    override fun run() {
        if(players.size == 1) {
            players.first().sendMessage("1st")
            stop()
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player

        if(isProcessing && players.contains(player)) {
            removePlayer(player)
            player.sendMessage("당신은 사망하셨습니다!")
            players.forEach {
                it.sendMessage(Component.text("${player.name}님이 사망하셨습니다!").color(TextColor.color(0xFF5555)))
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if(players.contains(player)) {
            removePlayer(player)
            event.quitMessage(Component.text("${player.name}님이 퇴장하셨습니다!").color(TextColor.color(0xFF5555)))
        }
    }

}