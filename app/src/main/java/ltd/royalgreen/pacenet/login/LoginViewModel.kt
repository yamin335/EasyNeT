package ltd.royalgreen.pacenet.login

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.*
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val application: Application, private val repository: LoginRepository) : BaseViewModel() {

    @Inject
    lateinit var preferences: SharedPreferences

    val errorToast: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val successToast: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val userName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val apiResult: MutableLiveData<LoginResponse> by lazy {
        MutableLiveData<LoginResponse>()
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
        if (checkNetworkStatus(application)) {

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue("ERROR")
                errorToast.postValue("Can not connect to server! Please try again later.")
            }

            apiCallStatus.postValue("LOADING")
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.loginRepo(userName.value!!, password.value!!))) {
                    is ApiSuccessResponse -> {
                        apiResult.postValue(apiResponse.body)
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

//    fun getIspUserData() {
//        if (isNetworkAvailable(application)) {
//            val jsonObject = JsonObject().apply {
//                addProperty("userName", )
//                addProperty("userPass", )
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