package com.anvipus.explore.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anvipus.explore.db.NotifDao
import com.anvipus.explore.model.Messages
import com.anvipus.explore.model.Notification

@Database(
    entities = [
        Notification::class,
        Messages::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

//    abstract fun pointDao(): PointDao
//    abstract fun homeDao(): HomeDao
//    abstract fun historyDao(): HistoryDao
//    abstract fun cardDao(): CardDao
//    abstract fun cardTrxDao(): CardTrxDao
//    abstract fun bankDao(): BankDao
//    abstract fun productDao(): ProductDao
//    abstract fun topupDao(): TopupDao
    abstract fun notifDao(): NotifDao

}