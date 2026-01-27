package xyz.devcmb.achievementsMC.ui.chest.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.controllers.AchievementController
import xyz.devcmb.achievementsMC.controllers.DataController
import xyz.devcmb.achievementsMC.ui.IUIBase
import xyz.devcmb.achievementsMC.ui.chest.ItemMapPageNextButton
import xyz.devcmb.achievementsMC.ui.chest.ItemMapPagePreviousButton
import xyz.devcmb.achievementsMC.util.buttonClickSound
import xyz.devcmb.invcontrol.chest.ChestInventoryPage
import xyz.devcmb.invcontrol.chest.ChestInventoryUI
import xyz.devcmb.invcontrol.chest.InventoryItem
import xyz.devcmb.invcontrol.chest.map.InventoryItemMap
import xyz.devcmb.invcontrol.chest.map.InventoryMappedItem
import kotlin.math.min
import kotlin.math.roundToInt

class AchievementsUI(override val id: String = "playerAchievementsUI") : IUIBase {
    lateinit var player: Player
    lateinit var ui: ChestInventoryUI
    val tierSelections: HashMap<String, Int> = HashMap()

    override fun init(player: Player) {
        this.player = player
        ui = ChestInventoryUI(
            player,
            rows = 5,
            title = Component.text("Achievements")
        )

        mainPage()
    }

    override fun show() {
        tierSelections.clear()
        ui.setPage("main")
        ui.show()
    }

    private fun mainPage() {
        val page = ChestInventoryPage()
        ui.addPage("main", page)

        val achievementsController: AchievementController = ControllerDelegate.getController("achievementController") as AchievementController
        val dataController: DataController = ControllerDelegate.getController("dataController") as DataController

        val itemMap = InventoryItemMap(
            getInventoryItems = { page, map ->
                val items: ArrayList<InventoryMappedItem> = ArrayList()
                achievementsController.activeAchievements.forEach {
                    val achievementClass = achievementsController.achievements[it]!!
                    val data = dataController.achievements[it]!!

                    val playerData = dataController.playerData[player]!!
                    val playerProgression = playerData.progresses[it] ?: 0
                    val lastClaimed = playerData.lastClaimed[it] ?: 0

                    val currentTier = min(
                        if (playerProgression < data.baseGoal) 0
                        else ((playerProgression - data.baseGoal) / data.goalIncrement) + 1,
                        data.tiers
                    )
                    val rewardAvailable = currentTier > lastClaimed

                    items.add(InventoryMappedItem(
                        getItemStack = { page, item ->
                            tierSelections.putIfAbsent(
                                it,
                                min(
                                    if (playerProgression < data.baseGoal) 1
                                    else ((playerProgression - data.baseGoal) / data.goalIncrement) + 2, data.tiers)
                            )

                            val currentTierSelection = tierSelections.get(it)!!

                            var item = achievementClass.item.clone()

                            if(rewardAvailable) {
                                item = item.withType(Material.GREEN_STAINED_GLASS_PANE)
                            }

                            item.apply {
                                val meta = itemMeta
                                var lore = meta.lore()
                                if(lore == null) lore = ArrayList()

                                lore.add(Component.empty())

                                var tiersComponent = Component.text("<").append(Component.text("1")
                                    .color(if (currentTierSelection == 1) NamedTextColor.YELLOW else NamedTextColor.GRAY))

                                for (i in 2..data.tiers) {
                                    tiersComponent = tiersComponent.append(Component.text("/").append(Component.text(i.toString())
                                        .color(if (currentTierSelection == i) NamedTextColor.YELLOW else NamedTextColor.GRAY)))
                                }

                                tiersComponent = tiersComponent.append(Component.text(">"))
                                tiersComponent = tiersComponent
                                    .color(NamedTextColor.WHITE)
                                    .decoration(TextDecoration.ITALIC, false)
                                lore.add(tiersComponent)

                                lore.add(Component.empty())

                                val tierRequirement = data.baseGoal + (data.goalIncrement * (currentTierSelection - 1))
                                lore.add(
                                    Component.text("Progress: ")
                                        .append(Component.text(playerProgression.toString())
                                            .color(if (playerProgression >= tierRequirement) NamedTextColor.GREEN else NamedTextColor.WHITE)
                                            .append(Component.text("/$tierRequirement")))
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(NamedTextColor.WHITE))

                                var progressBarComponent = Component.empty()
                                for (i in 1..60) {
                                    // each of these is 1/60th of progression
                                    // Take the requirement and divide it by 60
                                    // Multiply that quotient by `i` and check that against the playerProgression
                                    // this is the best way I can work out math
                                    // line count inflation!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                                    val isCompleted = (tierRequirement.toFloat() / 60) * i <= playerProgression
                                    progressBarComponent = progressBarComponent.append(Component.text("|")
                                        .color(if(isCompleted) NamedTextColor.GREEN else NamedTextColor.GRAY)
                                        .decoration(TextDecoration.ITALIC, false))
                                }

                                progressBarComponent = progressBarComponent.append(
                                    Component.text(
                                        " ${(playerProgression.toFloat() / tierRequirement.toFloat())
                                            .coerceAtMost(1f)
                                            .times(100f)
                                            .roundToInt()}%"
                                    ).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))

                                lore.add(progressBarComponent)

                                if(rewardAvailable) {
                                    lore.add(Component.empty())
                                    lore.add(Component.text("Click to claim an unclaimed reward!")
                                        .color(NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, false))
                                }

                                meta.lore(lore)
                                itemMeta = meta
                            }
                        },
                        onClick = { page, item ->
                            player.buttonClickSound()

                            if(rewardAvailable) {
                                achievementsController.claimAchievementTier(player, it)
                            } else {
                                tierSelections[it] = tierSelections[it]!! + 1
                                if(tierSelections[it]!! > data.tiers) {
                                    tierSelections[it] = 1
                                }
                            }

                            page.reload()
                        }
                    ))
                }
                items
            },
            startSlot = 0,
            maxItems = 27,
            itemPage = 1
        )
        page.addItemMap(itemMap)

        for(i in 27..35) {
            page.addItem(InventoryItem(
                getItemStack = { page, item ->
                    val itemStack = ItemStack.of(Material.BLACK_STAINED_GLASS_PANE)
                    val meta = itemStack.itemMeta
                    meta.isHideTooltip = true
                    itemStack.itemMeta = meta

                    itemStack
                },
                slot = i
            ))
        }

        page.addItem(ItemMapPagePreviousButton(itemMap, 36))
        page.addItem(ItemMapPageNextButton(itemMap, 44))

        page.addItem(InventoryItem(
            getItemStack = { page, item ->
                val itemStack = ItemStack.of(Material.BLUE_CONCRETE)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Refresh").color(NamedTextColor.BLUE))
                itemStack.itemMeta = meta
                itemStack
            },
            slot = 40,
            onClick = { page, item ->
                player.buttonClickSound()
                tierSelections.clear()
                page.reload()
            }
        ))
    }
}