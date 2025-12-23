package xyz.devcmb.achievementsMC

import org.bukkit.plugin.java.JavaPlugin
import xyz.devcmb.achievementsMC.util.Database
import xyz.devcmb.invcontrol.InvControlManager
import java.util.logging.Logger

class AchievementsMC : JavaPlugin() {
    companion object {
        lateinit var plugin: AchievementsMC
        lateinit var pluginLogger: Logger
    }

    override fun onEnable() {
        plugin = this
        pluginLogger = logger

        InvControlManager.setPlugin(this)

        saveDefaultConfig()
        ControllerDelegate.registerAllControllers()
        Database.connect()
    }

    override fun onDisable() {
        Database.close()
    }
}
