package ltd.royalgreen.pacenet.profile

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.dashboard.DashSessionResponse
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import ltd.royalgreen.pacenet.util.formatDateTime
import java.util.*
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val application: Application, private val repository: ProfileRepository, private val preferences: SharedPreferences) : BaseViewModel() {
    val name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val balance: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val createDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val phone: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val userPackServiceList = MutableLiveData<MutableList<UserPackService>>()

    init {
        Log.d("P-VIEWMODEL--> ", "${Random().nextInt(8)+1}")
    }

    fun prepareProfile() {
        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }
        viewModelScope.launch(handler) {
            val loggedUser = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            loggedUser?.let {
                name.postValue(if (it.displayName.isNullOrBlank()) "Unknown" else it.displayName)
                balance.postValue(if (it.balance.toString().isBlank()) "N/A" else it.balance.toString() + " BDT")
                createDate.postValue(if (it.created.isNullOrBlank()) "N/A" else it.created?.formatDateTime().first)
                email.postValue(if (it.email.isNullOrBlank()) "N/A" else it.email)
                phone.postValue(if (it.phone.isNullOrBlank()) "N/A" else it.phone)
            }
        }
    }

    fun getUserPackServiceList() {
        if (checkNetworkStatus(application)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.getUserPackServiceRepo())) {
                    is ApiSuccessResponse -> {
                        apiResponse.body.resdata?.listProfileUser?.let {
                            if (!it.isBlank()) {
                                val jsonArray = JsonParser.parseString(it).asJsonArray
                                val mutableDeploymentList: MutableList<UserPackService> = mutableListOf()
                                if (jsonArray.size() > 0) {
                                    val packService = Gson().fromJson(jsonArray[0], UserPackServiceList::class.java)
                                    packService.packList?.let { list ->
                                        mutableDeploymentList.addAll(list)
                                        userPackServiceList.postValue(mutableDeploymentList)
                                    }
                                }
                            }
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
}