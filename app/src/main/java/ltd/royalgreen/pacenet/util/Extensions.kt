package ltd.royalgreen.pacenet.util

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import okhttp3.MultipartBody
import java.io.File
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.math.BigDecimal
import java.math.RoundingMode

fun <T> MutableLiveData<MutableList<T>>.addNewItem(item: T) {
    val oldValue = this.value ?: mutableListOf()
    oldValue.add(item)
    this.value = oldValue
}

fun <T> MutableLiveData<MutableList<T>>.addNewItemAt(index: Int, item: T) {
    val oldValue = this.value ?: mutableListOf()
    oldValue.add(index, item)
    this.value = oldValue
}

fun <T> MutableLiveData<MutableList<T>>.removeItemAt(index: Int) {
    if (!this.value.isNullOrEmpty()) {
        val oldValue = this.value
        oldValue?.removeAt(index)
        this.value = oldValue
    } else {
        this.value = mutableListOf()
    }
}

fun File.asFilePart(): MultipartBody.Part {
    val attachedFile = this.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("attachedFileComment", this.name, attachedFile)
}

fun Uri.asFile(context: Application) = FileUtils.getFileFromUri(context, this)

fun Double.toRounded(digit: Int): Double {
    return BigDecimal(this).setScale(digit, RoundingMode.HALF_UP).toDouble()
}

inline fun<T> T?.returnIfNull(nullClause: () -> Nothing): T {
    return this ?: nullClause()
}

fun String.formatDateTime(): Pair<String, String> {
    val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val formattedDateTime: Pair<String, String>
    if (this.contains("T")) {
        val tempStringArray = this.split("T")
        var tempString1 = tempStringArray[1]
        if (tempString1.contains(".")){
            tempString1 = tempString1.split(".")[0]
            tempString1 = when {
                tempString1.split(":")[0].toInt()>12 -> {
                    val hour = tempString1.split(":")[0].toInt()
                    val minute = tempString1.split(":")[1].toInt()
                    val seconds = tempString1.split(":")[2].toInt()
                    "${hour-12}:$minute pm"
                }
                tempString1.split(":")[0].toInt() == 0 -> {
                    val hour = tempString1.split(":")[0].toInt()
                    val minute = tempString1.split(":")[1].toInt()
                    val seconds = tempString1.split(":")[2].toInt()
                    "${hour+12}:$minute am"
                }
                tempString1.split(":")[0].toInt()==12 -> {
                    val hour = tempString1.split(":")[0].toInt()
                    val minute = tempString1.split(":")[1].toInt()
                    val seconds = tempString1.split(":")[2].toInt()
                    "$hour:$minute pm"
                }
                else -> {
                    val hour = tempString1.split(":")[0].toInt()
                    val minute = tempString1.split(":")[1].toInt()
                    val seconds = tempString1.split(":")[2].toInt()
                    "$hour:$minute am"
                }
            }
        } else {
            tempString1 = when {
                tempString1.split(":")[0].toInt()>12 -> {
                    val hour = tempString1.split(":")[0].toInt()
                    val minute = tempString1.split(":")[1].toInt()
                    val seconds = tempString1.split(":")[2].toInt()
                    "${hour-12}:$minute pm"
                }
                tempString1.split(":")[0].toInt() == 0 -> {
                    val hour = tempString1.split(":")[0].toInt()
                    val minute = tempString1.split(":")[1].toInt()
                    val seconds = tempString1.split(":")[2].toInt()
                    "${hour+12}:$minute am"
                }
                tempString1.split(":")[0].toInt()==12 -> {
                    val hour = tempString1.split(":")[0].toInt()
                    val minute = tempString1.split(":")[1].toInt()
                    val seconds = tempString1.split(":")[2].toInt()
                    "$hour:$minute pm"
                }
                else -> {
                    val hour = tempString1.split(":")[0].toInt()
                    val minute = tempString1.split(":")[1].toInt()
                    val seconds = tempString1.split(":")[2].toInt()
                    "$hour:$minute am"
                }
            }
        }
        val year = tempStringArray[0].split("-")[0]
        val month = tempStringArray[0].split("-")[1]
        val day = tempStringArray[0].split("-")[2]
        formattedDateTime = Pair("$day ${months[month.toInt() - 1]} $year", tempString1)
    } else {
        formattedDateTime = Pair(this, "")
    }
    return  formattedDateTime
}