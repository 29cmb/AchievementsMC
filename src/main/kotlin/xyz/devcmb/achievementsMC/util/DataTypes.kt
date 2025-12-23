package xyz.devcmb.achievementsMC.util

import java.sql.ResultSet

object DataTypes {
    data class PlayerData(val uuid: String)
    data class AchievementData(
        val id: Int,
        val type: String,
        val description: String,
        val tiers: Int,
        val baseGoal: Int,
        val goalIncrement: Int,
        val baseReward: Int,
        val rewardIncrement: Int,
        val rewardType: String,
        val rewardItem: String
    ) {
        constructor(result: ResultSet) : this(
            id = result.getInt("id"),
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
