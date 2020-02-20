package ltd.royalgreen.pacenet.support

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ltd.royalgreen.pacenet.login.LoggedUserID
import ltd.royalgreen.pacenet.network.ApiService
import ltd.royalgreen.pacenet.util.DefaultResponse
import okhttp3.MediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import java.io.File
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

    suspend fun ticketConversationRepo(id: Long): Response<TicketCommentResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("id", id)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getbyidispticket(param)
        }
    }

    suspend fun supportTicketHistRepo(pageNumber: Long, pageSize: Int, searchValue: String?, SDate: String?, EDate: String?): Response<SupportTicketResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("ispUserId", user.userID)
            addProperty("pageNumber", pageNumber)
            addProperty("pageSize", pageSize)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getbypageispticket(param)
        }
    }

    suspend fun ticketEntryRepo(ticketSummary: String, ticketDescription: String, ispTicketCategoryId: String): Response<DefaultResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val ispUserId = user.userID.toString()
//        val attachedFile = File("").asRequestBody("image/jpeg".toMediaTypeOrNull())
//        val filePart = MultipartBody.Part.createFormData("attachedFile", "demo", attachedFile)

//        val formData = HashMap<String, RequestBody>()
//        formData["ticketSummary"] = ticketSummary.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
//        formData["ticketDescription"] = ticketDescription.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
//        formData["ispTicketCategoryId"] = ispTicketCategoryId.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
//        formData["ispUserId"] = ispUserId.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())

//        val formData = HashMap<String, String>()
//        formData["ticketSummary"] = ticketSummary
//        formData["ticketDescription"] = ticketDescription
//        formData["ispTicketCategoryId"] = ispTicketCategoryId
//        formData["ispUserId"] = ispUserId

//        val formData = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("ispTicketCategoryId", ispTicketCategoryId)
//            .addFormDataPart()
//            .addFormDataPart()
//            .addFormDataPart()
//            .build()

//        val formData = HashMap<String, String>()
//        formData["ispTicketCategoryId"] = ispTicketCategoryId
//        formData["ispUserId"] = ispTicketCategoryId
//        formData["ticketDescription"] = ispTicketCategoryId
//        formData["ticketSummary"] = ispTicketCategoryId

        return withContext(Dispatchers.IO) {
            apiService.saveupdateispticket(ticketSummary.toRequestBody("text/plain".toMediaTypeOrNull()),
                ticketDescription.toRequestBody("text/plain".toMediaTypeOrNull()),
                ispTicketCategoryId.toRequestBody("text/plain".toMediaTypeOrNull()),
                ispUserId.toRequestBody("text/plain".toMediaTypeOrNull()))
        }
    }

    suspend fun ticketCommentEntryRepo(ispTicketId: String, ticketComment: String): Response<DefaultResponse> {
        val user = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)
        val ispUserId = user.userID.toString()

        return withContext(Dispatchers.IO) {
            apiService.saveispticketconversation(ispTicketId.toRequestBody("text/plain".toMediaTypeOrNull()),
                ispUserId.toRequestBody("text/plain".toMediaTypeOrNull()),
                ticketComment.toRequestBody("text/plain".toMediaTypeOrNull()),
                null)
        }
    }
}