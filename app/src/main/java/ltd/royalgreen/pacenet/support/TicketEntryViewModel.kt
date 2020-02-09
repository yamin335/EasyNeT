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
import javax.inject.Inject

class TicketEntryViewModel @Inject constructor(private val application: Application, private val repository: SupportRepository) : BaseViewModel() {

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