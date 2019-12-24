package ltd.royalgreen.pacenet.dashboard

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ltd.royalgreen.pacenet.network.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(app: Application) : ViewModel() {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferences: SharedPreferences

    val application = app

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

//    fun processSignIn() {
//        if (isNetworkAvailable(application)) {
//            val jsonObject = JsonObject().apply {
//                addProperty("userName", userName.value)
//                addProperty("userPass", password.value)
//            }
//
//            val param = JsonArray().apply {
//                add(jsonObject)
//            }
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                apiCallStatus.postValue(ApiCallStatus.LOADING)
//                val response = apiService.loginportalusers(param)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        apiResult.postValue(apiResponse.body)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        } else {
//            showErrorToast(application, application.getString(R.string.net_error_msg))
//        }
//    }
//
//    fun processChangePassword() {
//        if (isNetworkAvailable(application)) {
//            val loggedUser = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
//
//            val jsonObject = JsonObject().apply {
//                addProperty("passowrd", newPassword.value)
//                addProperty("userName", loggedUser.userName)
//            }
//
//            val param = JsonArray().apply {
//                add(jsonObject)
//            }
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                apiCallStatus.postValue(ApiCallStatus.LOADING)
//                val response = apiService.changepassword(param)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        if (apiResponse.body.resdata.resstate == true) {
//                            showSuccessToast(application, apiResponse.body.resdata.message)
//                        } else {
//                            showErrorToast(application, apiResponse.body.resdata.message)
//                        }
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        } else {
//            showErrorToast(application, application.getString(R.string.net_error_msg))
//        }
//    }
//
//    fun getIspUserData() {
//        if (isNetworkAvailable(application)) {
//            val jsonObject = JsonObject().apply {
//                addProperty("userName", userName.value)
//                addProperty("userName", password.value)
//            }
//
//            val param = JsonArray().apply {
//                add(jsonObject)
//            }
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                apiCallStatus.postValue(ApiCallStatus.LOADING)
//                val response = apiService.loginportalusers(param)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        apiResult.postValue(apiResponse.body)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        } else {
//            showErrorToast(application, application.getString(R.string.net_error_msg))
//        }
//    }
}