package com.anvipus.library.util.state

import android.content.Context
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.anvipus.library.util.Constants
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AccountManager(appContext: Context) {

    companion object {
        private const val KEY_POINT_DATA: String = "point_data"
        const val TAG: String = "AccountManager"
    }

    private var SESSION_NAME = ""
    private val SESSION_CUSTOMER = "`$SESSION_NAME`.user.obj"
    private val SESSION_API_KEY = "`$SESSION_NAME`.user.api.key"
    private val SESSION_PUSH_TOKEN = "`$SESSION_NAME`.user.push.token"


    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val masterKey = MasterKey.Builder(appContext).apply {
        setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    }.build()

    private val prefs = EncryptedSharedPreferences.create(
        appContext,
        "spin_encrypted_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val editor = prefs.edit()

    var ipAddress: String? = null
    var userAgent: String? = null
    var coordinate: String? = null
    var versionName: String? = null
    var versionCode: String? = null
    var sdkVersion: String? = null
    var deviceFingerprint: String? = null
    var authIdCobrand: String? = null
    var manufacture = Build.MANUFACTURER
    var model = Build.MODEL
    var brand = Build.BRAND

    val hasSession
        get() = getValueSession(SESSION_CUSTOMER).isNullOrEmpty().not()


    val hasApiKey: Boolean
        get() = getValueSession(SESSION_API_KEY).isNullOrEmpty().not()

    val hasPushToken: Boolean
        get() = getValueSession(SESSION_PUSH_TOKEN).isNullOrEmpty().not()


    val pushToken: String
        get() = this.prefs.getString(SESSION_PUSH_TOKEN, "").toString()



    fun saveApplicationId(applicationId: String) {
        SESSION_NAME = "${applicationId}.session"
    }

    fun savePushToken(token: String?) {
        this.editor.run {
            putString(SESSION_PUSH_TOKEN, token)
            apply()
        }
    }

    fun logout() {
        editor.remove(SESSION_CUSTOMER)
        editor.remove(SESSION_PUSH_TOKEN)
        editor.apply()
    }

    fun clearPushToken() {
        editor.remove(SESSION_PUSH_TOKEN)
        editor.apply()
    }

    private fun putValueSession(key: String, value: String) {
        editor.apply {
            putString(key, value)
            apply()
        }
    }

    private fun getValueSession(key: String): String? {

        if (this.prefs.contains(key).not()) return ""
        return this.prefs.getString(key, "")
    }

    fun putInteger(key: String, value: Int) {
        editor.apply {
            putInt(key, value)
            apply()
        }
    }

    fun getInteger(key: String): Int? {

        if (this.prefs.contains(key).not()) return 0
        return this.prefs.getInt(key, 0)
    }

    fun putString(key: String, value: String?) {
        editor.apply {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String): String? {

        if (this.prefs.contains(key).not()) return null
        return this.prefs.getString(key, null)
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.apply {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(key: String): Boolean {

        if (this.prefs.contains(key).not()) return false
        return this.prefs.getBoolean(key, false)
    }

    fun setFirstLogin(value: Boolean) {
        editor.apply {
            putBoolean(Constants.KEY_FIRST_LOGIN, value)
            apply()
        }
    }

    fun getFirstLogin(): Boolean {
        return this.prefs.getBoolean(Constants.KEY_FIRST_LOGIN, true)
    }

}