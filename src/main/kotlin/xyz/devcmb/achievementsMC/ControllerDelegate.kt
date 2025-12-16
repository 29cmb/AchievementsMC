package xyz.devcmb.achievementsMC

import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import xyz.devcmb.achievementsMC.controllers.AchievementController
import xyz.devcmb.achievementsMC.controllers.CommandController
import xyz.devcmb.achievementsMC.controllers.DataController
import xyz.devcmb.achievementsMC.controllers.DebuggingController
import xyz.devcmb.achievementsMC.controllers.IController

object ControllerDelegate {
    private val controllers: HashMap<String, IController> = HashMap()

    fun registerAllControllers() {
        registerController("dataController", DataController())
        registerController("achievementController", AchievementController())

        if(Constants.IS_DEVELOPMENT) {
            registerController("debuggingController", DebuggingController())
        }

        registerController("commandController", CommandController())
    }

    fun registerController(id: String, controller: IController) {
        val manager: PluginManager = Bukkit.getServer().pluginManager
        manager.registerEvents(controller, AchievementsMC.plugin)

        controllers[id] = controller
        controller.init() // guess who forgot this :eyes:
        AchievementsMC.pluginLogger.info("Controller $id registered sucessfully")
    }

    fun getController(id: String): IController? {
        val controller: IController? = controllers[id]
        if(controller == null) {
            AchievementsMC.pluginLogger.warning("Controller with id $id not found")
            return null
        }

        return controller
    }
}