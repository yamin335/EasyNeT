package ltd.royalgreen.pacenet.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.*
import javax.inject.Inject

class ChangePackageViewModel @Inject constructor(private val application: Application, private val repository: ProfileRepository) : BaseViewModel() {

    val userPackageList: MutableLiveData<ArrayList<UserPackage>> by lazy {
        MutableLiveData<ArrayList<UserPackage>>()
    }

    fun getUserPackage() {
        if (checkNetworkStatus(application)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.userPackageRepo())) {
                    is ApiSuccessResponse -> {
                        val packageResponse = apiResponse.body
                        if (packageResponse.resdata != null) {
                            val packageJsonArray = JsonParser.parseString(packageResponse.resdata.listContactForPackService).asJsonArray
                            if (packageJsonArray != null) {
                                val packageList: ArrayList<UserPackage> = ArrayList()
                                for (jsonObject in packageJsonArray) {
                                    val userPackage = Gson().fromJson(jsonObject, UserPackage::class.java)
                                    packageList.add(userPackage)
                                }
                                userPackageList.postValue(packageList)
                            }
                        }
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