package com.anvipus.explore.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anvipus.explore.model.Messages
import com.anvipus.explore.model.Notification

@Dao
interface NotifDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Notification>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessages(list: List<Messages>?)

    @Query("SELECT * FROM notification WHERE fkWalletId=:wallet_id")
    fun getNotifs(wallet_id: String): LiveData<List<Notification>>

    @Query("SELECT * FROM messages WHERE fkWalletId=:wallet_id")
    fun getMessages(wallet_id: String): LiveData<List<Messages>>

    @Query("DELETE FROM notification")
    fun delete()

    @Query("DELETE FROM messages")
    fun deleteMessages()

}