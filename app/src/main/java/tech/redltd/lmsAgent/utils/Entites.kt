package tech.redltd.lmsAgent.utils

data class Division(val id:Int,val name:String,val localname:String){
    override fun toString(): String {
        return name
    }
}

data class District(val id:Int,val name:String,val localname:String,val division_id:Int){
    override fun toString(): String {
        return name
    }
}

data class Thana(val id:Int,val name:String,val localname:String,val districtid:Int){
    override fun toString(): String {
        return name
    }
}