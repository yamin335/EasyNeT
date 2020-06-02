package ltd.royalgreen.pacenet.profile

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.billing.BillingRepository
import javax.inject.Inject

class PackageChangeViewModel @Inject constructor(private val application: Application, private val repository: ProfileRepository, private val billingRepository: BillingRepository, private val preferences: SharedPreferences) : BaseViewModel()  {

}
