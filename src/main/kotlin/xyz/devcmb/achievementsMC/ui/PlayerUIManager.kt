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

    private fun registerUI(ui: IUIBase) {
        uiMap[ui.id] = ui
    }
}