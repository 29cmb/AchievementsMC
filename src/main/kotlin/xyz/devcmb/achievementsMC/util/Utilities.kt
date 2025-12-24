package xyz.devcmb.achievementsMC.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.buttonClickSound() {
    player!!.playSound(player!!.location, Sound.UI_BUTTON_CLICK, 10f, 1f)
}

fun selectionList(elements: HashMap<String, String>, selected: String) : ArrayList<Component> {
    val components: ArrayList<Component> = ArrayList()
    elements.forEach { key, value ->
        var component = Component.text("> $value")
            .color(if(key == selected) NamedTextColor.YELLOW else NamedTextColor.DARK_GRAY)

        if(key == selected) {
            component = component.decorate(TextDecoration.BOLD)
        }

        component = component.decoration(TextDecoration.ITALIC, false)
        components.add(component)
    }

    return components
}