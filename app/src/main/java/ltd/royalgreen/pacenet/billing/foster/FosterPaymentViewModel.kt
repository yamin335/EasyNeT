package ltd.royalgreen.pacenet.billing.foster

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.billing.BillingRepository
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import javax.inject.Inject

class FosterPaymentViewModel @Inject constructor(private val application: Application, private val repository: BillingRepository) : BaseViewModel() {

    val rechargeSuccessFailureStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val showMessage: MutableLiveData<Pair<String, String>> by lazy {
        MutableLiveData<Pair<String, String>>()
    }

    fun checkFosterPaymentStatus(fosterPaymentStatusUrl: String) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.fosterStatusRepo(fosterPaymentStatusUrl))) {
                    is ApiSuccessResponse -> {
                        val rechargeStatusFosterResponse = apiResponse.body
                        if (rechargeStatusFosterResponse.resdata.resstate) {
                            //saveNewFosterRecharge(rechargeStatusFosterResponse.resdata.fosterRes)
                        } else {
                            showMessage.postValue(Pair("ERROR", "Payment not successful !"))
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

//    private fun saveNewFosterRecharge(fosterString: String) {
//        if (checkNetworkStatus(application)) {
//            apiCallStatus.postValue("LOADING")
//            viewModelScope.launch {
//                when (val apiResponse = ApiResponse.create(repository.fosterRechargeSaveRepo(fosterString))) {
//                    is ApiSuccessResponse -> {
//                        val rechargeFinalSaveResponse = apiResponse.body
//                        if (rechargeFinalSaveResponse.resdata.resstate == true) {
//                            showMessage.postValue(Pair("SUCCESS", rechargeFinalSaveResponse.resdata.message))
//                            rechargeSuccessFailureStatus.postValue(rechargeFinalSaveResponse.resdata.resstate)
//                        } else {
//                            showMessage.postValue(Pair("ERROR", rechargeFinalSaveResponse.resdata.message))
//                        }
//                        apiCallStatus.postValue("SUCCESS")
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue("EMPTY")
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue("ERROR")
//                    }
//                }
//            }
//        }
//    }
}