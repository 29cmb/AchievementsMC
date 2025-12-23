package xyz.devcmb.achievementsMC.achievements

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class KillHostileMobsAchievement(
    override val item: ItemStack = ItemStack.of(Material.IRON_SWORD).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Kill Hostile Mobs").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every kill on a mob")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
            Component.text("that can damage the player without being provoked")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ))
        itemMeta = meta
    },
    override val id: String = "killMobsAchievement"
) : AbstractAchievement() {
    val hostiles: List<EntityType> = listOf(
        EntityType.ZOMBIE,
        EntityType.DROWNED,
        EntityType.SKELETON,
        EntityType.CREEPER,
        EntityType.STRAY,
        EntityType.WITCH,
        EntityType.WITHER_SKELETON,
        EntityType.WITHER,
        EntityType.BLAZE,
        EntityType.BOGGED,
        EntityType.BREEZE,
        EntityType.CAVE_SPIDER,
        EntityType.SPIDER,
        EntityType.PILLAGER,
        EntityType.RAVAGER,
        EntityType.VINDICATOR,
        EntityType.GUARDIAN,
        EntityType.ELDER_GUARDIAN,
        EntityType.GHAST,
        EntityType.PIGLIN,
        EntityType.ZOMBIFIED_PIGLIN,
        EntityType.HOGLIN,
        EntityType.MAGMA_CUBE,
        EntityType.SILVERFISH,
        EntityType.VEX,
        EntityType.WARDEN
    )

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if(event.entity.killer == null || event.entity.killer !is Player) return
        if(event.entity.type !in hostiles) return

        this.increment(event.entity.killer!!)
    }
}