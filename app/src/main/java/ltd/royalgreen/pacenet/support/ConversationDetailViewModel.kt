package ltd.royalgreen.pacenet.support

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
    }
}