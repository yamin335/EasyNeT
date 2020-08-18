package ltd.royalgreen.pacenet.billing.foster

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.billing.BillPaymentHelper
import ltd.royalgreen.pacenet.billing.BillingRepository
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import javax.inject.Inject

class FosterPaymentViewModel @Inject constructor(private val application: Application, private val repository: BillingRepository) : BaseViewModel(application) {

    val showMessage: MutableLiveData<Pair<Boolean, String>> by lazy {
        MutableLiveData<Pair<Boolean, String>>()
    }

    fun checkFosterPaymentStatus(fosterPaymentStatusUrl: String, billPaymentHelper: BillPaymentHelper) {
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.fosterStatusRepo(fosterPaymentStatusUrl))) {
                    is ApiSuccessResponse -> {
                        val rechargeStatusFosterResponse = apiResponse.body.resdata
                        if (rechargeStatusFosterResponse.resstate) {
                            saveNewFosterRecharge(rechargeStatusFosterResponse.fosterRes, billPaymentHelper)
                        } else {
                            showMessage.postValue(Pair(false, "Payment not successful, please try again later!"))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        showMessage.postValue(Pair(false, "Payment not successful, please try again later!"))
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        showMessage.postValue(Pair(false, "Payment not successful, please try again later!"))
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

    private fun saveNewFosterRecharge(fosterString: String, billPaymentHelper: BillPaymentHelper) {
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.fosterRechargeSaveRepo(fosterString, billPaymentHelper))) {
                    is ApiSuccessResponse -> {
                        val rechargeFinalSaveResponse = apiResponse.body.resdata
                        if (rechargeFinalSaveResponse?.resstate == true) {
                            showMessage.postValue(Pair(true, rechargeFinalSaveResponse.message ?: "Successful"))
                        } else {
                            showMessage.postValue(Pair(false, rechargeFinalSaveResponse?.message ?: "Not Successful!"))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        showMessage.postValue(Pair(false, "Payment not successful, please contact with support team!"))
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        showMessage.postValue(Pair(false, "Payment not successful, please contact with support team!"))
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }
}