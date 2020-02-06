package ltd.royalgreen.pacenet

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ltd.royalgreen.pacenet.util.isNetworkAvailable
import ltd.royalgreen.pacenet.util.showErrorToast

open class BaseViewModel : ViewModel() {

    val apiCallStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun checkNetworkStatus(application: Application) = if (isNetworkAvailable(application)) {
        true
    } else {
        showErrorToast(application, application.getString(R.string.net_error_msg))
        false
    }

    fun onAppExit(preferences: SharedPreferences) {
        preferences.edit().apply {
            putString("LoggedUserPassword",null)
            putString("LoggedUserID", null)
            putBoolean("goToLogin", false)
            apply()
        }
    }

    fun onLogOut(preferences: SharedPreferences) {
        preferences.edit().apply {
            putString("LoggedUserPassword",null)
            putString("LoggedUserID", null)
            putBoolean("goToLogin", true)
            apply()
        }
    }
}