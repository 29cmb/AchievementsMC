package xyz.devcmb.achievementsMC.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

class MineDiamondsAchievement(
    override val id: String = "mineDiamondsAchievement",
    override val item: ItemStack = ItemStack.of(Material.DIAMOND).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Mine Diamonds").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every diamond")
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

        if(block.type != Material.DIAMOND_ORE && block.type != Material.DEEPSLATE_DIAMOND_ORE) return
        this.increment(player, 1)
    }
}