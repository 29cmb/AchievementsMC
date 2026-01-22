package xyz.devcmb.achievementsMC.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack

class PlaceBlocksAchievement(
    override val id: String = "placeBlocksAchievement",
    override val item: ItemStack = ItemStack.of(Material.GRASS_BLOCK).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Place Blocks").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every block the")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
            Component.text("player places in the world")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ))
        itemMeta = meta
    }
) : AbstractAchievement() {
    @EventHandler
    fun blockPlaceEvent(event: BlockPlaceEvent) {
        this.increment(event.player)
    }
}