package xyz.devcmb.achievementsMC.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.abs
import kotlin.math.floor

class SwimAchievement(
    override val id: String = "swimAchievement",
    override val item: ItemStack = ItemStack.of(Material.SALMON).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Swim Blocks").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every block the player")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
            Component.text("travels while swimming in the world.")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ))
        itemMeta = meta
    }
) : AbstractAchievement() {
    @EventHandler
    fun playerSwim(event: PlayerMoveEvent) {
        val from = event.from.block.location
        val to = event.to.block.location

        if((from.x != to.x || from.z != to.z) && to.block.type == Material.WATER) {
            this.increment(
                event.player,
                floor(abs(from.x - to.x)).toInt() + floor(abs(from.z - to.z)).toInt(),
                true
            )
        }
    }
}