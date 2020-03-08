package ltd.royalgreen.pacenet.billing

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ltd.royalgreen.pacenet.billing.bkash.CreateBkashModel
import ltd.royalgreen.pacenet.billing.bkash.PaymentRequest
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.login.LoggedUserID
import ltd.royalgreen.pacenet.network.ApiService
import ltd.royalgreen.pacenet.util.DefaultResponse
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(private val apiService: ApiService, private val preferences: SharedPreferences) {
    suspend fun paymentHistoryRepo(pageNumber: Long, pageSize: Int, searchValue: String, SDate: String, EDate: String): Response<PaymentHistory> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("UserID", user.userID)
            addProperty("pageNumber", pageNumber)
            addProperty("pageSize", pageSize)
            addProperty("values", searchValue)
            addProperty("SDate", SDate)
            addProperty("EDate", EDate)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.billhistory(param)
        }
    }

    suspend fun rechargeHistoryRepo(pageNumber: Long, pageSize: Int, searchValue: String, SDate: String, EDate: String): Response<RechargeHistory> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("UserID", user.userID)
            addProperty("pageNumber", pageNumber)
            addProperty("pageSize", pageSize)
            addProperty("values", searchValue)
            addProperty("SDate", SDate)
            addProperty("EDate", EDate)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.rechargehistory(param)
        }
    }

    suspend fun fosterUrlRepo(amount: String, note: String): Response<RechargeResponse> {

        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val jsonObject = JsonObject()
        user?.let {
            jsonObject.addProperty("UserID", user.userID)
            jsonObject.addProperty("rechargeAmount", amount)
            jsonObject.addProperty("Particulars", note)
            jsonObject.addProperty("IsActive", true)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.cloudrecharge(param)
        }
    }

    suspend fun fosterStatusRepo(fosterPaymentStatusUrl: String): Response<RechargeStatusFosterCheckModel> {

        val jsonObject = JsonObject().apply {
            addProperty("statusCheckUrl", fosterPaymentStatusUrl)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.cloudrechargesave(param)
        }
    }

    suspend fun fosterRechargeSaveRepo(fosterString: String): Response<DefaultResponse> {

        val fosterJsonObject = JsonParser.parseString(fosterString).asJsonArray[0].asJsonObject
        val fosterModel = Gson().fromJson(fosterJsonObject, FosterModel::class.java)
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val userLoggedData = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        //Current Date
        val todayInMilSec = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val today = df.format(todayInMilSec)
        val jsonObject = JsonObject().apply {
            addProperty("ISPUserID", user?.userID)
            addProperty("ProfileId", user?.profileID)
            addProperty("UserTypeId", userLoggedData?.userTypeId)
            addProperty("TransactionNo", fosterModel.MerchantTxnNo)
            addProperty("InvoiceId", 0)
            addProperty("UserName", user?.displayName)
            addProperty("TransactionDate", today)
            addProperty("RechargeType", "foster")
            addProperty("BalanceAmount", fosterModel.TxnAmount)
            addProperty("Particulars", "")
            addProperty("IsActive", true)
        }

        val param = JsonArray().apply {
            add(jsonObject)
            add(fosterJsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.newrechargesave(param)
        }
    }

    suspend fun bkashTokenRepo(amount: String): Response<BKashTokenResponse> {

        val jsonObject = JsonObject().apply {
            addProperty("id", 0)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.generatebkashtoken(param)
        }
    }

    suspend fun bkashCreatePaymentRepo(paymentRequest: PaymentRequest?, createBkash: CreateBkashModel?): Response<BKashCreatePaymentResponse> {

        val jsonObject = JsonObject().apply {
            addProperty("authToken", createBkash?.authToken)
            addProperty("rechargeAmount", createBkash?.rechargeAmount)
            addProperty("Name", paymentRequest?.intent)
            addProperty("currency", createBkash?.currency)
            addProperty("mrcntNumber", createBkash?.mrcntNumber)
        }

        val body = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.createbkashpayment(body)
        }
    }

    suspend fun bkashExecutePaymentRepo(bkashPaymentExecuteJson: JsonObject, bkashToken: String?): Response<BKashExecutePaymentResponse> {

        val jsonObject = JsonObject().apply {
            addProperty("authToken", bkashToken)
            addProperty("paymentID", bkashPaymentExecuteJson.get("paymentID").asString)
        }

        val body = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.executebkashpayment(body)
        }
    }

    suspend fun bkashPaymentSaveRepo(bkashPaymentResponse: String): Response<DefaultResponse> {
        val bkashJsonObject = JsonParser.parseString(bkashPaymentResponse).asJsonObject
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val userLoggedData = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        //Current Date
        val todayInMilSec = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val today = df.format(todayInMilSec)
        val jsonObject = JsonObject().apply {
            addProperty("ISPUserID", user?.userID)
            addProperty("ProfileId", user?.profileID)
            addProperty("UserTypeId", userLoggedData?.userTypeId)
            addProperty("TransactionNo", bkashJsonObject.get("trxID").asString)
            addProperty("InvoiceId", 0)
            addProperty("UserName", user?.displayName)
            addProperty("TransactionDate", today)
            addProperty("RechargeType", "bKash")
            addProperty("BalanceAmount", bkashJsonObject.get("amount").asString)
            addProperty("Particulars", "")
            addProperty("IsActive", true)
        }

        val param = JsonArray().apply {
            add(jsonObject)
            add(bkashJsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.newrechargebkashpayment(param)
        }
    }
}