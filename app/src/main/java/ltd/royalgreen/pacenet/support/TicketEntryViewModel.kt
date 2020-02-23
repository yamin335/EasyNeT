package ltd.royalgreen.pacenet.support

import android.app.Application
import android.net.Uri
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

class TicketEntryViewModel @Inject constructor(private val application: Application, private val repository: SupportRepository) : BaseViewModel() {

    val selectedTicketCategory: MutableLiveData<TicketCategory> by lazy {
        MutableLiveData<TicketCategory>()
    }

    val ticketSubject: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val ticketDescription: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val fileUriList: MutableLiveData<ArrayList<Uri>> by lazy {
        MutableLiveData<ArrayList<Uri>>()
    }

    lateinit var backupFileUri: Uri

    fun entryNewTicket(): MutableLiveData<DefaultResponse> {
        val result = MutableLiveData<DefaultResponse>()
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.ticketEntryRepo(ticketSubject.value!!, ticketDescription.value!!, selectedTicketCategory.value?.ispTicketCategoryId.toString()))) {
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

    fun getTicketCategory(): MutableLiveData<ArrayList<TicketCategory>> {
        val ticketCategories = MutableLiveData<ArrayList<TicketCategory>>()
        if (checkNetworkStatus(application)) {
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.ticketCategoryRepo())) {
                    is ApiSuccessResponse -> {
                        ticketCategories.postValue(apiResponse.body.resdata?.listTicketCategory)
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
        return ticketCategories
    }
}