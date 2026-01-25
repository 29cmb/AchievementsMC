package xyz.devcmb.achievementsMC.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

class KillPlayersAchievement(
    override val item: ItemStack = ItemStack.of(Material.DIAMOND_SWORD).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Kill Players").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every player kill")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ))
        itemMeta = meta
    },
    override val id: String = "killPlayersAchievement"
) : AbstractAchievement() {
    @EventHandler
    fun onEntityDeath(event: PlayerDeathEvent) {
        if(event.entity.killer == null || event.entity.killer !is Player) return

        this.increment(event.entity.killer!!)
    }
}