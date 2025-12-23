package xyz.devcmb.achievementsMC.ui

import org.bukkit.entity.Player
import xyz.devcmb.achievementsMC.ui.chest.EditAchievementsChestUI

class PlayerUIManager {
    val player: Player
    val uiMap: HashMap<String, IUIBase> = HashMap()

    constructor(player: Player) {
        this.player = player

        registerUI(EditAchievementsChestUI())
    }

    fun openUI(id: String) {
        if(!uiMap.containsKey(id)) {
            throw IllegalArgumentException("Invalid UI ID: $id")
        }

        uiMap[id]!!.show()
    }

    private fun registerUI(ui: IUIBase) {
        ui.init(player)
        uiMap[ui.id] = ui
    }
}