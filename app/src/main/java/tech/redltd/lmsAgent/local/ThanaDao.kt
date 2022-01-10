package tech.redltd.lmsAgent.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ThanaDao {
    @Query("SELECT * from thana")
    fun getAllDistrict(): LiveData<List<Thana>>

    @Query("SELECT * from thana where districtid=:districtId")
    fun getThanaByDistrict( districtId:Int ): LiveData<List<Thana>>

    @Insert
    suspend fun insertAll(thanas:List<Thana>)

    @Query("DELETE FROM thana")
    suspend fun deleteAll()
}
