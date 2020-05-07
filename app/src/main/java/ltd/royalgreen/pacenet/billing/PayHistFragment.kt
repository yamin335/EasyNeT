package ltd.royalgreen.pacenet.billing

import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.*
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
import com.google.gson.Gson
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.billing.bkash.BKashPaymentWebDialog
import ltd.royalgreen.pacenet.billing.foster.FosterPaymentWebDialog
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BillingPaymentTabBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.login.LoggedUserID
import ltd.royalgreen.pacenet.util.*
import javax.inject.Inject

class PayHistFragment : Fragment(), Injectable, RechargeConfirmDialog.RechargeConfirmCallback,
    BKashPaymentWebDialog.BkashPaymentCallback,
    FosterPaymentWebDialog.FosterPaymentCallback {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModelReference by viewModels<PayHistViewModel>()

    private val viewModel: PayHistViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingPaymentTabBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Payment History
    private lateinit var adapter: PaymentListAdapter

    private var expanded = false

    private lateinit var toggle: Transition

    private lateinit var bkashPaymentDialog: BKashPaymentWebDialog

    private lateinit var fosterPaymentDialog: FosterPaymentWebDialog

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
            R.layout.billing_payment_tab,
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

        val userType = Gson().fromJson(preferences.getString("LoggedUserID", null), LoggedUserID::class.java)?.userTypeId

        if (userType == 1) {
            viewModel.getUserPackServiceList()
            binding.payBill.visibility = View.VISIBLE
            binding.payBill.setOnClickListener {
                 val serviceList = viewModel.userPackServiceList.value
                if (serviceList != null && serviceList.size > 0) {
                    val allServiceDialog = UserAllServiceDialog(object : UserAllServiceDialog.PayBillCallback {
                        override fun onAmountReceived(
                            invoiceId: Int,
                            userPackServiceId: Int,
                            amount: Double
                        ) {
                            if (amount > 0.0) {
                                val userBalance = viewModel.userBalance.value?.balanceAmount
                                if ( userBalance != null && userBalance >= amount) {
                                    viewModel.billPaymentHelper = BillPaymentHelper(balanceAmount = 0.0, deductedAmount = amount, invoiceId = invoiceId, userPackServiceId = userPackServiceId)
                                    viewModel.billPaymentHelper?.let { billPayment ->
                                        viewModel.saveNewPaymentFromBalance(billPayment)
                                    }
                                } else if (userBalance != null && userBalance < amount) {
                                    val balanceAmount = amount - userBalance
                                    viewModel.billPaymentHelper = BillPaymentHelper(balanceAmount = balanceAmount, deductedAmount = userBalance, invoiceId = invoiceId, userPackServiceId = userPackServiceId)
                                    viewModel.billPaymentHelper?.let {
                                        showRechargeConfirmDialog(amount)
                                    }
                                } else {
                                    viewModel.billPaymentHelper = BillPaymentHelper(balanceAmount = amount, deductedAmount = 0.0, invoiceId = invoiceId, userPackServiceId = userPackServiceId)
                                    viewModel.billPaymentHelper?.let {
                                        showRechargeConfirmDialog(amount)
                                    }
                                }
                            } else {
                                showErrorToast(requireContext(), "Payment amount must be greater than 0.0 BDT")
                            }
                        }
                    }, serviceList)
                    allServiceDialog.isCancelable = true
                    allServiceDialog.show(childFragmentManager, "#all_service_dialog")
                } else {
                    showErrorToast(requireContext(), "You don't have any active service yet!")
                }
            }
        } else if (userType == 2) {
            binding.dues.visibility = View.VISIBLE
            viewModel.userBalance.observe(viewLifecycleOwner, Observer { userBalance ->
                userBalance?.let {
                    binding.dues.text = if (userBalance.duesAmount != null)
                        "Dues: ${userBalance.duesAmount.toRounded(2)} BDT"
                    else "Dues: 0.0 BDT"
                }
            })
        }

        adapter = PaymentListAdapter()

        binding.paymentRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.paymentRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 16))
        binding.paymentRecycler.adapter = adapter

        binding.applyFilter.setOnClickListener {
            applyFilter()
        }

        binding.reset.setOnClickListener {
            resetSearch()
        }

        viewModel.toastPublisher.observe(viewLifecycleOwner, Observer { (isSuccess, message) ->
            if (isSuccess) {
                showSuccessToast(requireContext(), message)
            } else {
                showErrorToast(requireContext(), message)
            }
        })

        viewModel.bKashToken.observe(viewLifecycleOwner, Observer { bkashDataModel ->
            if (bkashDataModel != null) {
                bkashPaymentDialog = BKashPaymentWebDialog(this, bkashDataModel.createBkashModel,
                    bkashDataModel.paymentRequest, viewModel.billPaymentHelper!!)
                bkashPaymentDialog.isCancelable = false
                bkashPaymentDialog.show(childFragmentManager, "#bkash_payment_dialog")
            }
        })

        viewModel.fosterUrl.observe(viewLifecycleOwner, Observer { (paymentProcessUrl, paymentStatusUrl) ->
            if (paymentProcessUrl != null && paymentStatusUrl != null && viewModel.billPaymentHelper != null) {
                fosterPaymentDialog =
                    FosterPaymentWebDialog (
                        this,
                        paymentProcessUrl,
                        paymentStatusUrl,
                        viewModel.billPaymentHelper!!
                    )
                fosterPaymentDialog.isCancelable = false
                fosterPaymentDialog.show(childFragmentManager, "#foster_payment_dialog")
            }
        })

        //1
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        //2
        viewModel.paymentHistoryList = initializedPagedListBuilder(config).build()

        //3
        viewModel.paymentHistoryList.observe(viewLifecycleOwner, Observer { pagedList ->
            adapter.submitList(pagedList)
        })

        viewModel.getUserBalance()
    }

    fun toggleExpanded() {
        expanded = !expanded
        toggle.duration = if (expanded) 200L else 150L
        TransitionManager.beginDelayedTransition(binding.rootView as ViewGroup, toggle)
        binding.searchContainer.visibility = if (expanded) View.VISIBLE else View.GONE
        if (!expanded) {
            viewModel.fromDate.value = "dd/mm/yyyy"
            viewModel.toDate.value = "dd/mm/yyyy"
            viewModel.searchValue.value = ""
            viewModel.paymentHistoryList.value?.dataSource?.invalidate()
        }
    }

    private fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, PaymentTransaction> {
        val dataSourceFactory = object : DataSource.Factory<Long, PaymentTransaction>() {
            override fun create(): DataSource<Long, PaymentTransaction> {
                return PayHistDataSource(viewModelReference)
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }

    private fun showRechargeConfirmDialog(amount: Double) {
        val rechargeConfirmDialog = RechargeConfirmDialog(this, amount)
        rechargeConfirmDialog.isCancelable = false
        rechargeConfirmDialog.show(childFragmentManager, "#recharge_confirm_dialog")
    }

    override fun onFosterClicked(amount: Double) {
        viewModel.getFosterPaymentUrl(amount)
    }

    override fun onBKashClicked(amount: Double) {
        if (viewModel.hasBkashToken && viewModel.billPaymentHelper != null) {
            bkashPaymentDialog = BKashPaymentWebDialog(this, viewModel.bKashToken.value?.createBkashModel!!,
                viewModel.bKashToken.value?.paymentRequest!!, viewModel.billPaymentHelper!!)
            bkashPaymentDialog.isCancelable = false
            bkashPaymentDialog.show(childFragmentManager, "#bkash_payment_dialog")
        } else {
            viewModel.getBkashToken()
        }
    }

    override fun onBkashPaymentFinished() {
        bkashPaymentDialog.dismiss()
        viewModel.hasBkashToken = false
        viewModel.billPaymentHelper = null
        viewModel.bKashToken.postValue(null)
        refreshUI()
    }

    override fun onFosterPaymentFinished() {
        fosterPaymentDialog.dismiss()
        viewModel.billPaymentHelper = null
        viewModel.fosterUrl.postValue(Pair(null, null))
        refreshUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.bKashToken.postValue(null)
        viewModel.fosterUrl.postValue(Pair(null, null))
        viewModel.hasBkashToken = false
        if (expanded) toggleExpanded()
    }

    private fun applyFilter() {
        viewModel.paymentHistoryList.value?.dataSource?.invalidate()
    }

    private fun resetSearch() {
        viewModel.fromDate.value = "dd/mm/yyyy"
        viewModel.toDate.value = "dd/mm/yyyy"
        viewModel.searchValue.value = ""
        viewModel.paymentHistoryList.value?.dataSource?.invalidate()
        toggleExpanded()
    }

    private fun refreshUI() {
        viewModel.fromDate.value = "dd/mm/yyyy"
        viewModel.toDate.value = "dd/mm/yyyy"
        viewModel.searchValue.value = ""
        viewModel.paymentHistoryList.value?.dataSource?.invalidate()
    }
}