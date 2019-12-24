package ltd.royalgreen.pacenet.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import kotlinx.android.synthetic.main.dashboard_fragment.*
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.SplashActivity
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.login.ForgotPasswordDialog
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment(), Injectable {

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val exitDialog = CustomAlertDialog(object :
                CustomAlertDialog.YesCallback {
                override fun onYes() {

                    preferences.edit().apply {
                        putString("LoggedUserPassword",null)
                        apply()
                    }

                    preferences.edit().apply {
                        putString("LoggedUser", null)
                        apply()
                    }

                    preferences.edit().apply {
                        putBoolean("goToLogin", false)
                        apply()
                    }
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
        return inflater.inflate(R.layout.dashboard_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        logout.setOnClickListener {
//            startActivity(Intent(requireActivity(), SplashActivity::class.java))
//            requireActivity().finish()
//        }
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

                        preferences.edit().apply {
                            putString("LoggedUserPassword",null)
                            apply()
                        }

                        preferences.edit().apply {
                            putString("LoggedUser", null)
                            apply()
                        }

                        startActivity(Intent(requireActivity(), SplashActivity::class.java))
                        requireActivity().finish()
                    }
                }, "Do you want to Sign Out?", "")
                exitDialog.show(parentFragmentManager, "#sign_out_dialog")
                true
            }
            R.id.change_password -> {
                val forgotPasswordDialog = ForgotPasswordDialog()
                forgotPasswordDialog.isCancelable = true
                forgotPasswordDialog.show(parentFragmentManager, "#sign_up_dialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
