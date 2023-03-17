package com.github.mdjoon.minigame

import com.destroystokyo.paper.profile.PlayerProfile
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue
import org.bukkit.*
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator
import org.codehaus.plexus.util.FileUtils

object Command {

    fun register(plugin: PluginKommand) {
        plugin.register("starter", "str") {
            requires { playerOrNull != null && isPlayer }
            then("world") {
                then("name" to string()) {
                    executes {
                        val name : String by it
                        val worldCreator = WorldCreator.name(name)
                        worldCreator.type(WorldType.FLAT)
                        val world = worldCreator.createWorld()
                        world?.apply {
                            setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
                            setGameRule(GameRule.DO_MOB_SPAWNING, false)
                            setGameRule(GameRule.DO_MOB_LOOT, false)
                        }
                    }
                }
            }

            then("switch") {
                then("world" to string()) {
                    executes {
                        val world : String by it
                        player.teleport(Bukkit.getWorld(world)?.spawnLocation!!)
                    }
                }
            }

            then("remove") {
                then("str" to string()) {
                    executes {
                        val str : String by it
                        val world = Bukkit.getWorld(str)!!
                        Bukkit.unloadWorld(world, false)
                        Bukkit.getWorlds().remove(world)
                        FileUtils.deleteDirectory(world.worldFolder)
                    }
                }
            }

            then("copyWorld") {
                then("world" to dynamicByMap(Bukkit.getWorlds().associateBy { it.name })) {
                    executes {
                        val world : World by it
                        val copied = FileManager.copyWorld(world, "cop")
                        player.teleport(copied.spawnLocation)
                    }
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