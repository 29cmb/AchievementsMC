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

class KillPassiveMobsAchievement(
    override val id: String = "killPassiveMobsAchievement",
    override val item: ItemStack = ItemStack.of(Material.WOODEN_SWORD).apply {
        val meta = itemMeta
        meta.itemName(Component.text("Kill Passive Mobs").color(NamedTextColor.YELLOW))
        meta.lore(listOf(
            Component.text("Gives progress for every kill on a mob")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
            Component.text("that cannot damage the player.")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ))
        itemMeta = meta
    },
) : AbstractAchievement() {
    val passives: List<EntityType> = listOf(
        EntityType.CHICKEN,
        EntityType.STRIDER,
        EntityType.PIG,
        EntityType.SHEEP,
        EntityType.COW,
        EntityType.SALMON,
        EntityType.COD,
        EntityType.TROPICAL_FISH,
        EntityType.ALLAY,
        EntityType.SQUID,
        EntityType.GLOW_SQUID,
        EntityType.AXOLOTL,
        EntityType.BAT,
        EntityType.CAMEL,
        EntityType.HORSE,
        EntityType.DONKEY,
        EntityType.FROG,
        EntityType.MOOSHROOM,
        EntityType.TADPOLE,
        EntityType.TURTLE,
        EntityType.VILLAGER,
        EntityType.WANDERING_TRADER,
    )

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if(event.entity.killer == null || event.entity.killer !is Player) return
        if(event.entity.type !in passives) return

        this.increment(event.entity.killer!!)
    }
}