package ltd.royalgreen.pacenet.billing.bkash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.billing.BillPaymentHelper
import ltd.royalgreen.pacenet.billing.BillingRepository
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

class BKashPaymentViewModel @Inject constructor(private val application: Application, private val repository: BillingRepository) : BaseViewModel() {

    val resBkash: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var bkashPaymentExecuteJson = JsonObject()

    var bkashToken: String? = ""

    var dismissListener = MutableLiveData<Pair<Boolean, String>>()

    fun createBkashCheckout(paymentRequest: PaymentRequest?, createBkash: CreateBkashModel?) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.bkashCreatePaymentRepo(paymentRequest, createBkash))) {
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

    fun executeBkashPayment(billPaymentHelper: BillPaymentHelper) {
        if (checkNetworkStatus(application)) {
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
                            saveBkashNewRecharge(paymentExecuteResponse?.resExecuteBk!!, billPaymentHelper)
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

    fun saveBkashNewRecharge(bkashPaymentResponse: String, billPaymentHelper: BillPaymentHelper) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.bkashPaymentSaveRepo(bkashPaymentResponse, billPaymentHelper))) {
                    is ApiSuccessResponse -> {
                        val rechargeFinalSaveResponse = apiResponse.body.resdata
                        if (rechargeFinalSaveResponse.resstate == true) {
                            dismissListener.postValue(Pair(true, rechargeFinalSaveResponse.message))
                        } else {
                            dismissListener.postValue(Pair(false, rechargeFinalSaveResponse.message))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        dismissListener.postValue(Pair(false, "Payment not successful, please contact with support team!"))
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        dismissListener.postValue(Pair(false, "Payment not successful, please contact with support team!"))
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }
}