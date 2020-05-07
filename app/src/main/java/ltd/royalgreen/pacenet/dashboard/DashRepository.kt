package ltd.royalgreen.pacenet.dashboard

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.network.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashRepository @Inject constructor(private val apiService: ApiService, private val preferences: SharedPreferences) {
    suspend fun dashChartDataRepo(): Response<DashboardChart> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("CompanyID", 1)
            addProperty("values", "rechargepayment")
            addProperty("UserID", user.userID)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getdashboardchartportal(param)
        }
    }

    suspend fun dashSessionChartRepo(month: Int, type: String): Response<DashSessionResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("CompanyId", 1)
            addProperty("userName", user.userName)
            addProperty("values", type)
            addProperty("month", month)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getbizispsessionchart(param)
        }
    }
}