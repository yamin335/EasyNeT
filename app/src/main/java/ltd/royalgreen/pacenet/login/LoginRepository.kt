package ltd.royalgreen.pacenet.login

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ltd.royalgreen.pacenet.network.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun loginRepo(userName: String, userPass: String): Response<LoginResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("userName", userName)
            addProperty("userPass", userPass)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.loginportalusers(param)
        }
    }
}