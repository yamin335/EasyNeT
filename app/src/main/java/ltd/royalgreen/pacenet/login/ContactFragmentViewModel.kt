package ltd.royalgreen.pacenet.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ltd.royalgreen.pacenet.BaseViewModel
import javax.inject.Inject

class ContactFragmentViewModel @Inject constructor(private val application: Application) : BaseViewModel() {

    val currentNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}