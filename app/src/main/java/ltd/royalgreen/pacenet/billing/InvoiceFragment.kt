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
import com.google.gson.Gson
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.LoggedUser
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.billing.bkash.BKashPaymentWebDialog
import ltd.royalgreen.pacenet.billing.foster.FosterPaymentWebDialog
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.dashboard.DashSessionChartChangeDialog
import ltd.royalgreen.pacenet.databinding.BillingInvoiceFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

class InvoiceFragment : Fragment(), Injectable, RechargeConfirmDialog.RechargeConfirmCallback,
    BKashPaymentWebDialog.BkashPaymentCallback,
    FosterPaymentWebDialog.FosterPaymentCallback {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: InvoiceViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingInvoiceFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Payment History
    private lateinit var adapter: InvoiceListAdapter

    private var expanded = false

    private lateinit var toggle: Transition

    private lateinit var bkashPaymentDialog: BKashPaymentWebDialog

    private lateinit var fosterPaymentDialog: FosterPaymentWebDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toggle = TransitionInflater.from(requireContext()).inflateTransition(R.transition.search_bar_toogle)

//        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
//            val exitDialog = CustomAlertDialog(object :
//                CustomAlertDialog.YesCallback {
//                override fun onYes() {
//                    viewModel.onAppExit(preferences)
//                    requireActivity().finish()
//                }
//            }, "Do you want to exit?", "")
//            exitDialog.show(childFragmentManager, "#app_exit_dialog")
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.billing_invoice_fragment,
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

        viewModel.getUserBalance()

        adapter = InvoiceListAdapter(object : InvoiceListAdapter.OnItemClickListener {
            override fun onItemClick(invoice: Invoice?) {
                invoice?.let {
                    val invoiceDetail = InvoiceDetailDialog(object : InvoiceDetailDialog.InvoiceDetailCallback {
                        override fun onPayBill(
                            invoiceId: Int,
                            userPackServiceId: Int,
                            amount: Double
                        ) {
                            if (amount > 0.0) {
                                val userBalance = viewModel.userBalance.value?.balanceAmount
                                if ( userBalance != null && userBalance >= amount) {
                                    viewModel.billPaymentHelper = BillPaymentHelper(balanceAmount = amount, deductedAmount = amount, invoiceId = invoiceId, userPackServiceId = userPackServiceId)
                                    viewModel.billPaymentHelper?.let { billPayment ->
                                        val paymentConfirmDialog = CustomAlertDialog(object :
                                            CustomAlertDialog.YesCallback {
                                            override fun onYes() {
                                                viewModel.saveNewPaymentFromBalance(billPayment)
                                            }
                                        }, "Are you sure to pay from your balance?", "")
                                        paymentConfirmDialog.show(childFragmentManager, "#pay_from_balance_confirm_dialog")
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

                    }, it)
                    invoiceDetail.isCancelable = false
                    invoiceDetail.show(childFragmentManager, "#invoice_detail_dialog")
                }
            }
        })

        binding.rechargeRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.rechargeRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 16))
        binding.rechargeRecycler.adapter = adapter

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
        viewModel.invoiceList = initializedPagedListBuilder(config).build()

        //3
        viewModel.invoiceList.observe(viewLifecycleOwner, Observer { pagedList ->
            adapter.submitList(pagedList)
        })

        viewModel.toastPublisher.observe(viewLifecycleOwner, Observer { pair ->
            pair?.let {
                if (it.first) {
                    showSuccessToast(requireContext(), it.second)
                } else {
                    showErrorToast(requireContext(), it.second)
                }
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
            viewModel.invoiceList.value?.dataSource?.invalidate()
        }
    }

    private fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, Invoice> {
        val dataSourceFactory = object : DataSource.Factory<Long, Invoice>() {
            override fun create(): DataSource<Long, Invoice> {
                return InvoiceDataSource(viewModel)
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.bKashToken.postValue(null)
        viewModel.fosterUrl.postValue(Pair(null, null))
        viewModel.hasBkashToken = false
        if (expanded) toggleExpanded()
        viewModel.toastPublisher.postValue(null)
    }

    override fun onFosterPaymentFinished() {
        fosterPaymentDialog.dismiss()
        viewModel.billPaymentHelper = null
        viewModel.fosterUrl.postValue(Pair(null, null))
        refreshUI()
    }

    private fun applyFilter() {
        viewModel.invoiceList.value?.dataSource?.invalidate()
    }

    private fun resetSearch() {
        viewModel.fromDate.value = "dd/mm/yyyy"
        viewModel.toDate.value = "dd/mm/yyyy"
        viewModel.searchValue.value = ""
        viewModel.invoiceList.value?.dataSource?.invalidate()
        toggleExpanded()
    }

    private fun refreshUI() {
        viewModel.fromDate.value = "dd/mm/yyyy"
        viewModel.toDate.value = "dd/mm/yyyy"
        viewModel.searchValue.value = ""
        viewModel.invoiceList.value?.dataSource?.invalidate()
    }
}