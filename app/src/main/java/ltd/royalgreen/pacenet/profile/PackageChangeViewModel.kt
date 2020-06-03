package ltd.royalgreen.pacenet.profile

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.billing.BillingRepository
import ltd.royalgreen.pacenet.billing.UserBalance
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import javax.inject.Inject

class PackageChangeViewModel @Inject constructor(private val application: Application, private val profileRepository: ProfileRepository, private val billingRepository: BillingRepository, private val preferences: SharedPreferences) : BaseViewModel()  {

    val userBalance: MutableLiveData<UserBalance> by lazy {
        MutableLiveData<UserBalance>()
    }

    val packServiceList = MutableLiveData<MutableList<ChildPackService>>()

    fun getUserBalance() {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(billingRepository.userBalanceRepo())) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body.resdata?.billIspUserBalance
                        response?.let {
                            userBalance.postValue(it)
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

    fun getAllPackService() {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(profileRepository.packageServiceRepo())) {
                    is ApiSuccessResponse -> {
                        apiResponse.body.resdata?.ispservices?.let {
                            if (!it.isBlank()) {
                                val jsonArray = JsonParser.parseString(it).asJsonArray
                                val mutableList: MutableList<ChildPackService> = mutableListOf()
                                if (jsonArray.size() > 0) {
                                    for (jsonObject in jsonArray) {
                                        val packService = Gson().fromJson(jsonObject, PackService::class.java)
                                        if (packService.packServiceId == 1) {
                                            val packList = packService.childPackServices
                                            if (packList != null) {
                                                mutableList.addAll(packList)
                                            }
                                        }
                                    }
                                    packServiceList.postValue(mutableList)
                                }
                            }
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
}
