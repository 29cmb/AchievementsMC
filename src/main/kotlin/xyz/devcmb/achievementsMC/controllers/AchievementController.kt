package xyz.devcmb.achievementsMC.controllers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.achievements.*
import xyz.devcmb.achievementsMC.util.sound
import kotlin.math.floor
import kotlin.math.min

class AchievementController : IController {
    val achievements: HashMap<String, AbstractAchievement> = HashMap()
    val activeAchievements: ArrayList<String> = ArrayList()
    lateinit var dataController: DataController

    override fun init() {
        dataController = ControllerDelegate.getController("dataController") as DataController

        registerAllAchievements()

        dataController.achievements.forEach {
            activateAchievement(it.value.id)
        }
    }

    private fun registerAllAchievements() {
        registerAchievement(KillHostileMobsAchievement())
        registerAchievement(KillPassiveMobsAchievement())
        registerAchievement(KillPlayersAchievement())
        registerAchievement(TravelAchievement())
        registerAchievement(SwimAchievement())
        registerAchievement(MineDiamondsAchievement())
        registerAchievement(TradeAchievement())
        registerAchievement(PlaceBlocksAchievement())
        registerAchievement(MineBlocksAchievement())
        registerAchievement(MineEmeraldsAchievement())
    }

    private fun registerAchievement(achievement: AbstractAchievement) {
        achievements[achievement.id] = achievement
    }

    fun activateAchievement(id: String) {
        val achievement = achievements[id]
        if(achievement == null) {
            throw IllegalArgumentException("Achievement with id $id does not exist")
        }

        activeAchievements.add(id)
        Bukkit.getPluginManager().registerEvents(achievement, AchievementsMC.plugin)
    }

    fun deactivateAchievement(id: String) {
        val achievement = achievements[id]
        if(achievement == null) {
            throw IllegalArgumentException("Achievement with id $id does not exist")
        }

        HandlerList.unregisterAll(achievement)
        activeAchievements.remove(id)
    }

    fun incrementAchievementProgress(player: Player, id: String, amount: Int = 1) {
        val playerData = dataController.playerData[player]!!
        var progress = playerData.progresses[id] ?: 0

        val achievementData = dataController.achievements[id]!!

        val currentTier = min(if (progress < achievementData.baseGoal) 1
        else ((progress - achievementData.baseGoal) / achievementData.goalIncrement) + 2, achievementData.tiers)

        progress += amount

        val nextTier = min(if (progress < achievementData.baseGoal) 1
        else ((progress - achievementData.baseGoal) / achievementData.goalIncrement) + 2, achievementData.tiers)

        if(currentTier != nextTier) {
            player.sound(Sound.BLOCK_BEACON_ACTIVATE)
            player.sendMessage(
                Component.empty().append(
                    Component.text("Achievement tier completed!")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD)
                ).append(Component.text(" Check the ")
                    .append(Component.text("[achievements page]").color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("achievements")))
                    .append(Component.text(" to claim your reward!")))
            )
        }

        playerData.progresses[id] = progress
    }

    fun claimAchievementTier(player: Player, achievement: String) {
        val playerData = dataController.playerData[player]!!
        val achievementData = dataController.achievements[achievement]!!

        val playerProgress = playerData.progresses[achievement]!!
        val currentTier = min(
            if (playerProgress < achievementData.baseGoal) 0
            else ((playerProgress - achievementData.baseGoal) / achievementData.goalIncrement) + 1,
            achievementData.tiers
        )

        val lastClaimed = playerData.lastClaimed[achievement] ?: 0
        if(currentTier <= lastClaimed) {
            throw IllegalStateException("Player's last claim is greater than or equal to player's current tier")
        }

        val reward = achievementData.baseReward + (achievementData.rewardIncrement * lastClaimed)
        when(achievementData.rewardType) {
            "item" -> {
                val stacks: ArrayList<ItemStack> = ArrayList()
                repeat(floor(reward / 64.0f).toInt()) {
                    stacks.add(
                        ItemStack(
                            Material.matchMaterial(achievementData.rewardItem.removePrefix("minecraft:"))!!,
                            64
                        )
                    )
                }

                stacks.add(ItemStack(
                    Material.matchMaterial(achievementData.rewardItem.removePrefix("minecraft:"))!!,
                    reward % 64
                ))

                var openSlots = 0
                for(slot in player.inventory.contents) {
                    if(slot == null) openSlots++
                }

                if(openSlots < stacks.size) {
                    player.sendMessage(
                        Component.text("You do not have enough inventory space to claim your reward! You need ${stacks.size} slots, but you only have $openSlots!")
                            .color(NamedTextColor.RED))
                    return
                }

                player.inventory.addItem(*stacks.toTypedArray())

                var rewardRecap = Component.text("Rewards given successfully!").color(NamedTextColor.GREEN)
                for (item in stacks) {
                    rewardRecap = rewardRecap.append(
                        Component.newline().append(Component.translatable(item.type.translationKey()).color(NamedTextColor.YELLOW)
                            .append(Component.text(" x${item.amount}").color(NamedTextColor.WHITE)))
                    )
                }

                player.sendMessage(rewardRecap)
                playerData.lastClaimed[achievement] = (playerData.lastClaimed[achievement] ?: 0).plus(1)
            }
            "levels" -> {
                player.level += reward
                player.sendMessage(Component.text("+$reward levels given successfully!").color(NamedTextColor.GREEN))
                playerData.lastClaimed[achievement] = (playerData.lastClaimed[achievement] ?: 0).plus(1)
            }
        }
    }
}