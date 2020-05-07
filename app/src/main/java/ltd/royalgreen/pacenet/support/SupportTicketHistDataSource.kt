package ltd.royalgreen.pacenet.support

import androidx.lifecycle.viewModelScope
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.*
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse

class SupportTicketHistDataSource(supportViewModel: SupportViewModel) : PageKeyedDataSource<Long, SupportTicket>() {

    val viewModel = supportViewModel

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, SupportTicket>) {
        if (viewModel.checkNetworkStatus(viewModel.application)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }
            viewModel.viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(viewModel.getAllSupportTicketHist(1, 30,
                    null, null, null))) {
                    is ApiSuccessResponse -> {
                        if (!apiResponse.body.resdata?.listCrmIspTicket.isNullOrEmpty()) {
                            callback.onResult(apiResponse.body.resdata?.listCrmIspTicket!!, null, 2)
                        }
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, SupportTicket>) {
        if (viewModel.checkNetworkStatus(viewModel.application)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }
            viewModel.viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(viewModel.getAllSupportTicketHist(params.key, 30,
                    null, null, null))) {
                    is ApiSuccessResponse -> {
                        if (!apiResponse.body.resdata?.listCrmIspTicket.isNullOrEmpty()) {
                            callback.onResult(apiResponse.body.resdata?.listCrmIspTicket!!, params.key + 1)
                        }
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, SupportTicket>) {

    }
}
