package ltd.royalgreen.pacenet.billing

import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.billing.bkash.BKashPaymentWebDialog
import ltd.royalgreen.pacenet.billing.foster.FosterPaymentWebDialog
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BillingRechargeTabBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showErrorToast
import javax.inject.Inject

class RechargeHistFragment : Fragment(), Injectable, BillingRechargeDialog.RechargeCallback, RechargeConfirmDialog.RechargeConfirmCallback, BKashPaymentWebDialog.BkashPaymentCallback,
    FosterPaymentWebDialog.FosterPaymentCallback {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: RechargeHistViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingRechargeTabBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Payment History
    private lateinit var adapter: RechargeListAdapter

    private var expanded = false

    private lateinit var toggle: Transition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toggle = TransitionInflater.from(requireContext()).inflateTransition(R.transition.search_bar_toogle)

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
            R.layout.billing_recharge_tab,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        adapter = RechargeListAdapter()

        binding.rechargeRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.rechargeRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 8))
        binding.rechargeRecycler.adapter = adapter

        binding.searchFab.setOnClickListener{
            toggleExpanded()
        }

        binding.applyFilter.setOnClickListener {
            applyFilter()
        }

        binding.reset.setOnClickListener {
            resetSearch()
        }

        //1
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        //2
        viewModel.rechargeHistoryList = initializedPagedListBuilder(config).build()

        //3
        viewModel.rechargeHistoryList.observe(viewLifecycleOwner, Observer<PagedList<RechargeTransaction>> { pagedList ->
            adapter.submitList(pagedList)
        })

        binding.rechargeButton.setOnClickListener {
            showRechargeDialog()
        }

        viewModel.prepareBalance()

        observeBKashToken()

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
    }

    fun toggleExpanded() {
        expanded = !expanded
        toggle.duration = if (expanded) 200L else 150L
        TransitionManager.beginDelayedTransition(binding.rootView as ViewGroup, toggle)
        binding.searchContainer.visibility = if (expanded) View.VISIBLE else View.GONE
        if (expanded) {
            binding.searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_black_24dp, activity!!.theme))
        } else {
            viewModel.fromDate.value = "dd/mm/yyyy"
            viewModel.toDate.value = "dd/mm/yyyy"
            viewModel.searchValue.value = ""
            viewModel.rechargeHistoryList.value?.dataSource?.invalidate()
            binding.searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, activity!!.theme))
        }
    }

    private fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, RechargeTransaction> {
        val dataSourceFactory = object : DataSource.Factory<Long, RechargeTransaction>() {
            override fun create(): DataSource<Long, RechargeTransaction> {
                return RechargeHistDataSource(viewModel)
            }
        }
        return LivePagedListBuilder<Long, RechargeTransaction>(dataSourceFactory, config)
    }

    private fun applyFilter() {
        viewModel.rechargeHistoryList.value?.dataSource?.invalidate()
    }

    private fun resetSearch() {
        viewModel.fromDate.value = "dd/mm/yyyy"
        viewModel.toDate.value = "dd/mm/yyyy"
        viewModel.searchValue.value = ""
        viewModel.rechargeHistoryList.value?.dataSource?.invalidate()
        toggleExpanded()
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.bKashToken.postValue(null)
        viewModel.hasBkashToken = false
        if (expanded) toggleExpanded()
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

    private fun observeBKashToken() {
        viewModel.bKashToken.observe(viewLifecycleOwner, Observer { bkashDataModel ->
            if (bkashDataModel != null) {
                val bkashPaymentDialog = BKashPaymentWebDialog(this, bkashDataModel.createBkashModel, bkashDataModel.paymentRequest)
                bkashPaymentDialog.isCancelable = false
                bkashPaymentDialog.show(parentFragmentManager, "#bkash_payment_dialog")
            }
        })
    }
}