package xyz.devcmb.achievementsMC.achievements

import io.papermc.paper.event.player.PlayerTradeEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

class TradeAchievement(
    override val id: String = "tradeAchievement",
    override val item: ItemStack = ItemStack.of(Material.VILLAGER_SPAWN_EGG).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Villager Trades").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every time the")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
            Component.text("player trades with a villager.")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ))
        itemMeta = meta
    }
) : AbstractAchievement() {
    @EventHandler
    fun playerTradeEvent(event: PlayerTradeEvent) {
        this.increment(event.player, 1)
    }
}