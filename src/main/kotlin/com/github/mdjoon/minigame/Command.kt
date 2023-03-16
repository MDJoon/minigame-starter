package com.github.mdjoon.minigame

import com.destroystokyo.paper.profile.PlayerProfile
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue
import org.bukkit.Bukkit

object Command {

    fun register(plugin: PluginKommand) {
        plugin.register("starter", "str") {
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
                    player.sendMessage("현재 위치를 게임 포인트로 설정하였습니다.")
                }
           }
            then("join") {
                executes {
                    if(!GameManager.isInPlayer(player)) {
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

                        player.sendMessage("${GameManager.games.indexOf(game)}번째 대기열에 참가하였습니다!")

                        if(game.isFull) {
                            GameManager.startGame(game)
                        }
                    }

                }
            }

            then("start") {
                executes {
                    val game = GameManager.getGame(player)
                    if(game?.isProcessing == false) {
                        GameManager.startGame(game)
                        player.sendMessage("게임을 시작하였습니다!")
                    }

                }
            }

            then("limit") {
                requires { isOp }
                then("number" to int(0, 10)) {
                    executes {
                        val number : Int by it
                        GameManager.limit = number
                    }
                }
            }
        }
    }
}