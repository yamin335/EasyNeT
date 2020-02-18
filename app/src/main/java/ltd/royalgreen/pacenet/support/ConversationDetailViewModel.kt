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

    val selectedTicketCategory: MutableLiveData<TicketCategory> by lazy {
        MutableLiveData<TicketCategory>()
    }

    fun getTicketConversation(id: Long): MutableLiveData<ArrayList<TicketConversation>> {
        val ticketConversation = MutableLiveData<ArrayList<TicketConversation>>()
        if (checkNetworkStatus(application)) {
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.ticketConversationRepo(id))) {
                    is ApiSuccessResponse -> {
                        ticketConversation.postValue(apiResponse.body.resdata?.objCrmIspTicket?.listIspTicketConversation)
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
        return ticketConversation
    }
}