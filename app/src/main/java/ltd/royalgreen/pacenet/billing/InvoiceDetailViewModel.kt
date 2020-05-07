package ltd.royalgreen.pacenet.billing

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import javax.inject.Inject

class InvoiceDetailViewModel @Inject constructor(val application: Application, private val repository: BillingRepository) : BaseViewModel() {
    fun getInvoiceDetail(SDate: String, EDate: String, CDate: String, invId: Int, userPackServiceId: Int): LiveData<ArrayList<InvoiceDetail>> {

        val invoiceDetails = MutableLiveData<ArrayList<InvoiceDetail>>()

        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")

            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.invoiceDetailRepo(SDate, EDate, CDate, invId, userPackServiceId))) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body
                        if (!response.resdata?.userinvoiceDetail.isNullOrBlank()) {
                            val invoiceDetailList = JsonParser.parseString(apiResponse.body.resdata?.userinvoiceDetail).asJsonArray
                            val tempInvoiceList: ArrayList<InvoiceDetail> = ArrayList()
                            for (invoice in invoiceDetailList) {
                                tempInvoiceList.add(Gson().fromJson(invoice, InvoiceDetail::class.java))
                            }
                            invoiceDetails.postValue(tempInvoiceList)
                        }
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
        return invoiceDetails
    }
}