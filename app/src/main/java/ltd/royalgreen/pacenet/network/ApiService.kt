package ltd.royalgreen.pacenet.network

import com.google.gson.JsonArray
import ltd.royalgreen.pacenet.UserDataResponse
import ltd.royalgreen.pacenet.billing.PaymentHistory
import ltd.royalgreen.pacenet.login.LoginResponse
import ltd.royalgreen.pacenet.util.DefaultResponse
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

//
//    //API FOR USER BALANCE
//    @GET("/api/portal/billclouduserbalance")
//    suspend fun billclouduserbalance(@Query("param") param: String): Response<BalanceModel>
//
//    //API FOR USER VM STATUS
//    @GET("/api/portal/GetDashboardChartPortal")
//    suspend fun getDashboardChartPortal(@Query("param") param: String): Response<DashOsStatus>
//
//    //API FOR USER VM SUMMERY
//    @GET("/api/portal/GetDashboardChartPortal")
//    suspend fun getDashboardChartPortalSummery(@Query("param") param: String): Response<DashOsSummary>
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
