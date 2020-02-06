package ltd.royalgreen.pacenet.profile

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.BaseViewModel
import ltd.royalgreen.pacenet.LoggedUser
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val preferences: SharedPreferences) : BaseViewModel() {
    val name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val balance: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val createDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val packageName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val packageCharge: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val phone: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun prepareProfile() {
        viewModelScope.launch {
            val loggedUser = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            name.postValue(loggedUser.displayName)
            balance.postValue(loggedUser.balance.toString() + " (BDT)")
            createDate.postValue(loggedUser.created?.split("T")!![0])
            packageName.postValue(loggedUser.srvName)
            packageCharge.postValue(loggedUser.unitPrice.toString() + " (BDT)")
            email.postValue(loggedUser.email)
            phone.postValue(loggedUser.phone)
        }
    }
}