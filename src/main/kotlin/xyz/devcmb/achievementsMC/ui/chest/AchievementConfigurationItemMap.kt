package xyz.devcmb.achievementsMC.ui.chest

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.util.DataTypes
import xyz.devcmb.achievementsMC.util.buttonClickSound
import xyz.devcmb.invcontrol.chest.map.InventoryItemMap
import xyz.devcmb.invcontrol.chest.map.InventoryMappedItem
import xyz.devcmb.achievementsMC.util.selectionList
import xyz.devcmb.achievementsMC.util.sound
import java.util.Collections

class AchievementConfigurationItemMap(
    getAchievementData: () -> DataTypes.AchievementData?,
    visible: () -> Boolean,
    startSlot: Int,
    maxItems: Int,
) : InventoryItemMap(
    getInventoryItems = { _, _ -> ArrayList() },
    startSlot = startSlot,
    maxItems = maxItems,
    itemPage = 1
) {
    val defaultTiers: Int = 5
    val defaultBaseGoal: Int = 10
    val defaultGoalIncrement: Int = 20
    val defaultBaseReward: Int = 2
    val defaultRewardIncrement: Int = 3
    val defaultRewardType: String = "item"
    val defaultRewardItem: String = "minecraft:diamond"

    var tiers = defaultTiers
    var baseGoal = defaultBaseGoal
    var goalIncrement = defaultGoalIncrement
    var baseReward = defaultBaseReward
    var rewardIncrement = defaultRewardIncrement
    var rewardType = defaultRewardType
    var rewardItem = defaultRewardItem

    init {
        val data = getAchievementData()
        if(data != null) {
            setMapConfigFromAData(data)
        }

        val items: ArrayList<InventoryMappedItem> = ArrayList()

        // Tiers item
        items.add(InventoryMappedItem(
            getItemStack = { _,_ ->
                if(!visible()) return@InventoryMappedItem ItemStack.empty()

                val itemStack = ItemStack.of(Material.DIAMOND_PICKAXE)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Tiers").color(NamedTextColor.YELLOW))

                val loreList = arrayListOf<Component>(
                    Component.text("The total amount of times the achievement can be")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("completed, scaling with the increment settings.")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.empty()
                )

                loreList.addAll(selectionList(
                    (1..10).associate { "${it}_tiers" to "$it Tiers" } as HashMap<String, String>,
                    "${tiers}_tiers"
                ))

                meta.lore(loreList)
                itemStack.itemMeta = meta
                itemStack
            },
            onClick = { page, item ->
                page.ui.player.buttonClickSound()
                tiers++
                if(tiers > 10) {
                    tiers = 1
                }
                page.reload()
            }
        ))

        // Base goal item
        items.add(InventoryMappedItem(
            getItemStack = { page, item ->
                if (!visible()) return@InventoryMappedItem ItemStack.empty()
                val itemStack = ItemStack.of(Material.LEATHER_HELMET)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Base Goal").color(NamedTextColor.YELLOW))
                meta.lore(listOf(
                    Component.text("The starting value at tier 1 that gets incremented")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("by the goal increment every time the goal is completed")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Current value: ")
                        .color(NamedTextColor.AQUA).append(
                            Component.text(baseGoal.toString()).color(NamedTextColor.WHITE)
                        )
                        .decoration(TextDecoration.ITALIC, false)
                ))
                itemStack.itemMeta = meta
                itemStack
            },
            onClick = { page, item ->
                page.ui.player.buttonClickSound()
                AnvilGUI.Builder()
                    .onClick { slot, stateSnapshot ->
                        if(slot != AnvilGUI.Slot.OUTPUT) {
                            return@onClick Collections.emptyList()
                        }

                        val num: Int? = stateSnapshot.text.toIntOrNull()
                        if(num == null) {
                            return@onClick listOf(AnvilGUI.ResponseAction.replaceInputText("Must be a number!"))
                        }

                        baseGoal = num
                        page.ui.player.sound(Sound.BLOCK_ANVIL_USE)
                        listOf(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run {
                            page.ui.show()
                        })
                    }
                    .preventClose()
                    .text(baseGoal.toString())
                    .title("Enter the base goal")
                    .plugin(AchievementsMC.plugin)
                    .open(page.ui.player)
            }
        ))

        getInventoryItems = { _, _ ->
            // TODO: Don't do this on refresh, only on page load
            val data = getAchievementData()
            if(data != null) {
                setMapConfigFromAData(data)
            }

            items
        }
    }

    fun resetToDefaults() {
        tiers = defaultTiers
        baseGoal = defaultBaseGoal
        goalIncrement = defaultGoalIncrement
        baseReward = defaultBaseReward
        rewardIncrement = defaultRewardIncrement
        rewardType = defaultRewardType
        rewardItem = defaultRewardItem
    }

    private fun setMapConfigFromAData(data: DataTypes.AchievementData) {
        tiers = data.tiers
        baseGoal = data.baseGoal
        goalIncrement = data.goalIncrement
        baseReward = data.baseReward
        rewardIncrement = data.rewardIncrement
        rewardType = data.rewardType
        rewardItem = data.rewardItem
    }
}