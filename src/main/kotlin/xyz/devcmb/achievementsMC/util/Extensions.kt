package xyz.devcmb.achievementsMC.util

import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.buttonClickSound() {
    player!!.playSound(player!!.location, Sound.UI_BUTTON_CLICK, 10f, 1f)
}