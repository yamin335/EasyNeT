package ltd.royalgreen.pacenet.support

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import ltd.royalgreen.pacenet.util.DefaultResponse
import javax.inject.Inject

class ConversationDetailViewModel @Inject constructor(private val application: Application, private val repository: SupportRepository) : BaseViewModel() {

    val newMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val messageList: MutableLiveData<ArrayList<TicketConversation>> by lazy {
        MutableLiveData<ArrayList<TicketConversation>>()
    }

    fun entryNewComment(ispTicketId: String): MutableLiveData<DefaultResponse> {
        val result = MutableLiveData<DefaultResponse>()
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.ticketCommentEntryRepo(ispTicketId, newMessage.value!!))) {
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