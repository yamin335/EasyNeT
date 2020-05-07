package ltd.royalgreen.pacenet.profile

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.billing.RechargeHistory
import ltd.royalgreen.pacenet.login.LoggedUserID
import ltd.royalgreen.pacenet.network.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(private val apiService: ApiService, private val preferences: SharedPreferences) {
    suspend fun userPackageRepo(): Response<UserPackageResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("id", user.userID)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getispuserpackservices(param)
        }
    }

    suspend fun packageServiceRepo(): Response<PackageServiceResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("id", user.userID)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getbizisppackservice(param)
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
}