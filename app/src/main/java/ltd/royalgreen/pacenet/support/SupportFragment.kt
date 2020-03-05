package ltd.royalgreen.pacenet.support


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
import androidx.navigation.fragment.findNavController
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.SplashActivity
import ltd.royalgreen.pacenet.billing.PayHistDataSource
import ltd.royalgreen.pacenet.billing.PaymentListAdapter
import ltd.royalgreen.pacenet.billing.PaymentTransaction
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.ProfileFragmentBinding
import ltd.royalgreen.pacenet.databinding.SupportFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.login.ForgotPasswordDialog
import ltd.royalgreen.pacenet.profile.ProfileViewModel
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showChangePasswordDialog
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SupportFragment : Fragment(), Injectable {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModelReference by viewModels<SupportViewModel>()

    private val viewModel: SupportViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<SupportFragmentBinding>()

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Support Ticket History
    private lateinit var adapter: SupportTicketListAdapter

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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.support_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.newTicket.setOnClickListener {
            val action = SupportFragmentDirections.actionSupportFragmentToTicketEntryFragment()
            findNavController().navigate(action)
        }

        adapter = SupportTicketListAdapter(object :SupportTicketListAdapter.OnItemClickListenerCallback {
            override fun onItemClicked(supportTicket: SupportTicket) {
                val action = SupportFragmentDirections.actionSupportFragmentToSupportTicketConversation(supportTicket.ispTicketId ?: 0L)
                findNavController().navigate(action)
            }
        })

        binding.ticketRecycler.layoutManager = LinearLayoutManager(activity)
        binding.ticketRecycler.addItemDecoration(RecyclerItemDivider(activity!!.applicationContext, LinearLayoutManager.VERTICAL, 8))
        binding.ticketRecycler.adapter = adapter

        //1
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        //2
        viewModel.supportTicketHistList = initializedPagedListBuilder(config).build()

        //3
        viewModel.supportTicketHistList.observe(viewLifecycleOwner, Observer<PagedList<SupportTicket>> { pagedList ->
            adapter.submitList(pagedList)
        })
    }

    private fun initializedPagedListBuilder(config: PagedList.Config): LivePagedListBuilder<Long, SupportTicket> {
        val dataSourceFactory = object : DataSource.Factory<Long, SupportTicket>() {
            override fun create(): DataSource<Long, SupportTicket> {
                return SupportTicketHistDataSource(viewModelReference)
            }
        }
        return LivePagedListBuilder<Long, SupportTicket>(dataSourceFactory, config)
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

}
