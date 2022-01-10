package tech.redltd.lmsAgent.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Division(
    @PrimaryKey
    val id: Int,
    val name: String,
    val localname: String
)

@Entity
data class District(
    @PrimaryKey
    val id:Int,
    val division_id:Int,
    val name: String,
    val localname:String
)

@Entity
data class Thana(
    @PrimaryKey
    val id: Int,
    val districtid: Int,
    val name: String,
    val localname: String
)