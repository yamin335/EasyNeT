package ltd.royalgreen.pacenet

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ltd.royalgreen.pacenet.login.LoggedUserID
import ltd.royalgreen.pacenet.login.LoginResponse
import ltd.royalgreen.pacenet.network.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(private val apiService: ApiService, private val preferences: SharedPreferences) {
    suspend fun loggedUserRepo(): Response<UserDataResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("userName", user.userName)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getuserisp(param)
        }
    }
}