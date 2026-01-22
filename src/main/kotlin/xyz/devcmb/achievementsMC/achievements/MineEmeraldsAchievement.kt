package xyz.devcmb.achievementsMC.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

class MineEmeraldsAchievement (
    override val id: String = "mineEmeraldsAchievement",
    override val item: ItemStack = ItemStack.of(Material.EMERALD).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Mine Emeralds").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every emerald")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
            Component.text("ore block the player mines.")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ))
        itemMeta = meta
    }
) : AbstractAchievement() {
    @EventHandler
    fun onDiamondMineEvent(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if(block.type != Material.EMERALD_ORE && block.type != Material.DEEPSLATE_EMERALD_ORE) return
        this.increment(player, 1)
    }
}