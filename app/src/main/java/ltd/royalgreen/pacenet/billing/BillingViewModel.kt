package ltd.royalgreen.pacenet.billing

import android.app.Application
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.paymentmodule.bkash.BkashDataModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.CreateBkashModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.PaymentRequest
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.network.ApiEmptyResponse
import ltd.royalgreen.pacenet.network.ApiErrorResponse
import ltd.royalgreen.pacenet.network.ApiResponse
import ltd.royalgreen.pacenet.network.ApiSuccessResponse
import java.util.*
import javax.inject.Inject

class BillingViewModel @Inject constructor(val application: Application, private val repository: BillingRepository) : BaseViewModel() {

}