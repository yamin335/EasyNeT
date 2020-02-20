package ltd.royalgreen.pacenet.dashboard

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import java.util.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val application: Application, private val repository: DashRepository) : BaseViewModel() {

    init {
        Log.d("D-VIEWMODEL--> ", "${Random().nextInt(8)+1}")
    }

    fun getChartData(): LiveData<DashboardChart> {
        val chartData = MutableLiveData<DashboardChart>()
        if (checkNetworkStatus(application)) {
            viewModelScope.launch {
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
}