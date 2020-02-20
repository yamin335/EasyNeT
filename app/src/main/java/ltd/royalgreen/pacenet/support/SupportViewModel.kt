package ltd.royalgreen.pacenet.support

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import ltd.royalgreen.pacenet.BaseViewModel
import java.util.*
import javax.inject.Inject

class SupportViewModel @Inject constructor(val application: Application, private val repository: SupportRepository) : BaseViewModel() {

    lateinit var supportTicketHistList: LiveData<PagedList<SupportTicket>>

    init {
        Log.d("S-VIEWMODEL--> ", "${Random().nextInt(8)+1}")
    }

    suspend fun getAllSupportTicketHist(pageNumber: Long, pageSize: Int,
                                        searchValue: String?, SDate: String?,
                                        EDate: String?)
            = repository.supportTicketHistRepo(pageNumber, pageSize, searchValue, SDate, EDate)

}