package ltd.royalgreen.pacenet.pgw

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

class FosterPGWViewModel @Inject constructor(val application: Application, private val repository: BillingRepository) : BaseViewModel(application) {

    val showMessage: MutableLiveData<Pair<Boolean, String>> by lazy {
        MutableLiveData<Pair<Boolean, String>>()
    }

    val fosterResponse: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun checkFosterPaymentStatus(fosterPaymentStatusUrl: String) {
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
                            fosterResponse.postValue(rechargeStatusFosterResponse.fosterRes)
                            showMessage.postValue(Pair(true, "Payment Successful"))
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
}