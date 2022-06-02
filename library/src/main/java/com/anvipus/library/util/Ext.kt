package com.anvipus.library.util

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.Image
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.anvipus.library.R
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.*
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun Context.resColor(res: Int): Int = ContextCompat.getColor(this, res)
fun Context.resDrawable(res: Int): Drawable? = ContextCompat.getDrawable(this, res)

fun Fragment.resColor(res: Int) = try {
    requireActivity().resColor(res)
} catch (e: Exception) {
    e.printStackTrace()
    0
}
fun Fragment.resDrawable(res: Int) = try {
    requireActivity().resDrawable(res)
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun Context.dp(value: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value.toFloat(),
    resources.displayMetrics
)

fun Context.alert(title: String, msg: String, cancelable: Boolean = false, ok: () -> Unit = {}): AlertDialog {
    return AlertDialog.Builder(this).create().apply {
        setTitle(title)
        setMessage(msg.fromHtml())
        setButton(AlertDialog.BUTTON_POSITIVE, "Ya") { dialog, _ ->
            ok.invoke()
            dialog.dismiss()
        }
        setCancelable(cancelable)
        setCanceledOnTouchOutside(cancelable)
        if(cancelable){
            setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }
        show()
    }
}

fun Context.alert(
    title: String,
    msg: String,
    positiveButton: String = "Ya",
    negativeButton:String = "Cancel",
    ok: () -> Unit = {},
    cancel: () -> Unit = {}
): AlertDialog {
    return AlertDialog.Builder(this).create().apply {
        setTitle(title)
        setMessage(msg.fromHtml())
        setButton(AlertDialog.BUTTON_POSITIVE, positiveButton) { dialog, _ ->
            ok.invoke()
            dialog.dismiss()
        }
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setButton(AlertDialog.BUTTON_NEGATIVE, negativeButton) { dialog, _ ->
            cancel.invoke()
            dialog.dismiss()
        }
        show()
    }
}

fun FragmentActivity.windowPercentage(percent: Int): Int {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)

    return (metrics.widthPixels / 100) * percent
}


fun View.closeKeyBoard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun EditText.string(): String = text.toString()

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(this, Observer(body))
}

val <T> T.exhaustive: T
    get() = this


fun Spinner.hintedAdapter(
    list: MutableList<String>,
    def: String = "",
    onItemSelected: ((position: Int) -> Unit)? = null
) {

    val hasDefTitle = def.isNotEmpty() && !def.contentEquals("_")

    if (hasDefTitle) list.add(0, def)

    val mAdapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list) {
        override fun isEnabled(position: Int): Boolean {
            return position != 0
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = super.getDropDownView(position, convertView, parent)
            val tv = view as TextView
            if (position == 0) {
                tv.setTextColor(Color.GRAY)
            } else tv.setTextColor(context.resColor(0)) //TODO
            return view
        }
    }
    mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {}
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
            if (position == 0 && def.isNotEmpty()) {
                (p1 as? TextView)?.setTextColor(Color.GRAY)
            } else {
                onItemSelected?.invoke(position - 1)
            }
            this@hintedAdapter.closeKeyBoard()
        }
    }

    this.adapter = mAdapter
}

fun dateString(time: Long, format: String = "dd MMM yyyy"): String {
    val date = Date(time)
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(date)
}

fun parseStringToDate(
    value: String,
    fromFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS",
    toFormat: String = "dd MMM yyyy"
): String {
    var outPut = ""
    val sFormat = SimpleDateFormat(fromFormat, Locale.getDefault())
    sFormat.timeZone = TimeZone.getTimeZone("GMT")
    var d: Date? = null
    try {
        d = sFormat.parse(value)
    } catch (ignore: ParseException) {

    } finally {
        if (d != null) {
            outPut = dateString(d.time, toFormat)
        }
    }

    return outPut
}

fun String.fromHtml(): Spanned {
    val tmpValue = if (isEmpty()) "<p></p>" else this

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(tmpValue, Html.FROM_HTML_MODE_LEGACY)
    } else Html.fromHtml(tmpValue)
}

//IMAGE

fun Bitmap.encodeBitmap(): File {

    // Initialize a new file instance to save bitmap object
    val file = createTempFile(UUID.randomUUID().toString(), ".jpeg")

    try {
        // Compress the bitmap and save in jpg format
        val stream: OutputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.JPEG, 60, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        Log.d("TAGGGS", "bitmapToFile: error", e)
    }

    return file
}

fun File.fileToBitmap(crop: Boolean = false, output: (Bitmap) -> Unit) = GlobalScope.launch(
    Dispatchers.IO) {

    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    //Correct image orientation in displaying
    fun neededRotation(ff: File): Int {
        try {
            val exif = ExifInterface(ff.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) return 270
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) return 180
            return if (orientation == ExifInterface.ORIENTATION_ROTATE_90) 90 else 0
        } catch (ignore: IOException) {
        }

        return 0
    }

    val bitmap = async {
        val tmp = BitmapFactory.decodeFile(absolutePath)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = calculateInSampleSize(options, tmp.width, tmp.height)
        options.inJustDecodeBounds = false
        val bmp = BitmapFactory.decodeFile(absolutePath, options)

        val matrix = Matrix()

        val width = bmp.width
        val height = bmp.height

        val size = width.coerceAtMost(height)
        val H = size - (size / 100 * 30)

        matrix.postRotate(neededRotation(this@fileToBitmap).toFloat())

        return@async Bitmap.createBitmap(tmp, 0, 0, tmp.width, tmp.height, matrix, false)
    }

    output.invoke(bitmap.await())
}

fun getUri(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    return Uri.parse(path)
}

@Throws(IOException::class)
fun getBitmap(context: Context, uri: Uri): Bitmap {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
}

fun Bitmap.bitmapToFile(context: Context?): File {
    // Get the context wrapper
    val wrapper = ContextWrapper(context)

    // Initialize a new file instance to save bitmap object
    var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
    file = File(file, "${UUID.randomUUID()}.png")

    try {
        // Compress the bitmap and save in jpg format
        val stream: OutputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        Log.d("TAGGGS", "bitmapToFile: error", e)
    }

    // Return the saved bitmap uri
    return file
}

fun Image.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun File.fileToBmp(): Bitmap? {

    //Correct image orientation in displaying
    fun neededRotation(ff: File): Int {
        try {
            val exif = ExifInterface(ff.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) return 270
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) return 180
            return if (orientation == ExifInterface.ORIENTATION_ROTATE_90) 90 else 0
        } catch (ignore: IOException) {
        }

        return 0
    }

    return try {
        val bmp = BitmapFactory.decodeFile(absolutePath)
        val matrix = Matrix()
        matrix.postRotate(neededRotation(this).toFloat())

        Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, false)
    } catch (e: NullPointerException) {
        e.printStackTrace()
        null
    }

}

fun EditText.attachAmountWatcher(tl: TextInputLayout? = null, minimum: Double = 0.0, max: Double? = 10000000.00) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            if (p0.toString().isEmpty()) return

            val value = p0.toString().replace("[^0-9]".toRegex(), "")

            if (value.isEmpty()) {
                setText("")
                return
            }

            removeTextChangedListener(this)

            setText(value.toDouble().toCurrency())

            setSelection(string().length)

            if (minimum > 0.0 && value.toDouble() < minimum) {
                isSelected = true
                tl?.error = "Minimum amount is ${minimum.toCurrency()}"
            } else {
                tl?.error = null
                isSelected = false
            }

            if (max != null && max < value.toDouble()) {
                isSelected = true
                tl?.error = "Maximum amount is ${max.toCurrency()}"

                setText(max.toCurrency())

            } else {
                tl?.error = null
                isSelected = false
            }

            addTextChangedListener(this)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    })
}

fun EditText.scanQrisAmountWatcher(tl: TextInputLayout? = null, minimum: Double = 0.0, max: Double? = 2000000.00, button: Button? = null) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            if (p0.toString().isEmpty()) return

            val value = p0.toString().replace("[^0-9]".toRegex(), "")

            if (value.isEmpty()) {
                setText("")
                return
            }

            removeTextChangedListener(this)

            setText(value.toDouble().toCurrency())

            setSelection(string().length)

            if (minimum > 0.0 && value.toDouble() < minimum) {
                isSelected = true
                tl?.error = "Minimum amount is ${minimum.toCurrency()}"
            } else {
                tl?.error = null
                isSelected = false
            }

            if (max != null && max < value.toDouble()) {
                isSelected = true
                tl?.error = "Maximum amount is ${max.toCurrency()}"
                button?.isEnabled = false

            } else {
                tl?.error = null
                isSelected = false
                button?.isEnabled = true
            }

            addTextChangedListener(this)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    })
}

fun ImageView.loadQrCode(value: String?) {

    if (value == null || value.isEmpty()) return

    val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = manager.defaultDisplay
    val point = Point()
    display.getSize(point)
    val width = point.x
    val height = point.y
    var smallerDimension = if (width < height) width else height

    val multiFormatWriter = MultiFormatWriter()
    val bitMatrix = multiFormatWriter.encode(value, BarcodeFormat.QR_CODE, smallerDimension, smallerDimension)
    val barcodeEncoder = BarcodeEncoder()
    val bitmap = barcodeEncoder.createBitmap(bitMatrix)
    setImageBitmap(bitmap)
}

@Throws(WriterException::class)
fun ImageView.loadQRCode(value: String?) {
    fun guessAppropriateEncoding(contents: CharSequence): String? {
        for (element in contents) {
            if (element.toInt() > 0xFF) {
                return "UTF-8"
            }
        }
        return null
    }

    val black = -0x1000000
    val ghostWhite = -0x60501

    if (value.isNullOrEmpty()) {
        return
    }

    val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = manager.defaultDisplay
    val point = Point()
    display.getSize(point)
    val width = point.x
    val height = point.y
    val smallerDimension = if (width < height) width else height

    var hints: MutableMap<EncodeHintType?, Any?>? = null
    val encoding = guessAppropriateEncoding(value)
    if (encoding != null) {
        hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = encoding
    }
    val writer = MultiFormatWriter()
    val result: BitMatrix
    result = try {
        writer.encode(value, BarcodeFormat.QR_CODE, smallerDimension, smallerDimension, hints)
    } catch (iae: IllegalArgumentException) {
        // Unsupported format
        return
    }
    val width2 = result.width
    val height2 = result.height
    val pixels = IntArray(width2 * height2)
    for (y in 0 until height2) {
        val offset = y * width2
        for (x in 0 until width2) {
            pixels[offset + x] = if (result[x, y]) black else ghostWhite
        }
    }
    val bitmap = Bitmap.createBitmap(
        width2, height2,
        Bitmap.Config.ARGB_8888
    )
    bitmap.setPixels(pixels, 0, width2, 0, 0, width2, height2)
    setImageBitmap(bitmap)
}


fun ImageView.toGrayscale(){
    val matrix = ColorMatrix().apply {
        setSaturation(0f)
    }
    colorFilter = ColorMatrixColorFilter(matrix)
}

fun String.cleanString() = this.replace("[^0-9]".toRegex(), "")

fun String.phoneString(): String{
    return if(startsWith("62"))this.replaceFirst("62", "0") else this
}

fun String.isValidPhone(): Boolean{
    return this.matches("^08?\\d{8,}".toRegex())
}

fun String.isValidPhoneV2(): Boolean{
    return this.matches("^8?\\d{8,}".toRegex())
}

fun Int.toNum(): String{
    val df = DecimalFormat("#,###", DecimalFormatSymbols())
    return df.format(this).replace(",", ".")
}

fun Double.toCurrency(roundMode: RoundingMode? = null): String {
    val df = DecimalFormat("#,###", DecimalFormatSymbols())
    return "Rp ${df.format(this).replace(",", ".")}"
}

fun Double.toCurrencyV2(roundMode: RoundingMode? = null): String {
    val df = DecimalFormat("#,###", DecimalFormatSymbols())
    return df.format(this).replace(",", ".")
}

fun String.isValidFormat(): Boolean {
    val regex = "^[A-Za-z](?!.*?[.',-]{2})(?!.*?[ ]{2})(?!.*?[.',-]\\s[.',-])[A-Za-z0-9 .',-]{2,50}\$"
    return this.matches(regex.toRegex())
}

fun getCurrentDate(): String {
    val date = Date()
    val dateFormatWithZone = SimpleDateFormat(
        Constants.FORMAT_DATE_V1,
        Locale.getDefault()
    )
    return dateFormatWithZone.format(date)
}

fun Int.dpToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    return (this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun String.handleLongText(context: Context, startIndex: Int, endIndex: Int): String {
    return if (length > 20) {
        String.format(
            context.getString(R.string.placeholder_ellipsize_end),
            this.substring(startIndex, endIndex)
        )
    } else {
        this
    }
}

fun String.totalPayment(context: Context): String = String.format(
    context.getString(R.string.placeholder_total_payment),
    this
)

fun String.motionPointsValueTotal(context: Context): String = String.format(
    context.getString(R.string.placeholder_total_motionpoints),
    this
)

fun Intent.isLaunchFromHistory(): Boolean =
    this.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY