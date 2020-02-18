package ltd.royalgreen.pacenet.support

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import ltd.royalgreen.pacenet.BaseViewModel
import javax.inject.Inject

class SupportViewModel @Inject constructor(val application: Application, private val repository: SupportRepository) : BaseViewModel() {

    lateinit var supportTicketHistList: LiveData<PagedList<SupportTicket>>

    suspend fun getAllSupportTicketHist(pageNumber: Long, pageSize: Int,
                                        searchValue: String?, SDate: String?,
                                        EDate: String?)
            = repository.supportTicketHistRepo(pageNumber, pageSize, searchValue, SDate, EDate)

}