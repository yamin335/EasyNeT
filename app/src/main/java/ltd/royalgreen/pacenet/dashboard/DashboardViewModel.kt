package ltd.royalgreen.pacenet.dashboard

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import java.util.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val application: Application, private val repository: DashRepository) : BaseViewModel() {

    var selectedType = MutableLiveData<String>()
    var selectedMonth = MutableLiveData<Int>()
    val sessionChartData = MutableLiveData<DashSessionResponse>()

    init {
        selectedType.value = "daily"
        // calender class's instance and get current date , month and year from calender
        val c = Calendar.getInstance()
        selectedMonth.value = c.get(Calendar.MONTH) + 1 // current month indexed at 1
        Log.d("D-VIEWMODEL--> ", "${Random().nextInt(8)+1}")
    }

    fun getChartData(): LiveData<DashboardChart> {
        val chartData = MutableLiveData<DashboardChart>()
        if (checkNetworkStatus(application)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.dashChartDataRepo())) {
                    is ApiSuccessResponse -> {
                        chartData.postValue(apiResponse.body)
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
        return chartData
    }

    fun getSessionChartData(month: Int, type: String) {
        selectedType.value = type
        selectedMonth.value = month
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue("ERROR")
                exception.printStackTrace()
            }
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.dashSessionChartRepo(month, type))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue("SUCCESS")
                        sessionChartData.postValue(apiResponse.body)
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
    }
}