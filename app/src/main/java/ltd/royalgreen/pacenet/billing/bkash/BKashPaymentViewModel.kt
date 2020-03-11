package ltd.royalgreen.pacenet.billing.bkash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.billing.BillingRepository
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import javax.inject.Inject

class BKashPaymentViewModel @Inject constructor(private val application: Application, private val repository: BillingRepository) : BaseViewModel() {

    val resBkash: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val bKashPaymentStatus: MutableLiveData<Pair<Boolean, String>> by lazy {
        MutableLiveData<Pair<Boolean, String>>()
    }

    var bkashPaymentExecuteJson = JsonObject()

    var bkashToken: String? = ""

    fun createBkashCheckout(paymentRequest: PaymentRequest?, createBkash: CreateBkashModel?) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.bkashCreatePaymentRepo(paymentRequest, createBkash))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue("SUCCESS")
                        val paymentCreateResponse = apiResponse.body
                        if (!paymentCreateResponse.resdata?.resbKash.isNullOrBlank()) {
                            apiCallStatus.postValue("SUCCESS")
                            resBkash.postValue(paymentCreateResponse.resdata?.resbKash)
                        } else {
                            apiCallStatus.postValue("NO_DATA")
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
    }

    fun executeBkashPayment() {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.bkashExecutePaymentRepo(bkashPaymentExecuteJson, bkashToken))) {
                    is ApiSuccessResponse -> {
                        val paymentExecuteResponse = apiResponse.body
                        if (!paymentExecuteResponse.resdata?.resExecuteBk.isNullOrBlank()) {
                            apiCallStatus.postValue("SUCCESS")
                            saveBkashNewRecharge(paymentExecuteResponse.resdata?.resExecuteBk!!)
                        } else {
                            apiCallStatus.postValue("NO_DATA")
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
    }

    fun saveBkashNewRecharge(bkashPaymentResponse: String) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.bkashPaymentSaveRepo(bkashPaymentResponse))) {
                    is ApiSuccessResponse -> {
                        val rechargeFinalSaveResponse = apiResponse.body
                        bKashPaymentStatus.postValue(Pair(rechargeFinalSaveResponse.resdata.resstate ?: false, rechargeFinalSaveResponse.resdata.message))
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