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
import ltd.royalgreen.pacenet.util.*
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

class ConversationDetailViewModel @Inject constructor(private val application: Application, private val repository: SupportRepository) : BaseViewModel(application) {

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

        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                var attachedFile: MultipartBody.Part? = null

                for (uri in fileUriList) {
                    val path = FileUtils.getPathForMediaFile(application, uri)
                    path?.let {
                        attachedFile = File(it).asFilePart()
                    }
                }
//                for (uri in fileUriList) {
//                    val file = uri.asFile(application)?.asFilePart()
//                    file?.let {
//                        attachedFile = it
//                    }
//                }
                when (val apiResponse = ApiResponse.create(repository.ticketCommentEntryRepo(ispTicketId, newMessage.value!!, attachedFile))) {
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
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.ticketConversationRepo(id))) {
                    is ApiSuccessResponse -> {
                        messageList.postValue(apiResponse.body.resdata?.objCrmIspTicket?.listIspTicketConversation)
                        ticketDate.postValue(apiResponse.body.resdata?.objCrmIspTicket?.createDate?.formatDateTime()?.first)
                        ticketNo.postValue("Ticket No: ${apiResponse.body.resdata?.objCrmIspTicket?.ispTicketNo}")
                        ticketStatus.postValue(apiResponse.body.resdata?.objCrmIspTicket?.status)
                        ticketSubject.postValue(apiResponse.body.resdata?.objCrmIspTicket?.ticketSummary)
                        ticketCategory.postValue(apiResponse.body.resdata?.objCrmIspTicket?.ticketCategory)
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
    }
}