package ltd.royalgreen.pacenet.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.network.*
import ltd.royalgreen.pacenet.util.isNetworkAvailable
import ltd.royalgreen.pacenet.util.showErrorToast
import javax.inject.Inject

class LoginViewModel @Inject constructor(app: Application) : ViewModel() {

    @Inject
    lateinit var apiService: ApiService

    val application = app

    val userName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val apiResult: MutableLiveData<LoginResponse> by lazy {
        MutableLiveData<LoginResponse>()
    }

    val errorMessage: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val firstName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val lastName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isValidEmail: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val mobile: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isValidPhone: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val signUpPass: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val signUpConfPass: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val company: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val signUpMsg: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun processSignIn() {
        if (isNetworkAvailable(application)) {
            val jsonObject = JsonObject().apply {
                addProperty("userName", userName.value)
                addProperty("userPass", password.value)
            }

            val param = JsonArray().apply {
                add(jsonObject)
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.loginportalusers(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        apiResult.postValue(apiResponse.body)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
    }

//    fun processSignUp(jsonObject: JsonObject) {
//        if (isNetworkAvailable(application)) {
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
//                val response = apiService.register(param)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        signUpMsg.postValue(JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("message").asString)
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
//            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
//            val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
//            toastView.message.text = application.getString(R.string.net_error_msg)
//            toast.view = toastView
//            toast.show()
//        }
//    }
}