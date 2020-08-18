package ltd.royalgreen.pacenet.billing

import android.app.Application
import ltd.royalgreen.pacenet.BaseViewModel
import javax.inject.Inject

class BillingViewModel @Inject constructor(val application: Application, private val repository: BillingRepository) : BaseViewModel(application)