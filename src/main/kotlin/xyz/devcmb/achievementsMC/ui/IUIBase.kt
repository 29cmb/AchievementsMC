package xyz.devcmb.achievementsMC.ui

import org.bukkit.entity.Player

interface IUIBase {
    val id: String
    fun show(player: Player)
}