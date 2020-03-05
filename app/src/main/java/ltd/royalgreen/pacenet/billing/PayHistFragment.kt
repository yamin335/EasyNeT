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
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BillingPaymentTabBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import javax.inject.Inject

class PayHistFragment : Fragment(), Injectable {

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

        adapter = PaymentListAdapter()

        binding.paymentRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.paymentRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 8))
        binding.paymentRecycler.adapter = adapter

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
        viewModel.paymentHistoryList = initializedPagedListBuilder(config).build()

        //3
        viewModel.paymentHistoryList.observe(viewLifecycleOwner, Observer<PagedList<PaymentTransaction>> { pagedList ->
            adapter.submitList(pagedList)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (expanded) toggleExpanded()
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
            viewModel.paymentHistoryList.value?.dataSource?.invalidate()
            binding.searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, activity!!.theme))
        }
    }

    private fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, PaymentTransaction> {
        val dataSourceFactory = object : DataSource.Factory<Long, PaymentTransaction>() {
            override fun create(): DataSource<Long, PaymentTransaction> {
                return PayHistDataSource(viewModelReference)
            }
        }
        return LivePagedListBuilder<Long, PaymentTransaction>(dataSourceFactory, config)
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
}