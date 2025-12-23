package xyz.devcmb.achievementsMC.ui.chest

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.util.buttonClickSound
import xyz.devcmb.invcontrol.chest.InventoryItem
import xyz.devcmb.invcontrol.chest.map.InventoryItemMap

class ItemMapPageNextButton(
    itemMap: InventoryItemMap,
    slot: Int,
    player: Player
) : InventoryItem(
    getItemStack = { page, item ->
        val itemStack = ItemStack.of(Material.ARROW)
        val meta = itemStack.itemMeta
        meta.itemName(Component.text("Next Page").color(NamedTextColor.YELLOW))
        meta.lore(
            listOf(
                Component.text("Page ")
                    .append(Component.text(itemMap.itemPage.toString()))
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false)
            )
        )
        itemStack.itemMeta = meta
        itemStack
    },
    slot = slot,
    onClick = { page, item ->
        player.buttonClickSound()
        itemMap.pageForward()
    }
)