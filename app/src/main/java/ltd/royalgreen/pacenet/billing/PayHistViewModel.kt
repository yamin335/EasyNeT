package ltd.royalgreen.pacenet.billing

import android.app.Application
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.billing.bkash.BkashDataModel
import ltd.royalgreen.pacenet.billing.bkash.CreateBkashModel
import ltd.royalgreen.pacenet.billing.bkash.PaymentRequest
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import ltd.royalgreen.pacenet.profile.UserPackService
import ltd.royalgreen.pacenet.profile.UserPackServiceList
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import java.util.*
import javax.inject.Inject

class PayHistViewModel @Inject constructor(val application: Application, private val repository: BillingRepository) : BaseViewModel() {

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

    val userBalance: MutableLiveData<UserBalance> by lazy {
        MutableLiveData<UserBalance>()
    }

    val bKashToken: MutableLiveData<BkashDataModel> by lazy {
        MutableLiveData<BkashDataModel>()
    }

    var hasBkashToken = false

    val fosterUrl: MutableLiveData<Pair<String?, String?>> by lazy {
        MutableLiveData<Pair<String?, String?>>()
    }

    var billPaymentHelper: BillPaymentHelper? = null

    val toastPublisher: MutableLiveData<Pair<Boolean, String>> by lazy {
        MutableLiveData<Pair<Boolean, String>>()
    }

    lateinit var paymentHistoryList: LiveData<PagedList<PaymentTransaction>>
    val userPackServiceList = MutableLiveData<ArrayList<UserPackService>>()

    val handler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    init {
        fromDate.value = "dd/mm/yyyy"
        toDate.value = "dd/mm/yyyy"
        searchValue.value = ""
    }

    fun getUserBalance() {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.userBalanceRepo())) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body
                        response.resdata?.billIspUserBalance?.let {
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

    suspend fun getPaymentHistory(pageNumber: Long, pageSize: Int, values: String, SDate: String, EDate: String) = repository.paymentHistoryRepo(pageNumber, pageSize, values, SDate, EDate)

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
                                val mutableDeploymentList = ArrayList<UserPackService>()
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

    fun getFosterPaymentUrl(amount: Double) {
        if (checkNetworkStatus(application) && billPaymentHelper != null) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.fosterUrlRepo(amount))) {
                    is ApiSuccessResponse -> {
                        val rechargeResponse = apiResponse.body.resdata
                        if (rechargeResponse != null) {
                            fosterUrl.postValue(Pair(rechargeResponse.paymentProcessUrl, rechargeResponse.paymentStatusUrl))
                        } else {
                            toastPublisher.postValue(Pair(false, "Payment cancelled, please try again later!"))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                        toastPublisher.postValue(Pair(false, "Payment cancelled, please try again later!"))
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                        toastPublisher.postValue(Pair(false, "Payment cancelled, please try again later!"))
                    }
                }
            }
        }
    }

    fun getBkashToken() {
        if (checkNetworkStatus(application) && billPaymentHelper != null) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.bkashTokenRepo(billPaymentHelper!!))) {
                    is ApiSuccessResponse -> {
                        val tModel = apiResponse.body.resdata?.tModel
                        if (tModel != null) {
                            hasBkashToken = true
                            val paymentRequest = PaymentRequest()
                            paymentRequest.amount = billPaymentHelper?.balanceAmount

                            val createBkashModel = CreateBkashModel()
                            createBkashModel.authToken = JsonParser.parseString(tModel.token).asJsonObject.get("id_token").asString
                            createBkashModel.rechargeAmount = billPaymentHelper?.balanceAmount
                            createBkashModel.currency = tModel.currency
                            createBkashModel.mrcntNumber = tModel.marchantInvNo

                            val bkashDataModel = BkashDataModel()
                            bkashDataModel.paymentRequest = paymentRequest
                            bkashDataModel.createBkashModel = createBkashModel
                            bKashToken.postValue(bkashDataModel)
                        } else {
                            toastPublisher.postValue(Pair(false, "Payment cancelled, please try again later!"))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                        toastPublisher.postValue(Pair(false, "Payment cancelled, please try again later!"))
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                        toastPublisher.postValue(Pair(false, "Payment cancelled, please try again later!"))
                    }
                }
            }
        }
    }

    fun saveNewPaymentFromBalance(billPaymentHelper: BillPaymentHelper) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.newPaymentSaveRepo(billPaymentHelper))) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body.resdata
                        if (response.resstate == true) {
                            toastPublisher.postValue(Pair(true, response.message))
                        } else {
                            toastPublisher.postValue(Pair(false, response.message))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        toastPublisher.postValue(Pair(false, "Payment cancelled, please try again later!"))
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        toastPublisher.postValue(Pair(false, "Payment cancelled, please try again later!"))
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

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