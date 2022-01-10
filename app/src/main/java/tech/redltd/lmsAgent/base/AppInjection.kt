package tech.redltd.lmsAgent.base

import androidx.room.Room
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.redltd.lmsAgent.local.AppDatabase
import tech.redltd.lmsAgent.network.ApiService
import tech.redltd.lmsAgent.network.AspService
import tech.redltd.lmsAgent.utils.AppUtils

val appModule:Module = module {
    single { Room.databaseBuilder(get(),AppDatabase::class.java,"aniklmsdb")
        .fallbackToDestructiveMigration()
        .build()}
    single { AppUtils(get()) }
    single { ApiService() }
    single { AspService() }

}