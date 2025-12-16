package xyz.devcmb.achievementsMC.util

import org.bukkit.configuration.file.FileConfiguration
import xyz.devcmb.achievementsMC.AchievementsMC
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

/*
Gonna ramble here on how I intend to do this
To avoid any configuration (for achievements) in the files, all things will be done in-game through an inventory UI
Initially, there will be no configured achievements, so someone with permissions will need to do that

ACHIEVEMENT SCHEMA:
Here is everything an admin can set
    - The type of achievement from the list (type)
    - The description in the player's achievement menu (description)
    - The amount of tiers (tiers)
    - The base goal for each tier (tier_base_goal)
    - The amount the goal increases as the tier does (tier_goal_increment)
    - The reward for each tier (tier_base_reward)
    - The amount the reward increases as the tier does (tier_reward_increment)
    - The reward type, either `item` or `vault_currency` (reward_type)
    - The reward item, a `minecraft:item_name` or `money` (reward_item)
Other values that should be kept track of:
    - ID (id)

PROGRESS SCHEMA:
TODO
 */

object Database {
    var connection: Connection? = null
    fun connect() {
        val config: FileConfiguration = AchievementsMC.plugin.config
        val host = config.getString("database.host")
        val port = config.getInt("database.port")
        val username = config.getString("database.username")
        val password = config.getString("database.password")
        val database = config.getString("database.database")

        val url = "jdbc:mysql://$host:$port/$database?useSSL=false"

        try {
            connection = DriverManager.getConnection(url, username, password)
            setupColumns()
            AchievementsMC.pluginLogger.info("Successfully connected to the MySQL database.")
        } catch (e: SQLException) {
            AchievementsMC.pluginLogger.severe("Failed to connect to the MySQL database: " + e.message)
        }
    }

    private fun setupColumns() {
        val statement: PreparedStatement = connection!!.prepareStatement(
            """
                CREATE TABLE IF NOT EXISTS `anc_achievements` (
                    `id` INT NOT NULL AUTO_INCREMENT COMMENT 'The internal ID for achievements',
                    `type` VARCHAR(64) NOT NULL COMMENT 'The type of achievement',
                    `description` TEXT NOT NULL COMMENT 'The description displayed in the player\'s achievement menu',
                    `tiers` INT NOT NULL COMMENT 'The total amount of tiers an achievement can have',
                    `tier_base_goal` INT NOT NULL COMMENT 'The base amount for the achievement at tier 1',
                    `tier_goal_increment` INT NOT NULL COMMENT 'The amount the goal increases for each tier.',
                    `tier_base_reward` INT NOT NULL COMMENT 'The base reward amount at tier 1',
                    `tier_reward_increment` INT NOT NULL COMMENT 'The amount the reward increases at each tier',
                    `reward_type` VARCHAR(32) NOT NULL COMMENT 'item or vault_currency',
                    `reward_item` VARCHAR(64) NOT NULL COMMENT 'The minecraft item or "money"',
                    PRIMARY KEY (`id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """
        )

        // TODO: User progress storing

        statement.executeUpdate()
    }

    fun getAchievements() : HashMap<Number, DataTypes.Achievement> {
        val output: HashMap<Number, DataTypes.Achievement> = HashMap()
        val statement: PreparedStatement = connection!!.prepareStatement("SELECT * FROM anc_achievements")
        val result = statement.executeQuery()

        while (result.next()) {
            val achievement: DataTypes.Achievement = DataTypes.Achievement(result)
            output[achievement.id] = achievement
        }

        return output
    }

    fun close() {
        connection?.close()
    }
}