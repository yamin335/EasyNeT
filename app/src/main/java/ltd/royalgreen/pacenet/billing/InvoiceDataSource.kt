package ltd.royalgreen.pacenet.billing

import androidx.lifecycle.viewModelScope
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse

class InvoiceDataSource(invoiceViewModel: InvoiceViewModel) : PageKeyedDataSource<Long, Invoice>() {

    val viewModel = invoiceViewModel

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, Invoice>) {
        if (viewModel.checkNetworkStatus(viewModel.application)) {
            viewModel.apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                viewModel.apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModel.viewModelScope.launch(handler) {
                val startDate = if (viewModel.fromDate.value.equals("dd/mm/yyyy", true)) "" else viewModel.fromDate.value!!
                val endDate = if (viewModel.toDate.value.equals("dd/mm/yyyy", true)) "" else viewModel.toDate.value!!
                when (val apiResponse = ApiResponse.create(viewModel.getInvoiceList(1, 30,
                    viewModel.searchValue.value!!, startDate, endDate))) {
                    is ApiSuccessResponse -> {
                        if (!apiResponse.body.resdata?.userinvoiceList.isNullOrBlank()) {
                            val invoiceList = JsonParser.parseString(apiResponse.body.resdata?.userinvoiceList).asJsonArray
                            val tempInvoiceList: ArrayList<Invoice> = ArrayList()
                            for (invoice in invoiceList) {
                                tempInvoiceList.add(Gson().fromJson(invoice, Invoice::class.java))
                            }
                            callback.onResult(tempInvoiceList, null, 2)
                        }
                        viewModel.apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        viewModel.apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        viewModel.apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Invoice>) {
        if (viewModel.checkNetworkStatus(viewModel.application)) {
            viewModel.apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                viewModel.apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModel.viewModelScope.launch(handler) {
                val startDate = if (viewModel.fromDate.value.equals("dd/mm/yyyy", true)) "" else viewModel.fromDate.value!!
                val endDate = if (viewModel.toDate.value.equals("dd/mm/yyyy", true)) "" else viewModel.toDate.value!!
                when (val apiResponse = ApiResponse.create(viewModel.getInvoiceList(params.key, 30,
                    viewModel.searchValue.value!!, startDate, endDate))) {
                    is ApiSuccessResponse -> {
                        if (!apiResponse.body.resdata?.userinvoiceList.isNullOrBlank()) {
                            val invoiceList = JsonParser.parseString(apiResponse.body.resdata?.userinvoiceList).asJsonArray
                            val tempInvoiceList: ArrayList<Invoice> = ArrayList()
                            for (invoice in invoiceList) {
                                tempInvoiceList.add(Gson().fromJson(invoice, Invoice::class.java))
                            }
                            callback.onResult(tempInvoiceList, params.key + 1)
                        }
                        viewModel.apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        viewModel.apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        viewModel.apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Invoice>) {

    }
}
