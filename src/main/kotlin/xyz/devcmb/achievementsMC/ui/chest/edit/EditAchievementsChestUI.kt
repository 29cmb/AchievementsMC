package xyz.devcmb.achievementsMC.ui.chest.edit

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
import kotlin.collections.get

class EditAchievementsChestUI() : IUIBase {
    override val id: String = "editAchievementsChestUI"
    lateinit var player: Player
    lateinit var ui: ChestInventoryUI
    lateinit var newPageConfigMap: AchievementConfigurationItemMap
    lateinit var editPageConfigMap: AchievementConfigurationItemMap
    var editingAchievement: String? = null

    override fun init(player: Player) {
        this.player = player

        this.ui = ChestInventoryUI(
            player,
            title = Component.text("Achievements"),
            rows = 5,
        )

        mainPage()
        newPage()
        editPage()
        confirmDeletePage()
    }

    override fun show() {
        ui.setPage("main")
        ui.show()
    }

    fun mainPage() {
        val mainPage = ChestInventoryPage()
        ui.addPage("main", mainPage)

        val itemMap = InventoryItemMap(
            getInventoryItems = { _,_ ->
                val items: ArrayList<InventoryMappedItem> = ArrayList()
                items.add(InventoryMappedItem(
                    getItemStack = { page, item ->
                        val itemStack = ItemStack.of(Material.GREEN_STAINED_GLASS_PANE)
                        val meta = itemStack.itemMeta
                        meta.itemName(Component.text("New").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        itemStack.itemMeta = meta
                        itemStack
                    },
                    onClick = { page, item ->
                        player.buttonClickSound()
                        newPageConfigMap.resetToDefaults()
                        ui.setPage("newAchievement")
                    }
                ))

                val achievementController = ControllerDelegate.getController("achievementController") as AchievementController
                val dataController = ControllerDelegate.getController("dataController") as DataController

                achievementController.activeAchievements.forEach {
                    val achievement = achievementController.achievements[it]!!
                    val achievementData = dataController.achievements[it]!!
                    items.add(InventoryMappedItem(
                        getItemStack = { page, item ->
                            achievement.item.clone().apply {
                                val meta = itemMeta
                                val lore = meta.lore()
                                lore!!.addAll(arrayOf(
                                    Component.empty(),
                                    Component.text("Tiers: ").color(NamedTextColor.AQUA)
                                        .append(Component.text(achievementData.tiers.toString()).color(NamedTextColor.WHITE))
                                        .decoration(TextDecoration.ITALIC, false),
                                    Component.text("Base Goal: ").color(NamedTextColor.AQUA)
                                        .append(Component.text(achievementData.baseGoal.toString()).color(NamedTextColor.WHITE))
                                        .decoration(TextDecoration.ITALIC, false),
                                    Component.text("Goal Increment: ").color(NamedTextColor.AQUA)
                                        .append(Component.text(achievementData.goalIncrement.toString()).color(NamedTextColor.WHITE))
                                        .decoration(TextDecoration.ITALIC, false),
                                    Component.text("Base Reward: ").color(NamedTextColor.AQUA)
                                        .append(Component.text(achievementData.baseReward.toString()).color(NamedTextColor.WHITE))
                                        .decoration(TextDecoration.ITALIC, false),
                                    Component.text("Reward Increment: ").color(NamedTextColor.AQUA)
                                        .append(Component.text(achievementData.rewardIncrement.toString()).color(NamedTextColor.WHITE))
                                        .decoration(TextDecoration.ITALIC, false),
                                    Component.text("Reward Type: ").color(NamedTextColor.AQUA)
                                        .append(Component.text(achievementData.rewardType).color(NamedTextColor.WHITE))
                                        .decoration(TextDecoration.ITALIC, false),
                                ))

                                if(achievementData.rewardType == "item") {
                                    lore.add(
                                        Component.text("Reward Item: ").color(NamedTextColor.AQUA)
                                            .append(Component.text(achievementData.rewardItem).color(NamedTextColor.WHITE))
                                            .decoration(TextDecoration.ITALIC, false),
                                    )
                                }

                                lore.addAll(arrayOf(
                                    Component.empty(),
                                    Component.text("Click to Edit").color(NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, false)
                                ))

                                meta.lore(lore)
                                itemMeta = meta
                            }
                        },
                        onClick = { page, item ->
                            player.buttonClickSound()
                            editingAchievement = achievement.id
                            editPageConfigMap.setMapConfigFromAData(achievementData)
                            ui.setPage("editAchievement")
                        }
                    ))
                }

                items
            },
            startSlot = 0,
            maxItems = 26,
            itemPage = 1
        )
        mainPage.addItemMap(itemMap)

        // Black barrier
        for(i in 27..35) {
            mainPage.addItem(InventoryItem(
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

        mainPage.addItem(ItemMapPagePreviousButton(itemMap, 36))
        mainPage.addItem(ItemMapPageNextButton(itemMap, 44))

        mainPage.addItem(InventoryItem(
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
                page.reload()
            }
        ))
    }

    fun newPage() {
        val newAchievementPage = ChestInventoryPage()
        ui.addPage("newAchievement", newAchievementPage)

        var selectedAchievement: String? = null
        val achievementController = ControllerDelegate.getController("achievementController") as AchievementController

        val itemMap = InventoryItemMap(
            getInventoryItems = { page, map ->
                val items: ArrayList<InventoryMappedItem> = ArrayList()

                achievementController.achievements.forEach {
                    items.add(InventoryMappedItem(
                        getItemStack = { _,_ ->
                            var item = it.value.item
                            if(selectedAchievement == it.value.id) {
                                item = item.withType(Material.GREEN_STAINED_GLASS_PANE)
                            }

                            if(achievementController.activeAchievements.contains(it.value.id)) {
                                item = item.withType(Material.BARRIER)
                                val meta = item.itemMeta
                                var lore = meta.lore()
                                if(lore == null) lore = ArrayList()

                                lore.add(
                                    Component.text("This achievement already exists!")
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD)
                                        .decoration(TextDecoration.ITALIC, false)
                                )

                                meta.lore(lore)
                                item.itemMeta = meta
                            }

                            item
                        },
                        onClick = { page, item ->
                            player.buttonClickSound()
                            if(achievementController.activeAchievements.contains(it.value.id)) return@InventoryMappedItem

                            selectedAchievement = if (selectedAchievement != it.value.id) it.value.id else null
                            page.reload()
                        }
                    ))
                }

                items
            },
            startSlot = 1,
            maxItems = 7,
            itemPage = 1
        )
        newAchievementPage.addItemMap(itemMap)

        newPageConfigMap = AchievementConfigurationItemMap(
            visible = { selectedAchievement != null },
            startSlot = 9,
            maxItems = 27,
        )
        newAchievementPage.addItemMap(newPageConfigMap)

        newAchievementPage.addItem(ItemMapPagePreviousButton(itemMap, 0))
        newAchievementPage.addItem(ItemMapPageNextButton(itemMap, 8))

        newAchievementPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                val itemStack = ItemStack.of(Material.BARRIER)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Back").color(NamedTextColor.RED))
                itemStack.itemMeta = meta
                itemStack
            },
            slot = 36,
            onClick = { page, item ->
                player.buttonClickSound()
                newPageConfigMap.resetToDefaults()
                selectedAchievement = null
                ui.setPage("main")
            }
        ))

        newAchievementPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                if(selectedAchievement == null) return@InventoryItem ItemStack.empty()

                val itemStack = ItemStack.of(Material.GREEN_CONCRETE)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Create").color(NamedTextColor.GREEN))
                itemStack.itemMeta = meta
                itemStack
            },
            slot = 44,
            onClick = { page, item ->
                player.buttonClickSound()

                val dataController = ControllerDelegate.getController("dataController") as DataController
                dataController.addAchievement(
                    selectedAchievement!!,
                    newPageConfigMap.tiers,
                    newPageConfigMap.baseGoal,
                    newPageConfigMap.goalIncrement,
                    newPageConfigMap.baseReward,
                    newPageConfigMap.rewardIncrement,
                    newPageConfigMap.rewardType,
                    newPageConfigMap.rewardItem,
                    false
                )

                newPageConfigMap.resetToDefaults()
                selectedAchievement = null
                ui.setPage("main")
            }
        ))
    }

    fun editPage() {
        val editAchievementPage = ChestInventoryPage()
        ui.addPage("editAchievement", editAchievementPage)


        editPageConfigMap = AchievementConfigurationItemMap(
            visible = { true },
            startSlot = 9,
            maxItems = 27,
        )

        editAchievementPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                if(editingAchievement == null) return@InventoryItem ItemStack.empty()

                val achievementController: AchievementController = ControllerDelegate.getController("achievementController") as AchievementController
                achievementController.achievements[editingAchievement]!!.item
            },
            slot = 0,
        ))

        editAchievementPage.addItem(InventoryItem(
            getItemStack = { _, _ ->
                // this way of doing it is so much better holy
                ItemStack.of(Material.RED_CONCRETE).apply {
                    val meta = itemMeta
                    meta.itemName(Component.text("Cancel").color(NamedTextColor.RED))
                    itemMeta = meta
                }
            },
            slot = 36,
            onClick = { page, item ->
                player.buttonClickSound()
                editPageConfigMap.resetToDefaults()
                ui.setPage("main")
            }
        ))

        editAchievementPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                ItemStack.of(Material.BARRIER).apply {
                    val meta = itemMeta
                    meta.itemName(Component.text("Delete").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    itemMeta = meta
                }
            },
            slot = 40,
            onClick = { page, item ->
                player.buttonClickSound()
                page.ui.setPage("deleteConfirmation")
            }
        ))

        editAchievementPage.addItem(InventoryItem(
            getItemStack = { _, _ ->
                // this way of doing it is so much better holy
                ItemStack.of(Material.GREEN_CONCRETE).apply {
                    val meta = itemMeta
                    meta.itemName(Component.text("Save").color(NamedTextColor.GREEN))
                    itemMeta = meta
                }
            },
            slot = 44,
            onClick = { page, item ->
                player.buttonClickSound()
                val dataController: DataController = ControllerDelegate.getController("dataController") as DataController
                dataController.addAchievement(
                    editingAchievement!!,
                    editPageConfigMap.tiers,
                    editPageConfigMap.baseGoal,
                    editPageConfigMap.goalIncrement,
                    editPageConfigMap.baseReward,
                    editPageConfigMap.rewardIncrement,
                    editPageConfigMap.rewardType,
                    editPageConfigMap.rewardItem,
                    true
                )
                editPageConfigMap.resetToDefaults()
                ui.setPage("main")
            }
        ))

        editAchievementPage.addItemMap(editPageConfigMap)
    }

    fun confirmDeletePage() {
        val confirmPage = ChestInventoryPage()
        ui.addPage("deleteConfirmation", confirmPage)

        confirmPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                ItemStack.of(Material.RED_CONCRETE).apply {
                    val meta = itemMeta
                    meta.itemName(Component.text("Cancel").color(NamedTextColor.RED))
                    itemMeta = meta
                }
            },
            slot = 20,
            onClick = { page, item ->
                player.buttonClickSound()
                page.ui.setPage("editAchievement")
            }
        ))

        confirmPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                ItemStack.of(Material.PAPER).apply {
                    val meta = itemMeta
                    meta.itemName(
                        Component.text("Are you sure?")
                        .color(NamedTextColor.YELLOW)
                        .decorate(TextDecoration.BOLD)
                    )
                    meta.lore(listOf(
                        Component.text("This action is ").color(NamedTextColor.WHITE).append(
                            Component.text("IRREVERSIBLE").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
                        ).decoration(TextDecoration.ITALIC, false)
                    ))
                    itemMeta = meta
                }
            },
            slot = 22
        ))

        confirmPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                ItemStack.of(Material.GREEN_CONCRETE).apply {
                    val meta = itemMeta
                    meta.itemName(Component.text("Confirm").color(NamedTextColor.GREEN))
                    meta.lore(listOf(
                        Component.text("This action is ").color(NamedTextColor.WHITE).append(
                            Component.text("IRREVERSIBLE").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
                        ).decoration(TextDecoration.ITALIC, false)
                    ))
                    itemMeta = meta
                }
            },
            slot = 24,
            onClick = { page, item ->
                player.buttonClickSound()
                val dataController: DataController = ControllerDelegate.getController("dataController") as DataController
                dataController.removeAchievement(editingAchievement!!)
                editingAchievement = null
                editPageConfigMap.resetToDefaults()
                page.ui.setPage("main")
            }
        ))
    }
}