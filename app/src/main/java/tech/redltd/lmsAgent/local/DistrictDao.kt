package tech.redltd.lmsAgent.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DistrictDao {

    @Query("SELECT * from district")
    fun getAllDistrict():LiveData<List<District>>

    @Query("SELECT * from district where division_id=:divisionId")
    fun getDistrictByDivision( divisionId:Int ):LiveData<List<District>>

    @Insert
    suspend fun insertAll(districts:List<District>)

    @Query("DELETE FROM district")
    suspend fun deleteAll()
}