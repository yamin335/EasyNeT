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

class RechargeHistDataSource(rechargeHistViewModel: RechargeHistViewModel) : PageKeyedDataSource<Long, RechargeTransaction>() {

    val viewModel = rechargeHistViewModel

    val handler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, RechargeTransaction>) {
        if (viewModel.checkNetworkStatus(viewModel.application)) {
            viewModel.viewModelScope.launch(handler) {
                val startDate = if (viewModel.fromDate.value.equals("dd/mm/yyyy", true)) "" else viewModel.fromDate.value!!
                val endDate = if (viewModel.toDate.value.equals("dd/mm/yyyy", true)) "" else viewModel.toDate.value!!
                when (val apiResponse = ApiResponse.create(viewModel.getRechargeHistory(1, 30,
                    viewModel.searchValue.value!!, startDate, endDate))) {
                    is ApiSuccessResponse -> {
                        if (!apiResponse.body.resdata?.listRecharge.isNullOrBlank()) {
                            val transactionList = JsonParser.parseString(apiResponse.body.resdata?.listRecharge).asJsonArray
                            val tempTransactionList: ArrayList<RechargeTransaction> = ArrayList()
                            for (transaction in transactionList) {
                                tempTransactionList.add(Gson().fromJson(transaction, RechargeTransaction::class.java))
                            }
                            callback.onResult(tempTransactionList, null, 2)
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

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, RechargeTransaction>) {
        if (viewModel.checkNetworkStatus(viewModel.application)) {
            viewModel.viewModelScope.launch(handler) {
                val startDate = if (viewModel.fromDate.value.equals("dd/mm/yyyy", true)) "" else viewModel.fromDate.value!!
                val endDate = if (viewModel.toDate.value.equals("dd/mm/yyyy", true)) "" else viewModel.toDate.value!!
                when (val apiResponse = ApiResponse.create(viewModel.getRechargeHistory(params.key, 30,
                    viewModel.searchValue.value!!, startDate, endDate))) {
                    is ApiSuccessResponse -> {
                        if (!apiResponse.body.resdata?.listRecharge.isNullOrBlank()) {
                            val transactionList = JsonParser.parseString(apiResponse.body.resdata?.listRecharge).asJsonArray
                            val tempTransactionList: ArrayList<RechargeTransaction> = ArrayList()
                            for (transaction in transactionList) {
                                tempTransactionList.add(Gson().fromJson(transaction, RechargeTransaction::class.java))
                            }
                            callback.onResult(tempTransactionList, params.key + 1)
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

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, RechargeTransaction>) {

    }
}
