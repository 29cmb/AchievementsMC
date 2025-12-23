package xyz.devcmb.achievementsMC.ui.chest

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.ui.IUIBase
import xyz.devcmb.invcontrol.chest.ChestInventoryPage
import xyz.devcmb.invcontrol.chest.ChestInventoryUI
import xyz.devcmb.invcontrol.chest.InventoryItem
import xyz.devcmb.invcontrol.chest.map.InventoryItemMap
import xyz.devcmb.invcontrol.chest.map.InventoryMappedItem

class EditAchievementsChestUI() : IUIBase {
    override val id: String = "editAchievementsChestUI"
    override fun show(player: Player) {
        val ui = ChestInventoryUI(
            player,
            title = Component.text("Achievements"),
            rows = 5,
        )

        val mainPage = ChestInventoryPage()
        ui.addPage("main", mainPage)

        val itemMap = InventoryItemMap(
            getInventoryItems = { _,_ ->
                val items: ArrayList<InventoryMappedItem> = ArrayList()
                // TODO: Fill with the current achievements
                // TODO: "Add new" button
                items
            },
            startSlot = 0,
            maxItems = 26,
            itemPage = 1
        )
        mainPage.addItemMap(itemMap)

        // Black barrier
        for(i in 35..44) {
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

        mainPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                val itemStack = ItemStack.of(Material.ARROW)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Previous Page").color(NamedTextColor.YELLOW))
                meta.lore(listOf(
                    Component.text("Page ")
                        .append(Component.text(itemMap.itemPage.toString()))
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false)
                ))
                itemStack.itemMeta = meta

                itemStack
            },
            slot = 45,
            onClick = { page, item ->
                itemMap.pageForward()
                page.reload()
            }
        ))
        mainPage.addItem(InventoryItem(
            getItemStack = { page, item ->
                val itemStack = ItemStack.of(Material.ARROW)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Next Page").color(NamedTextColor.YELLOW))
                meta.lore(listOf(
                    Component.text("Page ")
                        .append(Component.text(itemMap.itemPage.toString()))
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false)
                ))
                itemStack.itemMeta = meta

                itemStack
            },
            slot = 53,
            onClick = { page, item ->
                itemMap.pageForward()
                page.reload()
            }
        ))

        ui.setPage("main")
        ui.show()
    }
}