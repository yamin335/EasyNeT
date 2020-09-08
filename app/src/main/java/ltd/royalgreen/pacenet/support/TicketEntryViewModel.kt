package ltd.royalgreen.pacenet.support

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import ltd.royalgreen.pacenet.util.DefaultResponse
import ltd.royalgreen.pacenet.util.FileUtils
import ltd.royalgreen.pacenet.util.asFile
import ltd.royalgreen.pacenet.util.asFilePart
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class TicketEntryViewModel @Inject constructor(private val application: Application, private val repository: SupportRepository) : BaseViewModel(application) {

    val selectedTicketCategory: MutableLiveData<TicketCategory> by lazy {
        MutableLiveData<TicketCategory>()
    }

    val ticketSubject: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val ticketDescription: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var fileUriList: ArrayList<Uri> = ArrayList()

    var backupFileUri: Uri? = null

    fun entryNewTicket(): MutableLiveData<DefaultResponse> {
        val result = MutableLiveData<DefaultResponse>()
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                val attachedFileList = ArrayList<MultipartBody.Part>()

                for (uri in fileUriList) {
                    val path = FileUtils.getPathForMediaFile(application, uri)
                    path?.let {
                        attachedFileList.add(File(it).asFilePart())
                    }
                }

//                for (uri in fileUriList) {
//                    val file = uri.asFile(application)?.asFilePart()
//                    file?.let {
//                        attachedFileList.add(it)
//                    }
//                }
                when (val apiResponse = ApiResponse.create(repository.ticketEntryRepo(ticketSubject.value!!,
                    ticketDescription.value!!, selectedTicketCategory.value?.ispTicketCategoryId.toString(), attachedFileList))) {
                    is ApiSuccessResponse -> {
                        result.postValue(apiResponse.body)
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
        return result
    }

    fun getTicketCategory(): MutableLiveData<ArrayList<TicketCategory>> {
        val ticketCategories = MutableLiveData<ArrayList<TicketCategory>>()
        if (checkNetworkStatus()) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.ticketCategoryRepo())) {
                    is ApiSuccessResponse -> {
                        ticketCategories.postValue(apiResponse.body.resdata?.listTicketCategory)
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
        return ticketCategories
    }
}