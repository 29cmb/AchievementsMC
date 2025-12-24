package xyz.devcmb.achievementsMC.util

import org.bukkit.entity.Player
import java.sql.ResultSet

object DataTypes {
    data class PlayerProgressionData(
        val player: Player,
        val progresses: HashMap<String, Int>
    )

    data class AchievementData(
        var id: String,
        var type: String,
        var description: String,
        var tiers: Int,
        var baseGoal: Int,
        var goalIncrement: Int,
        var baseReward: Int,
        var rewardIncrement: Int,
        var rewardType: String,
        var rewardItem: String
    ) {
        constructor(result: ResultSet) : this(
            id = result.getString("id"),
            type = result.getString("type"),
            description = result.getString("description"),
            tiers = result.getInt("tiers"),
            baseGoal = result.getInt("tier_base_goal"),
            goalIncrement = result.getInt("tier_goal_increment"),
            baseReward = result.getInt("tier_base_reward"),
            rewardIncrement = result.getInt("tier_reward_increment"),
            rewardType = result.getString("reward_type"),
            rewardItem = result.getString("reward_item")
        )
    }
}
