package com.anvipus.explore.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class NotifData(
    @Json(name = "notification_list")
    val notificationList: List<Notification>?
)

@JsonClass(generateAdapter = true)
data class MessagesData(
    @Json(name = "notification_list")
    val messagesList: List<Messages>?
)

@Entity
@JsonClass(generateAdapter = true)
data class Notification(
    @Json(name = "data")
    @Embedded(prefix = "data_")
    val `data`: Data?,
    @Json(name = "fk_wallet_id")
    val fkWalletId: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "pkey")
    @PrimaryKey
    val pkey: String,
    @Json(name = "rec_timestamp")
    val recTimestamp: Long?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "type")
    val type: String?
) : Serializable

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "status")
    val status: Int?,
    @Json(name = "note")
    val note: String?,
    @Json(name = "card_fee")
    val card_fee: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "spin_admin")
    val spin_admin: String?,
    @Json(name = "admin")
    val admin: String?,
    @Json(name = "unique_tid")
    val unique_tid: String?,
    @Json(name = "serial_no")
    val serial_no: String?,
    @Json(name = "spin_mdr")
    val spin_mdr: String?,
    @Json(name = "month")
    val month: String?,
    @Json(name = "penalty")
    val penalty: String?,
    @Json(name = "ref_no")
    val ref_no: String?,
    @Json(name = "amount")
    val amount: String?,
    @Json(name = "trans_date")
    val trans_date: String?,
    @Json(name = "spin_fixed_fee")
    val spin_fixed_fee: String?,
    @Json(name = "timestamp")
    val timestamp: String?,
    @Json(name = "biller_admin")
    val biller_admin: String?,
    @Json(name = "terminal_no")
    val terminal_no: String?,
    /*@Json(name = "response")
    @Embedded(prefix = "response_")
    val response: ResponseNotif?,*/
    @Json(name = "type")
    val type: String?
)

@JsonClass(generateAdapter = true)
data class ResponseNotif(
    @Json(name = "date_time")
    val date_time: String?,
    @Json(name = "terminal_type")
    val terminal_type: String?,
    @Json(name = "keyword")
    val keyword: String?,
    @Json(name = "data")
    @Embedded(prefix = "response_data_")
    val data: ResponseData?,
    @Json(name = "terminal_id")
    val terminal_id: String?,
    @Json(name = "trx_id")
    val trx_id: String?,
    @Json(name = "rc")
    val rc: String?,
    @Json(name = "product_code")
    val product_code: String?,
    @Json(name = "partner_id")
    val partner_id: String?
)

@JsonClass(generateAdapter = true)
data class ResponseData(
    @Json(name = "admin_fee")
    val admin_fee: String?,
    @Json(name = "periode")
    val periode: String?,
    @Json(name = "receipt_code")
    val receipt_code: String?,
    @Json(name = "total_amount")
    val total_amount: String?,
    @Json(name = "total_person")
    val total_person: String?,
    @Json(name = "cust_name")
    val cust_name: String?,
    @Json(name = "amount")
    val amount: String?,
    @Json(name = "cust_id")
    val cust_id: String?
)

@Entity
@JsonClass(generateAdapter = true)
data class Messages(
    @Json(name = "data")
    @Embedded(prefix = "data_")
    val `data`: Data?,
    @Json(name = "fk_wallet_id")
    val fkWalletId: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "pkey")
    @PrimaryKey
    val pkey: String,
    @Json(name = "rec_timestamp")
    val recTimestamp: Long?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "type")
    val type: String?
) : Serializable