package com.github.mdjoon.minigame

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.FileUtil
import org.codehaus.plexus.util.FileUtils

class Game : Runnable, Listener {
    private lateinit var gameWorld: World
    private lateinit var spawnLocation: Location

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
        val copiedWorld = FileManager.copyWorld(GameManager.snowLocation.world, taskId.toString())
        gameWorld = copiedWorld

        spawnLocation = GameManager.snowLocation.clone().apply {
            world = gameWorld
        }

        val timer = Timer()
        val task = Bukkit.getScheduler().runTaskTimer(GameManager.plugin, timer, 0L, 1L)

        timer.taskId = task.taskId
    }

    private fun stop() {
        isProcessing = false
        Bukkit.getScheduler().cancelTask(taskId)

        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)

        players.forEach {
            val location = GameManager.spawnWorld.spawnLocation
            it.teleport(location)
        }

        Bukkit.unloadWorld(gameWorld, false)
        Bukkit.getWorlds().remove(gameWorld)
        FileUtils.deleteDirectory(gameWorld.worldFolder)

        players.clear()
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
        player.teleport(Bukkit.getWorld("world")?.spawnLocation!!)
    }

    inner class Timer : Runnable {
        var tick = 10 * 20
        var taskId = 0

        override fun run() {
            if(tick == 10 * 20) {
                players.forEach {
                    it.sendMessage("10초후 게임이 시작됩니다...")
                }
            }

            if(tick < 5 * 20 && tick % 20 == 0) {
                players.forEach {
                    it.sendMessage("[@] ${ChatColor.RED}게임 시작까지 ${ChatColor.YELLOW}${tick / 20 + 1}${ChatColor.RED}초 남았습니다!")
                }
            }

            if(tick < 0) {
                players.forEach {
                    it.teleport(spawnLocation)
                    it.sendMessage("시작!")
                }

                Bukkit.getScheduler().cancelTask(taskId)
            }

            tick--
        }
    }

}