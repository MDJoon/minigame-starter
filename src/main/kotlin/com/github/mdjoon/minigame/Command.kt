package com.github.mdjoon.minigame

import io.github.monun.kommand.PluginKommand
import org.bukkit.Bukkit

object Command {
    fun register(plugin: PluginKommand) {
        plugin.register("game") {
            requires { playerOrNull != null && isPlayer }
            then("world") {
                executes {
                    val world = player.world
                    player.sendMessage(Bukkit.getWorlds().indexOf(world).toString())
                }
            }

            then("locate") {
                executes {
                    GameManager.snowLocation = player.location
                }
            }

            then("join") {
                executes {
                    val game = if(GameManager.games.isEmpty()) {
                        GameManager.addGame()
                    } else {
                        val last = GameManager.games.last()
                        if(last.isFull) {
                            GameManager.addGame()
                        } else {
                            last
                        }
                    }

                    game.join(player)

                    if(game.isFull) {
                        game.start()
                    }
                }
            }
        }
    }
}