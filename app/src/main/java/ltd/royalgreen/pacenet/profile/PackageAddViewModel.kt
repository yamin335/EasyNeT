package ltd.royalgreen.pacenet.profile

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.*
import javax.inject.Inject

class PackageAddViewModel @Inject constructor(private val application: Application, private val repository: ProfileRepository) : BaseViewModel() {

    val packageCounter = ObservableInt()

    val serviceCounter = ObservableInt()

    var isUpdatingUserPackage = false

    val userPackageList: MutableLiveData<ArrayList<UserPackage>> by lazy {
        MutableLiveData<ArrayList<UserPackage>>()
    }

    val packageList: MutableLiveData<ArrayList<PackageService>> by lazy {
        MutableLiveData<ArrayList<PackageService>>()
    }

    val serviceList: MutableLiveData<ArrayList<PackageService>> by lazy {
        MutableLiveData<ArrayList<PackageService>>()
    }

    var userPurchasedPackageIds: ArrayList<Int> = ArrayList()

    var userPurchasedPackageIdsMap: HashMap<Int, Int> = HashMap()

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

    fun getPackageService() {
        if (checkNetworkStatus(application)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.packageServiceRepo())) {
                    is ApiSuccessResponse -> {
                        val packageServiceResponse = apiResponse.body
                        if (packageServiceResponse.resdata != null) {
                            val packageServiceJsonArray = JsonParser.parseString(packageServiceResponse.resdata.ispservices).asJsonArray
                            if (packageServiceJsonArray != null) {
                                val packagesList: ArrayList<PackageService> = ArrayList()
                                val servicesList: ArrayList<PackageService> = ArrayList()
                                for (jsonObject in packageServiceJsonArray) {
                                    val packageService = Gson().fromJson(jsonObject, PackageServiceList::class.java)
                                    if (packageService.packServiceTypeId?.toInt() == 1 && packageService.packServiceType.equals("Package", true)) {
                                        val temp = packageService.packServices
                                        temp?.let {
                                            packagesList.addAll(it)
                                        }
                                    } else if (packageService.packServiceTypeId?.toInt() == 2 && packageService.packServiceType.equals("Service", true)) {
                                        val temp = packageService.packServices
                                        temp?.let {
                                            servicesList.addAll(it)
                                        }
                                    }
                                }
                                packageList.postValue(packagesList)
                                serviceList.postValue(servicesList)
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