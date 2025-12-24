package xyz.devcmb.achievementsMC.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.Constants
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.controllers.AchievementController

abstract class AbstractAchievement : Listener {
    abstract val id: String
    abstract val item: ItemStack

    protected fun increment(player: Player, amount: Int = 1) {
        val achievementController = ControllerDelegate.getController("achievementController") as AchievementController
        achievementController.incrementAchievementProgress(player, this.id, amount)

        if(Constants.IS_DEVELOPMENT) {
            player.sendMessage(
                Component.text("[AMC Dev] ").color(NamedTextColor.GOLD)
                    .append(Component.text("Incremented achievement progress by $amount").color(NamedTextColor.WHITE))
            )
        }
    }
}