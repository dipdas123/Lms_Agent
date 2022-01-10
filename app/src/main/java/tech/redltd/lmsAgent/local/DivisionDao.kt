package tech.redltd.lmsAgent.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DivisionDao {
    @Query("SELECT * from division")
    fun getAllDivision(): LiveData<List<Division>>

    @Insert
    suspend fun insertAll(division:List<Division>)

    @Query("DELETE FROM division")
    suspend fun deleteAll()
}