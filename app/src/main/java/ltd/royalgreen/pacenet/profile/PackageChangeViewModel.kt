package ltd.royalgreen.pacenet.profile

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.billing.BillPaymentHelper
import ltd.royalgreen.pacenet.billing.BillingRepository
import ltd.royalgreen.pacenet.billing.UserBalance
import ltd.royalgreen.pacenet.billing.bkash.BkashDataModel
import ltd.royalgreen.pacenet.billing.bkash.CreateBkashModel
import ltd.royalgreen.pacenet.billing.bkash.PaymentRequest
import ltd.royalgreen.pacenet.login.LoggedUserID
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import ltd.royalgreen.pacenet.util.returnIfNull
import javax.inject.Inject

class PackageChangeViewModel @Inject constructor(val application: Application, private val profileRepository: ProfileRepository, private val billingRepository: BillingRepository, private val preferences: SharedPreferences) : BaseViewModel(application)  {

    val userBalance: MutableLiveData<UserBalance> by lazy {
        MutableLiveData<UserBalance>()
    }

    val payMethods: MutableLiveData<ArrayList<PayMethod>> by lazy {
        MutableLiveData<ArrayList<PayMethod>>()
    }

    val packServiceList = MutableLiveData<MutableList<ChildPackService>>()

    var userConnectionId: Int? = null
    var changingPackage: UserPackService? = null
    var selectedPackage: ChildPackService? = null
    var consumeData: ConsumeData? = null
    var packageChangeHelper: PackageChangeHelper? = null

    val bKashToken: MutableLiveData<BkashDataModel> by lazy {
        MutableLiveData<BkashDataModel>()
    }

    val fosterUrl: MutableLiveData<Pair<String?, String?>> by lazy {
        MutableLiveData<Pair<String?, String?>>()
    }

    var billPaymentHelper: BillPaymentHelper? = null

    fun getFosterPaymentUrl(amount: Double) {
        if (checkNetworkStatus() && billPaymentHelper != null) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(billingRepository.fosterUrlRepo(amount))) {
                    is ApiSuccessResponse -> {
                        val rechargeResponse = apiResponse.body.resdata
                        if (rechargeResponse != null) {
                            fosterUrl.postValue(Pair(rechargeResponse.paymentProcessUrl, rechargeResponse.paymentStatusUrl))
                        } else {
                            toastError.postValue("Payment cancelled, please try again later!")
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                        toastError.postValue("Payment cancelled, please try again later!")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                        toastError.postValue("Payment cancelled, please try again later!")
                    }
                }
            }
        }
    }

    fun getBkashToken() {
        if (checkNetworkStatus() && billPaymentHelper != null) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(billingRepository.bkashTokenRepo(billPaymentHelper!!))) {
                    is ApiSuccessResponse -> {
                        val tModel = apiResponse.body.resdata?.tModel
                        if (tModel != null) {
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
                            toastError.postValue("Payment cancelled, please try again later!")
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                        toastError.postValue("Payment cancelled, please try again later!")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                        toastError.postValue("Payment cancelled, please try again later!")
                    }
                }
            }
        }
    }

    fun getUserBalance() {
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(billingRepository.userBalanceRepo())) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body.resdata?.billIspUserBalance
                        response?.let {
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

    fun getPayMethods() {
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(profileRepository.payMethodsRepo())) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body.resdata?.listPaymentMethod
                        response?.let {
                            payMethods.postValue(it)
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

    fun getAllPackService() {
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(profileRepository.packageServiceRepo())) {
                    is ApiSuccessResponse -> {
                        apiResponse.body.resdata?.ispservices?.let {
                            if (!it.isBlank()) {
                                val jsonArray = JsonParser.parseString(it).asJsonArray
                                val mutableList: MutableList<ChildPackService> = mutableListOf()
                                if (jsonArray.size() > 0) {
                                    forLoop@ for (jsonObject in jsonArray) {
                                        val packService = Gson().fromJson(jsonObject, PackService::class.java)
                                        if (packService.packServiceId == changingPackage?.parentPackServiceId) {
                                            val packList = packService.childPackServices
                                            if (packList != null) {
                                                val filteredList = ArrayList<ChildPackService>()
                                                packList.forEach { pack ->
                                                    if (pack.packServiceId != changingPackage?.packServiceId) {
                                                        filteredList.add(pack)
                                                    }
                                                }
                                                mutableList.addAll(filteredList)
                                            }
                                            break@forLoop
                                        }
                                    }
                                    packServiceList.postValue(mutableList)
                                }
                            }
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

    fun saveChangedPackage() {
        val body = prepareBodyForSaveChangedPackage()
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(profileRepository.saveChangedPackageRepo(body))) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body.resdata
                        if (response != null) {
                            toastSuccess.postValue(response.message)
                            popBackStack.postValue(true)
                        } else {
                            toastError.postValue(response?.message ?: "Not Successful! Contact with support team!")
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

    fun saveChangedPackageByBkash(bkashResponse: String) {
        val body = prepareBodyForSaveChangedPackageByBkash(bkashResponse)
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(profileRepository.saveChangedPackageByBkashRepo(body))) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body.resdata
                        if (response != null) {
                            toastSuccess.postValue(response.message)
                            popBackStack.postValue(true)
                        } else {
                            toastError.postValue(response?.message ?: "Not Successful! Contact with support team!")
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

    fun saveChangedPackageByFoster(fosterResponse: String) {
        val body = prepareBodyForSaveChangedPackageByFoster(fosterResponse)
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(profileRepository.saveChangedPackageByFosterRepo(body))) {
                    is ApiSuccessResponse -> {
                        val response = apiResponse.body.resdata
                        if (response != null) {
                            toastSuccess.postValue(response.message)
                            popBackStack.postValue(true)
                        } else {
                            toastError.postValue(response?.message ?: "Not Successful! Contact with support team!")
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

    fun prepareBodyForSaveChangedPackage(): JsonArray {
        val jsonObject1 = JsonObject().apply {
            addProperty("userPackServiceId", changingPackage?.userPackServiceId)
            addProperty("connectionNo", changingPackage?.connectionNo)
            addProperty("userId", changingPackage?.userId)
            addProperty("connectionTypeId", changingPackage?.connectionTypeId)
            addProperty("zoneId", changingPackage?.zoneId)
            addProperty("accountId", changingPackage?.accountId)
            addProperty("packServiceId", selectedPackage?.packServiceId)
            addProperty("packServiceName", selectedPackage?.packServiceName)
            addProperty("parentPackServiceId", selectedPackage?.parentPackServiceId)
            addProperty("parentPackServiceName", selectedPackage?.parentPackServiceName)
            addProperty("packServiceTypeId", selectedPackage?.packServiceTypeId)
            addProperty("packServiceType", selectedPackage?.packServiceType)
            addProperty("packServicePrice", selectedPackage?.packServicePrice)
            addProperty("packServiceInstallCharge", changingPackage?.packServiceInstallCharge)
            addProperty("packServiceOthersCharge", changingPackage?.packServiceOthersCharge)
            addProperty("actualPayAmount", packageChangeHelper?.actualPayAmount)
            addProperty("payAmount", packageChangeHelper?.payAmount)
            addProperty("saveAmount", packageChangeHelper?.savedAmount)
            addProperty("methodId", changingPackage?.methodId)
            addProperty("isUpGrade", packageChangeHelper?.isUpgrade)
            addProperty("isDownGrade", !packageChangeHelper?.isUpgrade!!)
            addProperty("expireDate", changingPackage?.expireDate)
            addProperty("activeDate", changingPackage?.activeDate)
            addProperty("isNew", false)
            addProperty("isUpdate", true)
            addProperty("isDelete", false)
            addProperty("enabled", changingPackage?.enabled)
            addProperty("deductBalance", packageChangeHelper?.deductedAmount)
            addProperty("isBalanceDeduct", packageChangeHelper?.deductedAmount!! > 0.0)
            addProperty("isActive", changingPackage?.isActive)
            addProperty("isNoneStop", changingPackage?.isNoneStop)
            addProperty("isDefault", changingPackage?.isDefault)
        }

        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val jsonObject2 = JsonObject().apply {
            addProperty("id", userConnectionId)
            addProperty("userId", changingPackage?.ispUserId)
            addProperty("values", changingPackage?.packServiceName)
            addProperty("loggeduserId", user.userID)
            addProperty("CDate", consumeData?.todays)
        }

        return JsonArray().apply {
            add(jsonObject1)
            add(jsonObject2)
        }
    }

    fun prepareBodyForSaveChangedPackageByBkash(bkashResponse: String): JsonArray {
        val bkashJson = JsonParser.parseString(bkashResponse).asJsonObject
        bkashJson.addProperty("isSuccess", true)

        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val packUserInfo = JsonObject().apply {
            addProperty("id", userConnectionId)
            addProperty("userId", changingPackage?.ispUserId)
            addProperty("values", changingPackage?.packServiceName)
            addProperty("loggeduserId", user.userID)
            addProperty("CDate", consumeData?.todays)
        }

        val packList = JsonObject().apply {
            addProperty("userPackServiceId", changingPackage?.userPackServiceId)
            addProperty("connectionNo", changingPackage?.connectionNo)
            addProperty("userId", changingPackage?.userId)
            addProperty("connectionTypeId", changingPackage?.connectionTypeId)
            addProperty("zoneId", changingPackage?.zoneId)
            addProperty("accountId", changingPackage?.accountId)
            addProperty("packServiceId", selectedPackage?.packServiceId)
            addProperty("packServiceName", selectedPackage?.packServiceName)
            addProperty("parentPackServiceId", selectedPackage?.parentPackServiceId)
            addProperty("parentPackServiceName", selectedPackage?.parentPackServiceName)
            addProperty("packServiceTypeId", selectedPackage?.packServiceTypeId)
            addProperty("packServiceType", selectedPackage?.packServiceType)
            addProperty("packServicePrice", selectedPackage?.packServicePrice)
            addProperty("packServiceInstallCharge", changingPackage?.packServiceInstallCharge)
            addProperty("packServiceOthersCharge", changingPackage?.packServiceOthersCharge)
            addProperty("actualPayAmount", packageChangeHelper?.actualPayAmount)
            addProperty("payAmount", packageChangeHelper?.payAmount)
            addProperty("saveAmount", packageChangeHelper?.savedAmount)
            addProperty("methodId", changingPackage?.methodId)
            addProperty("isUpGrade", packageChangeHelper?.isUpgrade)
            addProperty("isDownGrade", !packageChangeHelper?.isUpgrade!!)
            addProperty("expireDate", changingPackage?.expireDate)
            addProperty("activeDate", changingPackage?.activeDate)
            addProperty("isNew", false)
            addProperty("isUpdate", true)
            addProperty("isDelete", false)
            addProperty("enabled", changingPackage?.enabled)
            addProperty("deductBalance", packageChangeHelper?.deductedAmount)
            addProperty("isBalanceDeduct", packageChangeHelper?.deductedAmount!! > 0.0)
            addProperty("isActive", changingPackage?.isActive)
            addProperty("isNoneStop", changingPackage?.isNoneStop)
            addProperty("isDefault", changingPackage?.isDefault)
        }

        return JsonArray().apply {
            add(packList)
            add(bkashJson)
            add(packUserInfo)
        }
    }

    fun prepareBodyForSaveChangedPackageByFoster(fosterResponse: String): JsonArray {
        val temp = JsonParser.parseString(fosterResponse).asJsonArray
        val fosterJson = temp[0]

        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val packUserInfo = JsonObject().apply {
            addProperty("id", userConnectionId)
            addProperty("userId", changingPackage?.ispUserId)
            addProperty("values", changingPackage?.packServiceName)
            addProperty("loggeduserId", user.userID)
            addProperty("CDate", consumeData?.todays)
        }

        val packList = JsonObject().apply {
            addProperty("userPackServiceId", changingPackage?.userPackServiceId)
            addProperty("connectionNo", changingPackage?.connectionNo)
            addProperty("userId", changingPackage?.userId)
            addProperty("connectionTypeId", changingPackage?.connectionTypeId)
            addProperty("zoneId", changingPackage?.zoneId)
            addProperty("accountId", changingPackage?.accountId)
            addProperty("packServiceId", selectedPackage?.packServiceId)
            addProperty("packServiceName", selectedPackage?.packServiceName)
            addProperty("parentPackServiceId", selectedPackage?.parentPackServiceId)
            addProperty("parentPackServiceName", selectedPackage?.parentPackServiceName)
            addProperty("packServiceTypeId", selectedPackage?.packServiceTypeId)
            addProperty("packServiceType", selectedPackage?.packServiceType)
            addProperty("packServicePrice", selectedPackage?.packServicePrice)
            addProperty("packServiceInstallCharge", changingPackage?.packServiceInstallCharge)
            addProperty("packServiceOthersCharge", changingPackage?.packServiceOthersCharge)
            addProperty("actualPayAmount", packageChangeHelper?.actualPayAmount)
            addProperty("payAmount", packageChangeHelper?.payAmount)
            addProperty("saveAmount", packageChangeHelper?.savedAmount)
            addProperty("methodId", changingPackage?.methodId)
            addProperty("isUpGrade", packageChangeHelper?.isUpgrade)
            addProperty("isDownGrade", !packageChangeHelper?.isUpgrade!!)
            addProperty("expireDate", changingPackage?.expireDate)
            addProperty("activeDate", changingPackage?.activeDate)
            addProperty("isNew", false)
            addProperty("isUpdate", true)
            addProperty("isDelete", false)
            addProperty("enabled", changingPackage?.enabled)
            addProperty("deductBalance", packageChangeHelper?.deductedAmount)
            addProperty("isBalanceDeduct", packageChangeHelper?.deductedAmount!! > 0.0)
            addProperty("isActive", changingPackage?.isActive)
            addProperty("isNoneStop", changingPackage?.isNoneStop)
            addProperty("isDefault", changingPackage?.isDefault)
        }

        return JsonArray().apply {
            add(packList)
            add(fosterJson)
            add(packUserInfo)
        }
    }
}
