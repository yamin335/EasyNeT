package ltd.royalgreen.pacenet

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.network.*
import ltd.royalgreen.pacenet.util.ConnectivityLiveData
import javax.inject.Inject

class MainViewModel @Inject constructor(private val application: Application, private val repository: MainRepository) : BaseViewModel() {

    @Inject
    lateinit var preferences: SharedPreferences

//    val userBalance: MutableLiveData<BalanceModel> by lazy {
//        MutableLiveData<BalanceModel>()
//    }

    val internetStatus: ConnectivityLiveData by lazy {
        ConnectivityLiveData(application)
    }

    fun getLoggedUserData() {
        if (checkNetworkStatus(application)) {
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.loggedUserRepo())) {
                    is ApiSuccessResponse -> {
                        val user = Gson().fromJson(JsonParser.parseString(apiResponse.body.resdata.userIsp).asJsonArray[0], LoggedUser::class.java)
                        val loggedUserSerialized = Gson().toJson(user)
                        preferences.edit().apply {
                            putString("LoggedUser", loggedUserSerialized)
                            apply()
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

//    fun getUserBalance(user: LoggedUser?) {
//        if (isNetworkAvailable(application)) {
//            val jsonObject = JsonObject().apply {
//                addProperty("UserID", user?.resdata?.loggeduser?.userID)
//            }
//            val param = JsonArray().apply {
//                add(jsonObject)
//            }.toString()
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                exception.printStackTrace()
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                apiCallStatus.postValue(ApiCallStatus.LOADING)
//                val response = apiService.billclouduserbalance(param)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        userBalance.postValue(apiResponse.body)
//                        val userBalanceSerialized = Gson().toJson(apiResponse.body)
//                        preferences.edit().apply {
//                            putString("UserBalance", userBalanceSerialized)
//                            apply()
//                        }
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
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