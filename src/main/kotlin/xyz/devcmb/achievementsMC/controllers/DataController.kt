package xyz.devcmb.achievementsMC.controllers

import xyz.devcmb.achievementsMC.util.DataTypes
import xyz.devcmb.achievementsMC.util.Database

/*
TODO list
[x] Fetch db data on server open
[ ] Replicate back to db on server close
[ ] Fetch player data upon player join
[ ] Replicate player data to db on player leave
[ ] Replicate player data to db on server close (might call the above event, investigate this @29cmb)
*/

class DataController : IController {
    lateinit var achievements: HashMap<Number, DataTypes.AchievementData>
        private set

    override fun init() {
        achievements = Database.getAchievements()
    }
}