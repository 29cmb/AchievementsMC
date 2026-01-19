package xyz.devcmb.achievementsMC.ui.chest

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.controllers.AchievementController
import xyz.devcmb.achievementsMC.controllers.DataController
import xyz.devcmb.achievementsMC.ui.IUIBase
import xyz.devcmb.achievementsMC.util.buttonClickSound
import xyz.devcmb.invcontrol.chest.ChestInventoryPage
import xyz.devcmb.invcontrol.chest.ChestInventoryUI
import xyz.devcmb.invcontrol.chest.InventoryItem
import xyz.devcmb.invcontrol.chest.map.InventoryItemMap
import xyz.devcmb.invcontrol.chest.map.InventoryMappedItem

class EditAchievementsChestUI() : IUIBase {
    override val id: String = "editAchievementsChestUI"
    lateinit var player: Player
    lateinit var ui: ChestInventoryUI
    lateinit var newPageConfigMap: AchievementConfigurationItemMap

    override fun init(player: Player) {
        this.player = player

        this.ui = ChestInventoryUI(
            player,
            title = Component.text("Achievements"),
            rows = 5,
        )

        mainPage()
        newPage()
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
                AchievementsMC.pluginLogger.info("Found ${achievementController.activeAchievements.size} achievements. Propegating menu...")
                achievementController.activeAchievements.forEach {
                    val achievement = achievementController.achievements[it]
                    items.add(InventoryMappedItem(
                        getItemStack = { page, item -> achievement!!.item },
                        onClick = { page, item ->
                            // TODO: Open the edit menu
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

        mainPage.addItem(ItemMapPageNextButton(itemMap, 36, player))
        mainPage.addItem(ItemMapPagePreviousButton(itemMap, 44, player))
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
            getAchievementData = { null }, // since this is the new page this will make it use defaults
            visible = { selectedAchievement != null },
            startSlot = 9,
            maxItems = 27,
        )
        newAchievementPage.addItemMap(newPageConfigMap)

        newAchievementPage.addItem(ItemMapPageNextButton(itemMap, 0, player))
        newAchievementPage.addItem(ItemMapPagePreviousButton(itemMap, 8, player))

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
                    newPageConfigMap.rewardItem
                )

                newPageConfigMap.resetToDefaults()
                selectedAchievement = null
                ui.setPage("main")
            }
        ))
    }
}