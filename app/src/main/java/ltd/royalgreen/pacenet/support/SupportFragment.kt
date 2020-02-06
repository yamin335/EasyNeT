package ltd.royalgreen.pacenet.support


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.SplashActivity
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.login.ForgotPasswordDialog
import ltd.royalgreen.pacenet.profile.ProfileViewModel
import ltd.royalgreen.pacenet.util.showChangePasswordDialog
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SupportFragment : Fragment(), Injectable {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SupportViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val exitDialog = CustomAlertDialog(object :
                CustomAlertDialog.YesCallback {
                override fun onYes() {
                    viewModel.onAppExit(preferences)
                    requireActivity().finish()
                }
            }, "Do you want to exit?", "")
            exitDialog.show(parentFragmentManager, "#app_exit_dialog")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.support_fragment, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                val exitDialog = CustomAlertDialog(object :
                    CustomAlertDialog.YesCallback {
                    override fun onYes() {
                        viewModel.onLogOut(preferences)
                        startActivity(Intent(requireActivity(), SplashActivity::class.java))
                        requireActivity().finish()
                    }
                }, "Do you want to Sign Out?", "")
                exitDialog.show(parentFragmentManager, "#sign_out_dialog")
                true
            }
            R.id.change_password -> {
                showChangePasswordDialog(parentFragmentManager)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
