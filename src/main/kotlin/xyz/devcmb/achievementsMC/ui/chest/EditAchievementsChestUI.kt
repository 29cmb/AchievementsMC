package xyz.devcmb.achievementsMC.ui.chest

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.controllers.AchievementController
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
                        ui.setPage("newAchievement")
                    }
                ))

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
    }

    fun newPage() {
        val newAchievementPage = ChestInventoryPage()
        ui.addPage("newAchievement", newAchievementPage)

        val achievementController = ControllerDelegate.getController("achievementController") as AchievementController

        val itemMap = InventoryItemMap(
            getInventoryItems = { page, map ->
                val items: ArrayList<InventoryMappedItem> = ArrayList()

                achievementController.achievements.forEach {
                    items.add(InventoryMappedItem(
                        getItemStack = { _,_ -> it.value.item }
                    ))
                }

                items
            },
            startSlot = 1,
            maxItems = 7,
            itemPage = 1
        )

        newAchievementPage.addItemMap(itemMap)

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
                ui.setPage("main")
            }
        ))
    }
}