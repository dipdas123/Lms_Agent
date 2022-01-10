package tech.redltd.lmsAgent.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Division::class,District::class,Thana::class],version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun districtDao() : DistrictDao
    abstract fun divisionDao():DivisionDao
    abstract fun thanaDao():ThanaDao
}