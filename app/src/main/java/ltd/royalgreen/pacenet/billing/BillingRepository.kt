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
import ltd.royalgreen.pacenet.profile.UserPackServiceResponse
import ltd.royalgreen.pacenet.util.DefaultResponse
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(private val apiService: ApiService, private val preferences: SharedPreferences) {

    suspend fun userBalanceRepo(): Response<UserBalanceResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val jsonObject = JsonObject()
        user?.let {
            jsonObject.addProperty("UserID", user.userID)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.billispuserbalance(param)
        }
    }

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

    suspend fun invoiceRepo(pageNumber: Long, pageSize: Int, searchValue: String, SDate: String, EDate: String): Response<InvoiceResponse> {
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
            apiService.getallispinvbyusrid(param)
        }
    }

    suspend fun invoiceDetailRepo(SDate: String, EDate: String, CDate: String, invId: Int, userPackServiceId: Int): Response<InvoiceDetailResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("SDate", SDate)
            addProperty("EDate", EDate)
            addProperty("CDate", CDate)
            addProperty("invId", invId)
            addProperty("IspUserID", user.userID)
            addProperty("userPackServiceId", userPackServiceId)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getispuserinvocedetail(param)
        }
    }

    suspend fun childInvoiceRepo(SDate: String, EDate: String, CDate: String, invId: Int): Response<ChildInvoiceResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("SDate", SDate)
            addProperty("EDate", EDate)
            addProperty("CDate", CDate)
            addProperty("invId", invId)
            addProperty("IspUserID", user.userID)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getallispchildinvdetailsbyusrid(param)
        }
    }

    suspend fun getUserPackServiceRepo(): Response<UserPackServiceResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("id", user.userID)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getprofileuserbyid(param)
        }
    }

    suspend fun fosterUrlRepo(amount: Double): Response<RechargeResponse> {

        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val jsonObject = JsonObject()
        user?.let {
            jsonObject.addProperty("UserID", user.userID)
            jsonObject.addProperty("rechargeAmount", amount)
            jsonObject.addProperty("IsActive", true)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.isprecharge(param)
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
            apiService.isprechargesave(param)
        }
    }

    suspend fun fosterRechargeSaveRepo(fosterString: String, billPaymentHelper: BillPaymentHelper): Response<DefaultResponse> {

        val fosterJsonObject = JsonParser.parseString(fosterString).asJsonArray[0].asJsonObject
        val fosterModel = Gson().fromJson(fosterJsonObject, FosterModel::class.java)
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val userLoggedData = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        //Current Date
        val todayInMilSec = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val today = df.format(todayInMilSec)
        val jsonObject = JsonObject().apply {
            addProperty("BalanceAmount", billPaymentHelper.balanceAmount)
            addProperty("DeductedAmount", billPaymentHelper.deductedAmount)
            addProperty("ISPUserID", user?.userID)
            addProperty("InvoiceId", billPaymentHelper.invoiceId)
            addProperty("IsActive", true)
            addProperty("Particulars", "")
            addProperty("ProfileId", user?.profileID)
            addProperty("RechargeType", "foster")
            addProperty("TransactionDate", today)
            addProperty("TransactionNo", fosterModel.MerchantTxnNo)
            addProperty("UserName", user?.displayName)
            addProperty("UserPackServiceId", billPaymentHelper.userPackServiceId)
            addProperty("UserTypeId", userLoggedData?.userTypeId)
        }

        val param = JsonArray().apply {
            add(jsonObject)
            add(fosterJsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.newrechargesave(param)
        }
    }

    suspend fun bkashTokenRepo(billPaymentHelper: BillPaymentHelper): Response<BKashTokenResponse> {
        val userLoggedData = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("invId", billPaymentHelper.invoiceId)
            addProperty("id", billPaymentHelper.userPackServiceId)
            addProperty("rechargeAmount", billPaymentHelper.balanceAmount)
            addProperty("loggedUserId", userLoggedData.userID)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.generatebkashtoken(param)
        }
    }

    suspend fun bkashCreatePaymentRepo(paymentRequest: PaymentRequest?, createBkash: CreateBkashModel?, billPaymentHelper: BillPaymentHelper): Response<BKashCreatePaymentResponse> {

        val jsonObject = JsonObject().apply {
            addProperty("authToken", createBkash?.authToken)
            addProperty("rechargeAmount", billPaymentHelper.balanceAmount)
            addProperty("deductedAmount", billPaymentHelper.deductedAmount)
            addProperty("Name", paymentRequest?.intent)
            addProperty("currency", createBkash?.currency)
            addProperty("mrcntNumber", createBkash?.mrcntNumber)
            addProperty("canModify", billPaymentHelper.canModify)
            addProperty("isChildInvoicePayment", billPaymentHelper.isChildInvoice)
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

    suspend fun bkashPaymentSaveRepo(bkashPaymentResponse: String, billPaymentHelper: BillPaymentHelper): Response<DefaultResponse> {
        val bkashJsonObject = JsonParser.parseString(bkashPaymentResponse).asJsonObject
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val userLoggedData = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        //Current Date
        val todayInMilSec = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val today = df.format(todayInMilSec)
        val jsonObject = JsonObject().apply {
            addProperty("BalanceAmount", billPaymentHelper.balanceAmount)
            addProperty("DeductedAmount", billPaymentHelper.deductedAmount)
            addProperty("ISPUserID", user?.userID)
            addProperty("InvoiceId", billPaymentHelper.invoiceId)
            addProperty("IsActive", true)
            addProperty("Particulars", "")
            addProperty("ProfileId", user?.profileID)
            addProperty("RechargeType", "bkash")
            addProperty("TransactionDate", today)
            addProperty("TransactionNo", bkashJsonObject.get("trxID").asString)
            addProperty("UserName", user?.displayName)
            addProperty("UserPackServiceId", billPaymentHelper.userPackServiceId)
            addProperty("UserTypeId", userLoggedData?.userTypeId)
            addProperty("isChildInvoicePayment", billPaymentHelper.isChildInvoice)
        }

        val param = JsonArray().apply {
            add(jsonObject)
            add(bkashJsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.newrechargebkashpayment(param)
        }
    }

    suspend fun newPaymentSaveRepo(billPaymentHelper: BillPaymentHelper): Response<DefaultResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val userLoggedData = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        //Current Date
        val todayInMilSec = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val today = df.format(todayInMilSec)
        val jsonObject = JsonObject().apply {
            addProperty("ispUserID", user?.userID)
            addProperty("userPackServiceId", billPaymentHelper.userPackServiceId)
            addProperty("profileId", user?.profileID)
            addProperty("userTypeId", userLoggedData?.userTypeId)
            addProperty("invoiceId", billPaymentHelper.invoiceId)
            addProperty("username", user?.displayName)
            addProperty("transactionDate", today)
            addProperty("rechargeType", "From Balance")
            addProperty("balanceAmount", billPaymentHelper.balanceAmount)
            addProperty("particulars", "Payment by user existing balance")
            addProperty("isActive", true)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.newpayment(param)
        }
    }
}