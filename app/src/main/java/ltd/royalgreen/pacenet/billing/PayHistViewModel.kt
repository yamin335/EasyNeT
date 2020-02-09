package ltd.royalgreen.pacenet.billing

import android.app.Application
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.R
import java.util.*
import javax.inject.Inject

class PayHistViewModel @Inject constructor(val application: Application, private val repository: BillingRepository) : BaseViewModel() {

    @Inject
    lateinit var preferences: SharedPreferences

    val toDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val fromDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val searchValue: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    lateinit var paymentHistoryList: LiveData<PagedList<PaymentTransaction>>

    init {
        fromDate.value = ""
        toDate.value = ""
        searchValue.value = ""
//        fromDate.value = "dd/mm/yyyy"
//        toDate.value = "dd/mm/yyyy"
//        searchValue.value = ""
    }

    suspend fun getPaymentHistory(pageNumber: Long, pageSize: Int, values: String, SDate: String, EDate: String) = repository.paymentHistoryRepo(pageNumber, pageSize, values, SDate, EDate)

    fun pickDate(view: View){
        // calender class's instance and get current date , month and year from calender
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR) // current year
        val mMonth = c.get(Calendar.MONTH) // current month
        val mDay = c.get(Calendar.DAY_OF_MONTH) // current day
        // date picker dialog
        val datePickerDialog = DatePickerDialog(view.context,
            { _, year, monthOfYear, dayOfMonth ->
                when(view.id) {
                    R.id.fromDate -> {
                        fromDate.value = year.toString()+"-"+(monthOfYear + 1)+"-"+dayOfMonth
                    }
                    R.id.toDate -> {
                        toDate.value = year.toString()+"-"+(monthOfYear + 1)+"-"+dayOfMonth
                    }
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }
}