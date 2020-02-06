package ltd.royalgreen.pacenet.dashboard

import android.app.Application
import android.content.SharedPreferences
import ltd.royalgreen.pacenet.BaseViewModel
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val application: Application) : BaseViewModel() {

    @Inject
    lateinit var preferences: SharedPreferences
}