package ltd.royalgreen.pacenet.support

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.android.synthetic.main.billing_recharge_row.view.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import ltd.royalgreen.pacenet.util.DefaultResponse
import ltd.royalgreen.pacenet.util.asFile
import ltd.royalgreen.pacenet.util.asFilePart
import okhttp3.MultipartBody
import javax.inject.Inject

class ConversationDetailViewModel @Inject constructor(private val application: Application, private val repository: SupportRepository) : BaseViewModel() {

    val newMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val messageList: MutableLiveData<ArrayList<TicketConversation>> by lazy {
        MutableLiveData<ArrayList<TicketConversation>>()
    }

    var fileUriList: ArrayList<Uri> = ArrayList()

    var backupFileUri: Uri? = null

    val ticketNo: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val ticketSubject: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val ticketCategory: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val ticketStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val ticketDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun entryNewComment(ispTicketId: String): MutableLiveData<DefaultResponse> {
        val result = MutableLiveData<DefaultResponse>()

        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }

        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch(handler) {
                val attachedFileList = ArrayList<MultipartBody.Part>()
                for (uri in fileUriList) {
                    val file = uri.asFile(application)?.asFilePart()
                    file?.let {
                        attachedFileList.add(it)
                    }
                }
                when (val apiResponse = ApiResponse.create(repository.ticketCommentEntryRepo(ispTicketId, newMessage.value!!, attachedFileList))) {
                    is ApiSuccessResponse -> {
                        result.postValue(apiResponse.body)
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
        return result
    }

    fun getTicketConversation(id: Long) {
        if (checkNetworkStatus(application)) {
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.ticketConversationRepo(id))) {
                    is ApiSuccessResponse -> {
                        messageList.postValue(apiResponse.body.resdata?.objCrmIspTicket?.listIspTicketConversation)
                        ticketDate.postValue(formatDateTime(apiResponse.body.resdata?.objCrmIspTicket?.createDate))
                        ticketNo.postValue("Ticket No: ${apiResponse.body.resdata?.objCrmIspTicket?.ispTicketNo}")
                        ticketStatus.postValue(apiResponse.body.resdata?.objCrmIspTicket?.status)
                        ticketSubject.postValue(apiResponse.body.resdata?.objCrmIspTicket?.ticketSummary)
                        ticketCategory.postValue(apiResponse.body.resdata?.objCrmIspTicket?.ticketCategory)
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
    }

    fun formatDateTime(value: String?): String {
        var formattedDateTime = ""
        if (value != null && value.contains("T")) {
            val tempStringArray = value.split("T")
            var tempString1 = tempStringArray[1]
            if (tempString1.contains(".")){
                tempString1 = tempString1.split(".")[0]
                tempString1 = when {
                    tempString1.split(":")[0].toInt()>12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour-12}:$minute:$seconds PM"
                    }
                    tempString1.split(":")[0].toInt() == 0 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour+12}:$minute:$seconds AM"
                    }
                    tempString1.split(":")[0].toInt()==12 -> "$tempString1 PM"
                    else -> "$tempString1 AM"
                }
            } else {
                tempString1 = when {
                    tempString1.split(":")[0].toInt()>12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour-12}:$minute:$seconds PM"
                    }
                    tempString1.split(":")[0].toInt() == 0 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour+12}:$minute:$seconds AM"
                    }
                    tempString1.split(":")[0].toInt()==12 -> "$tempString1 PM"
                    else -> "$tempString1 AM"
                }
            }
            val year = tempStringArray[0].split("-")[0]
            val month = tempStringArray[0].split("-")[1]
            val day = tempStringArray[0].split("-")[2]
            formattedDateTime = "$day-$month-$year  $tempString1"
        } else {
            formattedDateTime = value ?: ""
        }
        return  formattedDateTime
    }
}