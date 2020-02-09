package ltd.royalgreen.pacenet.support

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.login.LoggedUserID
import ltd.royalgreen.pacenet.network.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportRepository @Inject constructor(private val apiService: ApiService, private val preferences: SharedPreferences) {
    suspend fun ticketCategoryRepo(): Response<TicketCategoryResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("id", 0)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getispticketcategory(param)
        }
    }

//    suspend fun rechargeHistoryRepo(pageNumber: Long, pageSize: Int, searchValue: String, SDate: String, EDate: String): Response<RechargeHistory> {
//        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUser::class.java)
//        val jsonObject = JsonObject().apply {
//            addProperty("UserID", user.userID)
//            addProperty("pageNumber", pageNumber)
//            addProperty("pageSize", pageSize)
//            addProperty("values", searchValue)
//            addProperty("SDate", SDate)
//            addProperty("EDate", EDate)
//        }
//
//        val param = JsonArray().apply {
//            add(jsonObject)
//        }.toString()
//
//        return withContext(Dispatchers.IO) {
//            apiService.rechargehistory(param)
//        }
//    }
}