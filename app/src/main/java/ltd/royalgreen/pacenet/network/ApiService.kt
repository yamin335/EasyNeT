package ltd.royalgreen.pacenet.network

import com.google.gson.JsonArray
import ltd.royalgreen.pacenet.UserDataResponse
import ltd.royalgreen.pacenet.billing.*
import ltd.royalgreen.pacenet.dashboard.DashSessionResponse
import ltd.royalgreen.pacenet.dashboard.DashboardChart
import ltd.royalgreen.pacenet.login.LoginResponse
import ltd.royalgreen.pacenet.profile.PackServiceResponse
import ltd.royalgreen.pacenet.profile.UserPackServiceResponse
import ltd.royalgreen.pacenet.support.SupportTicketResponse
import ltd.royalgreen.pacenet.support.TicketCategoryResponse
import ltd.royalgreen.pacenet.support.TicketCommentResponse
import ltd.royalgreen.pacenet.util.DefaultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface ApiService {
    // API FOR LOGIN
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/loginportalusers")
    suspend fun loginportalusers(@Body jsonArray: JsonArray): Response<LoginResponse>

    // API FOR CHANGE PASSWORD
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/changepassword")
    suspend fun changepassword(@Body jsonArray: JsonArray): Response<DefaultResponse>

    //API FOR USER BALANCE
    @GET("/api/ispportal/getuserisp")
    suspend fun getuserisp(@Query("param") param: String): Response<UserDataResponse>

    //API FOR USER PAYMENT HISTORY
    @GET("/api/ispportal/billhistory")
    suspend fun billhistory(@Query("param") param: String): Response<PaymentHistory>

    //API FOR USER RECHARGE HISTORY
    @GET("/api/ispportal/getallispinvbyusrid")
    suspend fun getallispinvbyusrid(@Query("param") param: String): Response<InvoiceResponse>


    //API FOR  SUPPORT TICKET CATEGORY
    @GET("/api/dropdown/getispticketcategory")
    suspend fun getispticketcategory(@Query("param") param: String): Response<TicketCategoryResponse>

    //API FOR  DASHBOARD CHART
    @GET("/api/ispportal/getdashboardchartportal")
    suspend fun getdashboardchartportal(@Query("param") param: String): Response<DashboardChart>

    //API FOR  DASHBOARD SESSION CHART
    @GET("/api/ispportal/getbizispsessionchart")
    suspend fun getbizispsessionchart(@Query("param") param: String): Response<DashSessionResponse>

    //API FOR TICKET HISTORY
    @GET("/api/ispportal/getbypageispticket")
    suspend fun getbypageispticket(@Query("param") param: String): Response<SupportTicketResponse>

    @Multipart
    @POST("/api/ispportal/saveupdateispticket")
    suspend fun saveupdateispticket(@Part("ticketSummary") ticketSummary: RequestBody,
                                    @Part("ticketDescription") ticketDescription: RequestBody,
                                    @Part("ispTicketCategoryId") ispTicketCategoryId: RequestBody,
                                    @Part("ispUserId") ispUserId: RequestBody,
                                    @Part attachedFiles: List<MultipartBody.Part>): Response<DefaultResponse>

    @Multipart
    @POST("/api/ispportal/saveispticketconversation")
    suspend fun saveispticketconversation(@Part("ispTicketId") ispTicketId: RequestBody,
                                          @Part("ispUserId") ispUserId: RequestBody,
                                          @Part("ticketComment") ticketComment: RequestBody,
                                          @Part attachedFiles: MultipartBody.Part?): Response<DefaultResponse>

    //API FOR TICKET CONVERSATION
    @GET("/api/ispportal/getbyidispticket")
    suspend fun getbyidispticket(@Query("param") param: String): Response<TicketCommentResponse>

    //API FOR RECHARGE
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/isprecharge")
    suspend fun isprecharge(@Body jsonArray: JsonArray): Response<RechargeResponse>

    //API FOR RECHARGE STATUS CHECK
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/isprechargesave")
    suspend fun isprechargesave(@Body jsonArray: JsonArray): Response<RechargeStatusFosterCheckModel>

    //API FOR RECHARGE SAVE
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/newrechargesave")
    suspend fun newrechargesave(@Body jsonArray: JsonArray): Response<DefaultResponse>

    //API FOR GENERATE TOKEN FOR BKASH PAYMENT
    @GET("/api/ispportal/generatebkashtoken")
    suspend fun generatebkashtoken(@Query("param") param: String): Response<BKashTokenResponse>

    //API FOR RECHARGE SAVE
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/createbkashpayment")
    suspend fun createbkashpayment(@Body jsonArray: JsonArray): Response<BKashCreatePaymentResponse>

    //API FOR RECHARGE SAVE
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/executebkashpayment")
    suspend fun executebkashpayment(@Body jsonArray: JsonArray): Response<BKashExecutePaymentResponse>

    //API FOR RECHARGE SAVE
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/newrechargebkashpayment")
    suspend fun newrechargebkashpayment(@Body jsonArray: JsonArray): Response<DefaultResponse>

    // API for save payment from balance
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/newpayment")
    suspend fun newpayment(@Body jsonArray: JsonArray): Response<DefaultResponse>

    //API FOR ISP PACKAGES
    @GET("/api/ispuser/getbizisppackservice")
    suspend fun getbizisppackservice(@Query("param") param: String): Response<PackServiceResponse>

    //API FOR USER PROFILE PACK_SERVICE
    @GET("/api/ispuser/getprofileuserbyid")
    suspend fun getprofileuserbyid(@Query("param") param: String): Response<UserPackServiceResponse>

    //API FOR USER INVOICE DETAILS
    @GET("/api/ispuser/getispuserinvocedetail")
    suspend fun getispuserinvocedetail(@Query("param") param: String): Response<InvoiceDetailResponse>

    //API FOR USER BALANCE
    @GET("/api/ispportal/billispuserbalance")
    suspend fun billispuserbalance(@Query("param") param: String): Response<UserBalanceResponse>

    // API for logout
    @Headers("Content-Type: application/json")
    @POST("/api/ispportal/loggedout")
    suspend fun loggedout(@Body jsonArray: JsonArray): Response<DefaultResponse>


//    @Multipart
//    @Headers("Content-Type: multipart/form-data")
//    @POST("/api/ispportal/saveupdateispticket")
//    suspend fun saveupdateispticket(@Part ispTicketCategoryId: MultipartBody.Part,
//                                    @Part ispUserId: MultipartBody.Part,
//                                    @Part ticketDescription: MultipartBody.Part,
//                                    @Part ticketSummary: MultipartBody.Part): Response<DefaultResponse>

//    @Multipart
//    @Headers("Content-Type: multipart/form-data")
//    @POST("/api/ispportal/saveupdateispticket")
//    suspend fun saveupdateispticket(@PartMap formData: HashMap<String, String>): Response<DefaultResponse>

//    @Headers("Content-Type: multipart/form-data")
//    @POST("/api/ispportal/saveupdateispticket")
//    suspend fun saveupdateispticket(@Body requestBody: MultipartBody): Response<DefaultResponse>

//    @Multipart
//    @Headers("Content-Type: multipart/form-data")
//    @POST("/api/ispportal/saveupdateispticket")
//    suspend fun saveupdateispticket(@Part("ispTicketCategoryId") ispTicketCategoryId: String,
//                                    @Part("ispUserId") ispUserId: String,
//                                    @Part("ticketDescription") ticketDescription: String,
//                                    @Part("ticketSummary") ticketSummary: String): Response<DefaultResponse>
//
//    //API FOR USER ACTIVITY LOG
//    @GET("/api/portal/cloudactivitylog")
//    suspend fun cloudactivitylog(@Query("param") param: String): Response<UserActivityLog>
//
//    //API FOR USER VM LIST
//    @GET("/api/portal/cloudvmbyuserid")
//    suspend fun cloudvmbyuserid(@Query("param") param: String): Response<String>
//
//    //API FOR USER PAYMENT HISTORY
//    @GET("/api/portal/billhistory")
//    suspend fun billhistory(@Query("param") param: String): Response<PaymentHistory>
//
//    //API FOR LAST PAYMENT AMOUNT
//    @GET("/api/portal/lastbillbyuser")
//    suspend fun lastbillbyuser(@Query("param") param: String): Response<LastRechargeBalance>
//
//    //API FOR VM START_STOP
//    @Headers("Content-Type: application/json")
//    @POST("/api/portal/cloudvmstartstop")
//    suspend fun cloudvmstartstop(@Body jsonArray: JsonArray): Response<String>
//
//    //API FOR VM REBOOT
//    @Headers("Content-Type: application/json")
//    @POST("/api/portal/cloudvmreboot")
//    suspend fun cloudvmreboot(@Body jsonArray: JsonArray): Response<String>
//
//    //API FOR RENAME DEPLOYMENT
//    @Headers("Content-Type: application/json")
//        @POST("/api/portal/updatedeploymentname")
//    suspend fun updatedeploymentname(@Body jsonArray: JsonArray): Response<String>
//
//    //API FOR DEPLOYMENT NOTE
//    @Headers("Content-Type: application/json")
//    @POST("/api/portal/updatevmnote")
//    suspend fun updatevmnote(@Body jsonArray: JsonArray): Response<String>
//
//    //API FOR SYNC DATABASE
//    @Headers("Content-Type: application/json")
//    @POST("/api/portal/clouduservmsyncwithlocaldb")
//    suspend fun clouduservmsyncwithlocaldb(@Body jsonArray: JsonArray): Response<String>
//
//    //API FOR SIGN UP
//    @Headers("Content-Type: application/json")
//    @POST("/api/portal/register")
//    suspend fun register(@Body jsonArray: JsonArray): Response<String>
//
//    //API FOR RECHARGE
//    @Headers("Content-Type: application/json")
//    @POST("/api/billclouduserclient/cloudrecharge")
//    suspend fun cloudrecharge(@Body jsonArray: JsonArray): Response<RechargeResponse>
//
//    //API FOR RECHARGE STATUS CHECK
//    @Headers("Content-Type: application/json")
//    @POST("/api/billclouduserclient/cloudrechargesave")
//    suspend fun cloudrechargesave(@Body jsonArray: JsonArray): Response<RechargeStatusFosterCheckModel>

//    //API FOR RECHARGE SAVE
//    @Headers("Content-Type: application/json")
//    @POST("/api/portal/newrechargesave")
//    suspend fun newrechargesave(@Body jsonArray: JsonArray): Response<DefaultResponse>



}
