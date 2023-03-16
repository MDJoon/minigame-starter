package com.github.mdjoon.minigame

import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.*

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

    fun deleteFolder(path: File) {
        if(path.exists()) {
            path.listFiles().forEach {
                if(it.isDirectory) {
                    deleteFolder(it)
                } else {
                    it.delete()
                }
            }

            path.delete()
        }
    }

    fun copyWorld(source: File, target: File) {
        try {
            val ignore = listOf("uid.dat", "session.dat")
            if (!ignore.contains(source.name)) {
                if (source.isDirectory) {
                    if (!target.exists())
                        target.mkdirs()
                    val files = source.list()
                    for (file in files) {
                        val srcFile = File(source, file)
                        val destFile = File(target, file)
                        copyWorld(srcFile, destFile)
                    }
                } else {
                    val inStream: InputStream = FileInputStream(source)
                    val outStream: OutputStream = FileOutputStream(target)
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inStream.read(buffer).also { length = it } > 0) {
                        outStream.write(buffer, 0, length)
                    }
                    inStream.close()
                    outStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}