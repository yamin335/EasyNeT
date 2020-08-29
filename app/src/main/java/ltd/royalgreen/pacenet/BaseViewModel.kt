package ltd.royalgreen.pacenet

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ltd.royalgreen.pacenet.util.isNetworkAvailable
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast

open class BaseViewModel constructor(val context: Application) : ViewModel() {

    val apiCallStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val toastError = MutableLiveData<String>()
    val toastSuccess = MutableLiveData<String>()
    val popBackStack = MutableLiveData<Boolean>()

    fun checkNetworkStatus() = if (isNetworkAvailable(context)) {
        true
    } else {
        showErrorToast(context, context.getString(R.string.net_error_msg))
        false
    }

    fun onAppExit(preferences: SharedPreferences) {
        preferences.edit().apply {
            //putString("LoggedUserPassword",null)
            //putString("LoggedUserID", null)
            putBoolean("goToLogin", false)
            apply()
        }
    }

    fun onLogOut(preferences: SharedPreferences) {
        preferences.edit().apply {
            putString("LoggedUserPassword",null)
            putString("LoggedUserID", null)
            putBoolean("goToLogin", true)
            putBoolean("isLoggedIn", false)
            apply()
        }
    }
}