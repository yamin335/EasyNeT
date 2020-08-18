package ltd.royalgreen.pacenet


import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 */
class PrivacyPolicyFragment : MainNavigationFragment() {

    private var windowConfig: Int? = null

    override fun onDestroyView() {
        super.onDestroyView()
        if (windowConfig != null) {
            requireActivity().window.decorView.systemUiVisibility = windowConfig!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //For dark status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val uiOptions = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            windowConfig = requireActivity().window.decorView.systemUiVisibility
            requireActivity().window.decorView.systemUiVisibility = uiOptions
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.privacy_policy_fragment, container, false)
    }


}
