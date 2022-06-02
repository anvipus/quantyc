package com.anvipus.library.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.util.Base64
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import com.anvipus.library.BuildConfig
import com.anvipus.library.model.months
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException
import java.net.URL
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

object Helpers {

    fun getHmacMd5(value: String): String? {
        try {
            val result = hmacDigest(
                Base64.encodeToString(value.toByteArray(), Base64.DEFAULT).trim(),
                BuildConfig.KUNCI_GARAM,
                "HmacMD5"
            )
            Timber.d("hmac result: ${result}")
            return result
        }catch (e: Exception){
            e.printStackTrace()
        }
        return ""
    }

    fun getMd5Wms(value: String, msisdn: String): String? {
        try {
            val msisdn_suffix = msisdn.substring(msisdn.length - 6)


            val result = md5(value+msisdn_suffix)
            Timber.d("md5 wms result: ${result}")
            return result
        }catch (e: Exception){
            e.printStackTrace()
        }
        return ""
    }

    fun getSHA256(value: String): String {
        Timber.e("getSHA256: $value")
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val msgDigest = digest.digest(value.toByteArray(charset("US-ASCII")))
            val hexString = StringBuilder()
            msgDigest.forEach {
                var h = Integer.toHexString((0xFF and it.toInt()))
                while (h.length < 2)
                    h = "0$h"
                hexString.append(h)
            }
            Timber.e("sha256 result: $hexString.toString()")
            return hexString.toString()
        } catch (ignore: NoSuchAlgorithmException) {
        } catch (ignore: UnsupportedEncodingException) {
        }

        return ""
    }

    fun md5(s: String): String? {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest: MessageDigest = MessageDigest
                .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest: ByteArray = digest.digest()

            // Create Hex String
            val hexString = java.lang.StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun hmacDigest(msg: String, keyString: String, algo: String?): String? {
        var digest: String? = null
        try {
            val key = SecretKeySpec(keyString.toByteArray(charset("UTF-8")), algo)
            val mac = Mac.getInstance(algo)
            mac.init(key)
            val bytes = mac.doFinal(msg.toByteArray(charset("ASCII")))
            val hash = StringBuffer()
            for (i in bytes.indices) {
                val hex = Integer.toHexString(0xFF and bytes[i].toInt())
                if (hex.length == 1) {
                    hash.append('0')
                }
                hash.append(hex)
            }
            digest = hash.toString()
        } catch (e: UnsupportedEncodingException) {
        } catch (e: InvalidKeyException) {
        } catch (e: NoSuchAlgorithmException) {
        }
        return digest
    }

    fun verifyInstallerId(context: Context): Boolean {
        // A list with valid installers package name
        val validInstallers: List<String> =
            ArrayList(Arrays.asList("com.android.vending", "com.google.android.feedback","dev.firebase.appdistribution"))

        // The package name of the app that has installed your app
        val installer = context.packageManager.getInstallerPackageName(context.packageName)
        Timber.d("verifyInstaller")
        Timber.d("value: ${installer}")
        Timber.d("value: ${installer != null && validInstallers.contains(installer)}")

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer)
    }

    fun isFirstInstall(context: Context): Boolean {
        return try {
            val firstInstallTime =
                context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            val lastUpdateTime =
                context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
            firstInstallTime == lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            true
        }
    }

    fun isInstallFromUpdate(context: Context): Boolean {
        return try {
            val firstInstallTime =
                context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            val lastUpdateTime =
                context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
            firstInstallTime != lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun getIPAddressLocal(useIPv4: Boolean): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> =
                    Collections.list(intf.getInetAddresses())
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress()) {
                        val sAddr: String = addr.getHostAddress()
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                    0,
                                    delim
                                ).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
        } // for now eat exceptions
        return ""
    }

    suspend fun getPublicIpAddress(): String?{
        return try {
            withContext(Dispatchers.IO) {
                getExternalIpAddress()
            }
        } catch (e: SocketTimeoutException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun getExternalIpAddress(): String? {
        val whatismyip = URL("https://checkip.amazonaws.com")
        var `in`: BufferedReader? = null
        return try {
            `in` = BufferedReader(
                InputStreamReader(
                    whatismyip.openStream()
                )
            )
            `in`.readLine()
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getUserAgent(mContext: Context): String?{
        return WebView(mContext).getSettings().getUserAgentString()
    }

    private fun getNavigationBarHeight(windowManager: WindowManager): Int {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(metrics)
                val usableHeight = metrics.heightPixels
                windowManager.defaultDisplay.getRealMetrics(metrics)
                val realHeight = metrics.heightPixels
                return if (realHeight > usableHeight) realHeight - usableHeight else 0
            }
            return 0
        }catch (e: Exception){
            return 0
        }

    }

    fun getScreenHeight(windowManager: WindowManager): Int{
        try {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels + getNavigationBarHeight(windowManager)
            return height
        }catch (e: Exception){
            return 0
        }

    }

    fun getScreenWeight(windowManager: WindowManager): Int{
        try {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            return width
        }catch (e: Exception){
            return  0
        }

    }

    fun formatSize(size: Long): String? {
        var size = size
        var suffix: String? = null
        if (size >= 1024) {
            suffix = "KB"
            size /= 1024
            if (size >= 1024) {
                suffix = "MB"
                size /= 1024
            }
        }
        val resultBuffer =
            StringBuilder(java.lang.Long.toString(size))
        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    fun getTotalInternalMemorySize(): String? {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return formatSize(totalBlocks * blockSize)
    }

    fun getAvailableInternalMemorySize(): String? {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return formatSize(availableBlocks * blockSize)
    }

    fun getRamSize(mContext: Context): String? {
        val actManager =
            mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        val totalMemory: Long = memInfo.totalMem/ (1024 * 1024)

        return totalMemory.toString()+" MB"
    }

    fun getAvailableRam(mContext: Context): String? {
        val actManager =
            mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        val vailMem: Long = memInfo.availMem/ (1024 * 1024)

        return vailMem.toString()+" MB"
    }

    @Throws(IOException::class)
    fun getCPUName(): String? {
        var cpuName: String? = null
        val br = BufferedReader(FileReader("/proc/cpuinfo"))
        var str: String? = null
        while (br.readLine().also { str = it } != null) {
            val data = str!!.split(":".toRegex()).toTypedArray()
            if (data.size > 1) {
                var key = data[0].trim { it <= ' ' }.replace(" ", "_")
                if (key == "model_name") key = "cpu_model"
                cpuName = data[1].trim { it <= ' ' }
            }
        }
        br.close()
        return cpuName
    }

    fun getNext12Month(): MutableList<months>{
        val monthList: MutableList<months> = ArrayList()
        var spf = SimpleDateFormat("MMMM yyyy", Locale("in", "ID"))
        var spf2 = SimpleDateFormat("MM-yyyy", Locale("in", "ID"))
        for(x in 1..12){
            val months = months()
            months.id = x
            var date = getNextMonthDays(x - 1)
            months.date = date
            months.monthYear = spf.format(date)
            months.monthNumber = spf2.format(date)
            monthList.add(months)
        }
        return monthList
    }

    fun getNextMonthDays(param: Int): Date? {
        val cl: Calendar = Calendar.getInstance()
        var displayedDate = Date()
        cl.setTime(displayedDate)
        cl.add(Calendar.MONTH, param)
        displayedDate = cl.getTime()
        return displayedDate
    }

    fun hideSoftKeyboard(mActivity: Activity) {
        try {
            val inputMethodManager = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mActivity.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getDateNow(format: String): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(Date())
    }

    fun firebaseAnalyticEventBundle(
        page: String,
        key_event: String?,
        key_activity: String?,
        userId: String?,
        env: String
    ): Bundle {
        val timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val bundle = Bundle()
        bundle.putString(("page"), page)
        if(key_event != null){
            bundle.putString(("key_event"), key_event)
        }
        if(key_activity != null){
            bundle.putString(("key_activity"), key_activity)
        }
        if(userId != null){
            bundle.putString(("userId"), userId)
        }
        bundle.putString(("environtment"), env)
        bundle.putString(("device_type"), "android")
        bundle.putString(("timestamp"), timestamp)
        bundle.putString(("fingerprint"), "${userId}_android_${timestamp}_${env}_${page}")
        return bundle
    }
}