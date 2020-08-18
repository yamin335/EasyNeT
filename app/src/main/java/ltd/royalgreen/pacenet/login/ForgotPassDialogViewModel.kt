package ltd.royalgreen.pacenet.login

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.*
import javax.inject.Inject

class ForgotPassDialogViewModel @Inject constructor(app: Application) : BaseViewModel(app) {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferences: SharedPreferences

    val application = app

    val errorToast: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val successToast: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val errorMessage: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val oldPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val newPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val confirmPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun processChangePassword() {
//        if (isNetworkAvailable(application)) {
//            val loggedUser = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
//
//            val jsonObject = JsonObject().apply {
//                addProperty("password", newPassword.value)
//                addProperty("userName", loggedUser.userName)
//                addProperty("oldPassword", oldPassword.value)
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
//                            successToast.postValue(apiResponse.body.resdata.message)
//                        } else {
//                            errorToast.postValue(apiResponse.body.resdata.message)
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
    }
}