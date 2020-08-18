package ltd.royalgreen.pacenet

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.main_activity.*
import ltd.royalgreen.pacenet.dashboard.DashboardFragment
import ltd.royalgreen.pacenet.databinding.MainActivityBinding
import ltd.royalgreen.pacenet.network.ApiService
import ltd.royalgreen.pacenet.network.NetworkStatusDialog
import ltd.royalgreen.pacenet.util.setupWithNavController
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

const val SHARED_PREFS_KEY = "%*APP_DEFAULT_KEY*%"

class MainActivity : AppCompatActivity(), NavigationHost, HasAndroidInjector, DashboardFragment.DashItemInteractionListener, MainActivityCallback {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var preferences: SharedPreferences

    lateinit var exitDialog: NetworkStatusDialog

    private var currentNavController: LiveData<NavController>? = null

    var navigatedFromDashboard = false


//    var listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
//        when (key) {
//            "UserBalance" -> {
//                val userBalance = Gson().fromJson(prefs.getString("UserBalance", null), BalanceModel::class.java)
//                userBalance?.let {
//                    viewModel.userBalance.value = it
//                }
//            }
//        }
//    }

//    override fun onResume() {
//        super.onResume()
//        preferences.registerOnSharedPreferenceChangeListener(listener)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        preferences.unregisterOnSharedPreferenceChangeListener(listener)
//    }

    override fun androidInjector() = dispatchingAndroidInjector

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onAppExit(preferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

//        preferences.registerOnSharedPreferenceChangeListener(listener)
        val binding: MainActivityBinding = DataBindingUtil.setContentView(
            this, R.layout.main_activity
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

//        viewModel.userBalance.observe(this, Observer { balance ->
//            nav_view.getHeaderView(0).loggedUserBalance.text = BigDecimal(balance.resdata?.billCloudUserBalance?.balanceAmount?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
//        })

        viewModel.isLoggedOut.observe(this, Observer {(logout, message) ->
            if (logout) {
                viewModel.onLogOut(preferences)
                showSuccessToast(this, message)
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            } else if (!message.isBlank()) {
                showErrorToast(this, message)
            }
        })

        exitDialog = NetworkStatusDialog(object : NetworkStatusDialog.NetworkChangeCallback {
            override fun onExit() {
                this@MainActivity.finish()
            }
        })
        exitDialog.isCancelable = false

        viewModel.internetStatus.observe(this, Observer {
            if (it) {
                if (exitDialog.isVisible)
                    exitDialog.dismiss()
            } else {
                if (!exitDialog.isAdded )
                    exitDialog.show(supportFragmentManager, "#net_status_dialog")
            }
        })

        if (savedInstanceState == null) {
            viewModel.getLoggedUserData()
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

//        bottom_nav.setOnNavigationItemSelectedListener {
//            it.isCheckable = true
//            it.isChecked = true
//            false
//        }
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        val navGraphIds = listOf(
            R.navigation.dashboard_nav_graph,
            R.navigation.profile_nav_graph,
            R.navigation.billing_nav_graph,
            R.navigation.support_nav_graph
        )

        // Setup the bottom navigation view with a payment_graph of navigation graphs
        val controller = bottom_nav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
//            appBarConfiguration = AppBarConfiguration(
//                navGraph = navController.graph,
//                drawerLayout = drawer_layout
//            )
            // Set up ActionBar
//            setSupportActionBar(toolbar)
//            setupActionBarWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.dashboardFragment -> {
                        bottom_nav.visibility = View.VISIBLE
                    }
                    R.id.profileFragment -> {
                        bottom_nav.visibility = View.VISIBLE
                    }
                    R.id.billingFragment -> {
                        bottom_nav.visibility = View.VISIBLE
                    }
                    R.id.supportFragment -> {
                        bottom_nav.visibility = View.VISIBLE
                    }
                    else -> bottom_nav.visibility = View.GONE
                }
            }

//            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false || super.onSupportNavigateUp()
    }

//    private fun doSignOut() {
//        val signOutDialog = CustomAlertDialog(object :  CustomAlertDialog.YesCallback{
//            override fun onYes() {
//                val handler = CoroutineExceptionHandler { _, exception ->
//                    exception.printStackTrace()
//                }
//                CoroutineScope(Dispatchers.IO).launch(handler) {
//                    preferences.edit().apply {
//                        putString("LoggedUser", "")
//                        apply()
//                    }
//                }
//                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//                finish()
//            }
//        }, "Do you want to sign out?", "")
//        signOutDialog.show(supportFragmentManager, "#app_signout_dialog")
//    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onDashItemClicked(navigation: String) {

        when {
            navigation.equals("PROFILE", true) -> {
                navigatedFromDashboard = true
                bottom_nav.selectedItemId = R.id.profile_nav_graph
            }
            navigation.equals("PAY_HISTORY", true) -> {
                navigatedFromDashboard = true
                bottom_nav.selectedItemId = R.id.billing_nav_graph
            }
            navigation.equals("TICKET_HISTORY", true) -> {
                navigatedFromDashboard = true
                bottom_nav.selectedItemId = R.id.support_nav_graph
            }
        }
    }

    override fun setupToolbar(toolbar: MaterialToolbar) {
        setSupportActionBar(toolbar)
        currentNavController?.value?.let {
            setupActionBarWithNavController(it)
        }
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        currentNavController?.value?.let {
            setupActionBarWithNavController(it)
        }
    }

    override fun onLogOut() {
        val exitDialog = CustomAlertDialog(object :
            CustomAlertDialog.YesCallback {
            override fun onYes() {
                viewModel.doLogOut()
            }
        }, "Do you want to Sign Out?", "")
        exitDialog.show(supportFragmentManager, "#sign_out_dialog")
    }

    override fun onAppExit() {
        if (navigatedFromDashboard) {
            navigatedFromDashboard = false
            bottom_nav.selectedItemId = R.id.dashboard_nav_graph
        } else {
            val exitDialog = CustomAlertDialog(object :
                CustomAlertDialog.YesCallback {
                override fun onYes() {
                    viewModel.onAppExit(preferences)
                    finish()
                }
            }, "Do you want to exit?", "")
            exitDialog.show(supportFragmentManager, "#app_exit_dialog")
        }
    }

}
