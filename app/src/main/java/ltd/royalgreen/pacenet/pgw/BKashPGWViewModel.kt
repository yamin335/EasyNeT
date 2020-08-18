package ltd.royalgreen.pacenet.pgw

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.billing.BillPaymentHelper
import ltd.royalgreen.pacenet.billing.BillingRepository
import ltd.royalgreen.pacenet.billing.bkash.CreateBkashModel
import ltd.royalgreen.pacenet.billing.bkash.PaymentRequest
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

class BKashPGWViewModel @Inject constructor(private val application: Application, private val repository: BillingRepository) : BaseViewModel(application) {

    val resBkash: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val resExecuteBkash: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var bkashPaymentExecuteJson = JsonObject()

    var bkashToken: String? = ""

    var dismissListener = MutableLiveData<Pair<Boolean, String>>()

    fun createBkashCheckout(paymentRequest: PaymentRequest?, createBkash: CreateBkashModel?, billPaymentHelper: BillPaymentHelper) {
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.bkashCreatePaymentRepo(paymentRequest, createBkash, billPaymentHelper))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue("SUCCESS")
                        val paymentCreateResponse = apiResponse.body.resdata
                        if (!paymentCreateResponse?.resbKash.isNullOrBlank()) {
                            apiCallStatus.postValue("SUCCESS")
                            resBkash.postValue(paymentCreateResponse?.resbKash)
                        } else {
                            apiCallStatus.postValue("NO_DATA")
                            dismissListener.postValue(Pair(false, paymentCreateResponse?.message ?: "Payment cancelled, please try again later!"))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                        dismissListener.postValue(Pair(false, "Payment cancelled, please try again later!"))
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                        dismissListener.postValue(Pair(false, "Payment cancelled, please try again later!"))
                    }
                }
            }
        }
    }

    fun executeBkashPayment() {
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.bkashExecutePaymentRepo(bkashPaymentExecuteJson, bkashToken))) {
                    is ApiSuccessResponse -> {
                        val paymentExecuteResponse = apiResponse.body.resdata
                        if (!paymentExecuteResponse?.resExecuteBk.isNullOrBlank()) {
                            apiCallStatus.postValue("SUCCESS")
                            resExecuteBkash.postValue(paymentExecuteResponse?.resExecuteBk!!)
                            dismissListener.postValue(Pair(true, "Payment Successful"))
                        } else {
                            apiCallStatus.postValue("NO_DATA")
                            dismissListener.postValue(Pair(false, "Payment not successful, please contact with support team!"))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                        dismissListener.postValue(Pair(false, "Payment not successful, please contact with support team!"))
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                        dismissListener.postValue(Pair(false, "Payment not successful, please contact with support team!"))
                    }
                }
            }
        }
    }
}