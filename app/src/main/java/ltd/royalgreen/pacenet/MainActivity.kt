package ltd.royalgreen.pacenet

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.main_activity.*
import ltd.royalgreen.pacenet.databinding.MainActivityBinding
import ltd.royalgreen.pacenet.network.ApiService
import ltd.royalgreen.pacenet.network.NetworkStatusDialog
import ltd.royalgreen.pacenet.util.setupWithNavController
import javax.inject.Inject

const val SHARED_PREFS_KEY = "%*APP_DEFAULT_KEY*%"

class MainActivity : AppCompatActivity(), HasAndroidInjector {

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

//        viewModel.apiCallStatus.observe(this, Observer {
//            val parent: ViewGroup? = null
//            when(it) {
//                ApiCallStatus.SUCCESS -> Log.d("Successful", "Nothing to do")
//                ApiCallStatus.ERROR -> {
//                    val toast = Toast.makeText(this@MainActivity, "", Toast.LENGTH_LONG)
//                    val inflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
//                    toastView.message.text = this@MainActivity.getString(R.string.error_msg)
//                    toast.view = toastView
//                    toast.show()
//                }
//                ApiCallStatus.NO_DATA -> {
//                    val toast = Toast.makeText(this@MainActivity, "", Toast.LENGTH_LONG)
//                    val inflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
//                    toastView.message.text = this@MainActivity.getString(R.string.no_data_msg)
//                    toast.view = toastView
//                    toast.show()
//                }
//                ApiCallStatus.EMPTY -> {
//                    val toast = Toast.makeText(this@MainActivity, "", Toast.LENGTH_LONG)
//                    val inflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
//                    toastView.message.text = this@MainActivity.getString(R.string.empty_msg)
//                    toast.view = toastView
//                    toast.show()
//                }
//                ApiCallStatus.TIMEOUT -> {
//                    val toast = Toast.makeText(this@MainActivity, "", Toast.LENGTH_LONG)
//                    val inflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
//                    toastView.message.text = this@MainActivity.getString(R.string.timeout_msg)
//                    toast.view = toastView
//                    toast.show()
//                }
//                else -> Log.d("NOTHING", "Nothing to do")
//            }
//        })

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
            setSupportActionBar(toolbar)
            setupActionBarWithNavController(navController)

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

}
