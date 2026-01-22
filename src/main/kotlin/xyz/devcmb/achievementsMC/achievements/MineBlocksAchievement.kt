package xyz.devcmb.achievementsMC.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

class MineBlocksAchievement(
    override val id: String = "breakBlocksAchievement",
    override val item: ItemStack = ItemStack.of(Material.COBBLESTONE).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Break Blocks").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every block the")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
            Component.text("player breaks in the world")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ))
        itemMeta = meta
    }
) : AbstractAchievement() {
    @EventHandler
    fun mineBlockEvent(event: BlockBreakEvent) {
        this.increment(event.player)
    }
}