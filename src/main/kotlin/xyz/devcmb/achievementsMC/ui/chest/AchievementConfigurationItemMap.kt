package xyz.devcmb.achievementsMC.ui.chest

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.controllers.UIController
import xyz.devcmb.achievementsMC.util.DataTypes
import xyz.devcmb.achievementsMC.util.buttonClickSound
import xyz.devcmb.invcontrol.chest.map.InventoryItemMap
import xyz.devcmb.invcontrol.chest.map.InventoryMappedItem
import xyz.devcmb.achievementsMC.util.selectionList
import xyz.devcmb.achievementsMC.util.sound
import java.util.Collections

class AchievementConfigurationItemMap(
    val getAchievementData: () -> DataTypes.AchievementData?,
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
        // TODO: So this gets called whenever the menu's `open` method is called
        // which is NOT good because the open method is called by the anvil inputs to go back to the page
        // Best way to do this is probably a flag somewhere to denote if the page is changing, but im not sure
        // future devcmb figure this out
        // kthxbye
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

        // Goal increment item
        items.add(InventoryMappedItem(
            getItemStack = { page, item ->
                if (!visible()) return@InventoryMappedItem ItemStack.empty()
                val itemStack = ItemStack.of(Material.GOLDEN_HELMET)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Goal Increment").color(NamedTextColor.YELLOW))
                meta.lore(listOf(
                    Component.text("The amount the goal amount increases")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("with each passing tier.")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Current value: ")
                        .color(NamedTextColor.AQUA).append(
                            Component.text(goalIncrement.toString()).color(NamedTextColor.WHITE)
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

                        goalIncrement = num
                        page.ui.player.sound(Sound.BLOCK_ANVIL_USE)
                        listOf(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run {
                            page.ui.show()
                        })
                    }
                    .preventClose()
                    .text(goalIncrement.toString())
                    .title("Enter the goal increment")
                    .plugin(AchievementsMC.plugin)
                    .open(page.ui.player)
            }
        ))

        // Base reward item
        items.add(InventoryMappedItem(
            getItemStack = { page, item ->
                if (!visible()) return@InventoryMappedItem ItemStack.empty()
                val itemStack = ItemStack.of(Material.DIAMOND)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Base reward").color(NamedTextColor.YELLOW))
                meta.lore(listOf(
                    Component.text("The starting reward value at tier 1 that gets incremented")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("by the reward increment every time the goal is completed")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Current value: ")
                        .color(NamedTextColor.AQUA).append(
                            Component.text(baseReward.toString()).color(NamedTextColor.WHITE)
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

                        baseReward = num
                        page.ui.player.sound(Sound.BLOCK_ANVIL_USE)
                        listOf(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run {
                            page.ui.show()
                        })
                    }
                    .preventClose()
                    .text(baseReward.toString())
                    .title("Enter the base reward")
                    .plugin(AchievementsMC.plugin)
                    .open(page.ui.player)
            }
        ))

        // Reward increment item
        items.add(InventoryMappedItem(
            getItemStack = { page, item ->
                if (!visible()) return@InventoryMappedItem ItemStack.empty()
                val itemStack = ItemStack.of(Material.EMERALD)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Reward increment").color(NamedTextColor.YELLOW))
                meta.lore(listOf(
                    Component.text("The amount the reward increases")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("with each passing tier.")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Current value: ")
                        .color(NamedTextColor.AQUA).append(
                            Component.text(rewardIncrement.toString()).color(NamedTextColor.WHITE)
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

                        rewardIncrement = num
                        page.ui.player.sound(Sound.BLOCK_ANVIL_USE)
                        listOf(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run {
                            page.ui.show()
                        })
                    }
                    .preventClose()
                    .text(rewardIncrement.toString())
                    .title("Enter the reward increment")
                    .plugin(AchievementsMC.plugin)
                    .open(page.ui.player)
            }
        ))

        // Reward type item
        items.add(InventoryMappedItem(
            getItemStack = { _,_ ->
                if(!visible()) return@InventoryMappedItem ItemStack.empty()

                val itemStack = ItemStack.of(Material.BOOK)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Reward Type").color(NamedTextColor.YELLOW))

                val loreList = arrayListOf<Component>(
                    Component.text("The type of reward to grant after")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("completing a tier")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.empty()
                )

                loreList.addAll(selectionList(
                    hashMapOf(Pair("item", "Item"), Pair("vault_currency", "Vault Currency")),
                    rewardType
                ))

                loreList.add(Component.empty())

                meta.lore(loreList)
                itemStack.itemMeta = meta
                itemStack
            },
            onClick = { page, item ->
                page.ui.player.buttonClickSound()
                rewardType = if (rewardType == "item") "vault_currency" else "item"
                page.reload()
            }
        ))

        // Reward item (if the rewardType is item) item
        items.add(InventoryMappedItem(
            getItemStack = { _,_ ->
                if(!visible() || rewardType != "item") return@InventoryMappedItem ItemStack.empty()

                val itemStack = ItemStack.of(Material.REDSTONE)
                val meta = itemStack.itemMeta
                meta.itemName(Component.text("Reward Item").color(NamedTextColor.YELLOW))

                meta.lore(arrayListOf<Component>(
                    Component.text("The item to grant when each achievement")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.text("tier is completed.")
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Current value: ")
                        .color(NamedTextColor.AQUA).append(
                            Component.text(rewardItem).color(NamedTextColor.WHITE)
                        )
                        .decoration(TextDecoration.ITALIC, false)
                ))

                itemStack.itemMeta = meta
                itemStack
            },
            onClick = { page, item ->
                val player = page.ui.player
                player.buttonClickSound()

                player.closeInventory()
                player.sendMessage(
                    Component.text("Drop an item to set the achievement reward")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD)
                        .appendNewline()
                        .append(
                            Component.text("This prompt will expire in 10 seconds")
                                .color(NamedTextColor.AQUA)
                                .decorate(TextDecoration.ITALIC)
                                .decoration(TextDecoration.BOLD, false)
                        )
                )

                var completed = false
                val uiController: UIController = ControllerDelegate.getController("uiController") as UIController
                uiController.eventCallbacks[player]!!.put("playerDropEvent") { item ->
                    completed = true
                    rewardItem = item.type.key.toString()
                    player.sendMessage(Component.text("Successfully set the reward item to $rewardItem").color(NamedTextColor.GREEN))
                    page.ui.show()
                }

                Bukkit.getScheduler().runTaskLater(AchievementsMC.plugin, Runnable {
                    if(uiController.eventCallbacks.containsKey(player) && !completed) {
                        uiController.eventCallbacks[player]!!.remove("playerDropEvent")
                        player.sendMessage(Component.text("Item drop event cancelled").color(NamedTextColor.RED))
                        page.ui.show()
                    }
                }, 10 * 20)
            }
        ))

        getInventoryItems = { _, _ ->
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