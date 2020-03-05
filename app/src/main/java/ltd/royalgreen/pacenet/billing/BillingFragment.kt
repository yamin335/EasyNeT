package ltd.royalgreen.pacenet.billing

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.SplashActivity
import ltd.royalgreen.pacenet.billing.bkash.BKashPaymentWebDialog
import ltd.royalgreen.pacenet.billing.foster.FosterPaymentWebDialog
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BillingFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showChangePasswordDialog
import ltd.royalgreen.pacenet.util.showErrorToast
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class BillingFragment : Fragment(), Injectable {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: BillingViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var viewPagerFragments: Array<Fragment>
    private val viewPagerPageTitles = arrayOf("Payments", "Recharges")

    private lateinit var pagerAdapter: BillingViewPagerAdapter

    private var viewPagerCurrentItem = 0

    private lateinit var viewPager2PageChangeCallback: ViewPager2PageChangeCallback

    private val payHistFragment: PayHistFragment = PayHistFragment()
    private val rechargeHistFragment: RechargeHistFragment = RechargeHistFragment()

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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.billing_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        //payHistFragment = PayHistFragment()
        //rechargeHistFragment = RechargeHistFragment()

        viewPagerFragments = arrayOf(payHistFragment, rechargeHistFragment)

        pagerAdapter = BillingViewPagerAdapter(viewPagerFragments, childFragmentManager, lifecycle)

        binding.includedContentMain.viewPager.adapter = pagerAdapter

        viewPager2PageChangeCallback = ViewPager2PageChangeCallback {
            setCurrentPageItemPosition(it)
        }

        binding.includedContentMain.viewPager.registerOnPageChangeCallback(viewPager2PageChangeCallback)

        TabLayoutMediator(binding.includedContentMain.tabs, binding.includedContentMain.viewPager) { tab, position ->
            tab.text = viewPagerPageTitles[position]
        }.attach()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        binding.includedContentMain.viewPager.unregisterOnPageChangeCallback(viewPager2PageChangeCallback)
//    }

    private fun setCurrentPageItemPosition(position: Int) {
        viewPagerCurrentItem = position
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.billing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {

            R.id.search -> {
                when (viewPagerCurrentItem) {
                    0 -> {
                        payHistFragment.toggleExpanded()
                    }
                    1 -> {
                        rechargeHistFragment.toggleExpanded()
                    }
                }
                true
            }

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

class ViewPager2PageChangeCallback(private val listener: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        listener.invoke(position)
    }
}
