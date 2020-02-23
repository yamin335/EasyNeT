package ltd.royalgreen.pacenet.util

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.*
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.toast_custom_green.view.*
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.login.ForgotPasswordDialog

class ConnectivityLiveData @VisibleForTesting internal constructor(private val connectivityManager: ConnectivityManager)
    : LiveData<Boolean>() {

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    constructor(application: Application) : this(application.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            postValue(true)
        }

        override fun onLost(network: Network?) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()

        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnectedOrConnecting == true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}

class PermissionUtils {
    companion object {
        fun checkExternalStoragePermission(context: Context): Boolean {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        fun requestExternalStoragePermission(context: Context, activity: Activity, fragmentManager: FragmentManager, permissionRequestCode: Int) {
            val shouldProvidePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)

            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            if (shouldProvidePermissionRationale) {
                val rationaleDialog = CustomAlertDialog(object :
                    CustomAlertDialog.YesCallback {
                    override fun onYes() {
                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), permissionRequestCode)
                    }
                }, "Requires Storage Permission", "You must have to allow this permission for further process")
                rationaleDialog.show(fragmentManager, "#PERMISSION_REQUEST_DIALOG")
            } else {
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), permissionRequestCode)
            }
        }
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    if (currentFocus == null) {
        hideKeyboard(View(this))
    } else {
        hideKeyboard(currentFocus as View)
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
}

fun showErrorToast(context: Context, message: String) {
    val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val toastView = inflater.inflate(R.layout.toast_custom_red, null)
    toastView.errorMessage.text = message
    toast.view = toastView
    toast.show()
}

fun showSuccessToast(context: Context, message: String) {
    val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val toastView = inflater.inflate(R.layout.toast_custom_green, null)
    toastView.successMessage.text = message
    toast.view = toastView
    toast.show()
}

fun showChangePasswordDialog(fragmentManager: FragmentManager) {
    val forgotPasswordDialog = ForgotPasswordDialog()
    forgotPasswordDialog.isCancelable = true
    forgotPasswordDialog.show(fragmentManager, "#sign_up_dialog")
}