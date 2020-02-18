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
class BillingFragment : Fragment(), Injectable, BillingRechargeDialog.RechargeCallback, RechargeConfirmDialog.RechargeConfirmCallback, BKashPaymentWebDialog.BkashPaymentCallback,
    FosterPaymentWebDialog.FosterPaymentCallback {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var bottomSheetBehaviour: BottomSheetBehavior<View>

    val viewModelReference by viewModels<BillingViewModel>()

    private val viewModel: BillingViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val viewPagerFragments: Array<Fragment> = arrayOf(PayHistFragment(), RechargeHistFragment())
    private val viewPagerPageTitles = arrayOf("Payments", "Recharges")

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
        binding.includedBottomSheet.viewModel = viewModel

        binding.includedContentMain.viewModel = viewModel

        viewModel.bKashToken.observe(viewLifecycleOwner, Observer { bkashDataModel ->
            if (bkashDataModel != null) {
                val bkashPaymentDialog = BKashPaymentWebDialog(this, bkashDataModel.createBkashModel, bkashDataModel.paymentRequest)
                bkashPaymentDialog.isCancelable = false
                bkashPaymentDialog.show(parentFragmentManager, "#bkash_payment_dialog")
            }
        })

        viewModel.fosterUrl.observe(viewLifecycleOwner, Observer { (paymentProcessUrl, paymentStatusUrl) ->
            if (paymentProcessUrl != null && paymentStatusUrl != null) {
                val fosterPaymentDialog =
                    FosterPaymentWebDialog(
                        this,
                        paymentProcessUrl,
                        paymentStatusUrl
                    )
                fosterPaymentDialog.isCancelable = false
                fosterPaymentDialog.show(parentFragmentManager, "#foster_payment_dialog")
            }
        })

        val paymentStatus = preferences.getString("paymentRechargeStatus", null)
        paymentStatus?.let {
            if (it == "true") {


            } else if (it == "false"){
                showErrorToast(requireContext(), "Payment not successful !")
            }

            preferences.edit().apply {
                putString("paymentRechargeStatus", "null")
                apply()
            }
        }

        viewModel.prepareBalance()

        binding.includedContentMain.viewPager.adapter = BillingViewPagerAdapter(viewPagerFragments, childFragmentManager, viewLifecycleOwner.lifecycle)

        bottomSheetBehaviour = BottomSheetBehavior.from(binding.includedBottomSheet.bottomSheet)
        binding.searchFab.setOnClickListener{
            if (bottomSheetBehaviour.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                binding.searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_black_24dp, activity!!.theme))
            } else {
                bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED)
                binding.searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, activity!!.theme))
            }
        }

        binding.includedContentMain.rechargeButton.setOnClickListener {
            showRechargeDialog()
        }

        binding.includedBottomSheet.applyFilter.setOnClickListener {
            //applySearch()
        }

        TabLayoutMediator(binding.includedContentMain.tabs, binding.includedContentMain.viewPager) { tab, position ->
            tab.text = viewPagerPageTitles[position]
        }.attach()
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

    private fun showRechargeDialog() {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        user?.let {
            val rechargeDialog = BillingRechargeDialog(this, it.fullName)
            rechargeDialog.isCancelable = false
            rechargeDialog.show(parentFragmentManager, "#recharge_dialog")
        }
    }

    private fun showRechargeConfirmDialog(amount: String, note: String) {
//        val rechargeConfirmDialog = RechargeConfirmDialog(this, rechargeResponse?.resdata?.amount, note, rechargeResponse?.resdata?.paymentProcessUrl)
//        rechargeConfirmDialog.isCancelable = false
//        rechargeConfirmDialog.show(parentFragmentManager, "#recharge_confirm_dialog")

        val rechargeConfirmDialog = RechargeConfirmDialog(this, amount, note)
        rechargeConfirmDialog.isCancelable = false
        rechargeConfirmDialog.show(parentFragmentManager, "#recharge_confirm_dialog")
    }

    override fun onSavePressed(date: String, amount: String, note: String) {
        showRechargeConfirmDialog(amount, note)
    }

    override fun onFosterClicked(amount: String, note: String) {
        viewModel.getFosterPaymentUrl(amount, note)
    }

    override fun onBKashClicked(amount: String) {
        if (viewModel.hasBkashToken) {
            val bkashPaymentDialog = BKashPaymentWebDialog(this, viewModel.bKashToken.value?.createBkashModel!!, viewModel.bKashToken.value?.paymentRequest!!)
            bkashPaymentDialog.isCancelable = false
            bkashPaymentDialog.show(parentFragmentManager, "#bkash_payment_dialog")
        } else {
            viewModel.getBkashToken(amount)
        }
    }

    override fun onPaymentSuccess() {
        viewModel.hasBkashToken = false
        //refreshUI()
    }

    override fun onPaymentError() {
        //viewModel.hasBkashToken = false
    }

    override fun onPaymentCancelled() {
        //viewModel.hasBkashToken = false
    }

    override fun onFosterPaymentSuccess() {
        viewModel.fosterUrl.postValue(Pair(null, null))
        //refreshUI()
    }

    override fun onFosterPaymentError() {
        viewModel.fosterUrl.postValue(Pair(null, null))
    }

    override fun onFosterPaymentCancelled() {
        viewModel.fosterUrl.postValue(Pair(null, null))
    }
}
