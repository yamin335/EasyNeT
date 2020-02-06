package ltd.royalgreen.pacenet.billing

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.TextView
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
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.billing_payment_tab.*
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.SplashActivity
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BillingFragmentBinding
import ltd.royalgreen.pacenet.databinding.BillingPaymentTabBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showChangePasswordDialog
import javax.inject.Inject

class BillPayHistFragment : Fragment(), Injectable {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModelReference by viewModels<BillPayHistViewModel>()

    private val viewModel: BillPayHistViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingPaymentTabBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Payment History
    private lateinit var adapter: PaymentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        adapter = PaymentListAdapter()

        binding.paymentRecycler.layoutManager = LinearLayoutManager(activity)
        binding.paymentRecycler.addItemDecoration(RecyclerItemDivider(activity!!.applicationContext, LinearLayoutManager.VERTICAL, 8))
        binding.paymentRecycler.adapter = adapter

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

    private fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, PaymentTransaction> {
        val dataSourceFactory = object : DataSource.Factory<Long, PaymentTransaction>() {
            override fun create(): DataSource<Long, PaymentTransaction> {
                return PayHistDataSource(viewModelReference)
            }
        }
        return LivePagedListBuilder<Long, PaymentTransaction>(dataSourceFactory, config)
    }
}