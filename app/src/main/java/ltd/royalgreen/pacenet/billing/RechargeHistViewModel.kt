package ltd.royalgreen.pacenet.billing

import android.app.Application
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.paymentmodule.bkash.BkashDataModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.CreateBkashModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.PaymentRequest
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import java.util.*
import javax.inject.Inject

class RechargeHistViewModel @Inject constructor(val application: Application, private val repository: BillingRepository) : BaseViewModel() {

    @Inject
    lateinit var preferences: SharedPreferences

    val toDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val fromDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val searchValue: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    lateinit var rechargeHistoryList: LiveData<PagedList<RechargeTransaction>>

    val balance: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val bKashToken: MutableLiveData<BkashDataModel> by lazy {
        MutableLiveData<BkashDataModel>()
    }

    var hasBkashToken = false

    val fosterUrl: MutableLiveData<Pair<String?, String?>> by lazy {
        MutableLiveData<Pair<String?, String?>>()
    }

    val handler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    init {
        fromDate.value = "dd/mm/yyyy"
        toDate.value = "dd/mm/yyyy"
        searchValue.value = ""
        Log.d("B-VIEWMODEL--> ", "${Random().nextInt(8)+1}")
    }

    fun getFosterPaymentUrl(amount: String, note: String) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")

            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.fosterUrlRepo(amount, note))) {
                    is ApiSuccessResponse -> {
                        val rechargeResponse = apiResponse.body
                        if (rechargeResponse.resdata != null) {
//                            preferences.edit().apply {
//                                putString("paymentStatusUrl", rechargeResponse.resdata.paymentStatusUrl)
//                                apply()
//                            }

                            fosterUrl.postValue(Pair(rechargeResponse.resdata.paymentProcessUrl, rechargeResponse.resdata.paymentStatusUrl))
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

    fun getBkashToken(amount: String) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.bkashTokenRepo(amount))) {
                    is ApiSuccessResponse -> {
                        val bKashTokenResponse = apiResponse.body
                        if (bKashTokenResponse.resdata?.tModel != null) {
                            hasBkashToken = true
                            val paymentRequest = PaymentRequest()
                            paymentRequest.amount = amount

                            val createBkashModel = CreateBkashModel()
                            createBkashModel.authToken = JsonParser.parseString(bKashTokenResponse.resdata.tModel.token).asJsonObject.get("id_token").asString
                            createBkashModel.rechargeAmount = amount
                            createBkashModel.currency = bKashTokenResponse.resdata.tModel.currency
                            createBkashModel.mrcntNumber = bKashTokenResponse.resdata.tModel.marchantInvNo

                            val bkashDataModel = BkashDataModel()
                            bkashDataModel.paymentRequest = paymentRequest
                            bkashDataModel.createBkashModel = createBkashModel
                            bKashToken.postValue(bkashDataModel)
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

    fun prepareBalance() {
        viewModelScope.launch(handler) {
            val loggedUser = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            balance.postValue(loggedUser.balance.toString() + " (BDT)")
        }
    }

    suspend fun getRechargeHistory(pageNumber: Long, pageSize: Int, values: String, SDate: String, EDate: String) = repository.rechargeHistoryRepo(pageNumber, pageSize, values, SDate, EDate)

    fun pickDate(view: View){
        // calender class's instance and get current date , month and year from calender
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR) // current year
        val mMonth = c.get(Calendar.MONTH) // current month
        val mDay = c.get(Calendar.DAY_OF_MONTH) // current day
        // date picker dialog
        val datePickerDialog = DatePickerDialog(view.context,
            { _, year, monthOfYear, dayOfMonth ->
                when(view.id) {
                    R.id.fromDate -> {
                        fromDate.value = year.toString()+"-"+(monthOfYear + 1)+"-"+dayOfMonth
                    }
                    R.id.toDate -> {
                        toDate.value = year.toString()+"-"+(monthOfYear + 1)+"-"+dayOfMonth
                    }
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }
}