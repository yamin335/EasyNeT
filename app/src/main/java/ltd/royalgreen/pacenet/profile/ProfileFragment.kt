package ltd.royalgreen.pacenet.profile


import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import ltd.royalgreen.pacenet.*
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.ProfileFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.login.ForgotPasswordDialog
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showChangePasswordDialog
import ltd.royalgreen.pacenet.util.showErrorToast
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : MainNavigationFragment(), Injectable {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ProfileViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var mainActivityCallback: MainActivityCallback? = null

    private var binding by autoCleared<ProfileFragmentBinding>()

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityCallback) {
            mainActivityCallback = context
        } else {
            throw RuntimeException("$context must implement MainActivityCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            mainActivityCallback?.onAppExit()
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
            R.layout.profile_fragment,
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

        viewModel.userConnectionId = null
        viewModel.changingUserPackage = null
        viewModel.consumeData.value = null

        viewModel.consumeData.observe(viewLifecycleOwner, Observer {
            val connectionId = viewModel.userConnectionId
            val packService = viewModel.changingUserPackage
            if (it != null && connectionId != null && packService != null) {
                if (it.isPossibleChange == true) {
                    val action = ProfileFragmentDirections.actionProfileFragmentToPackageChangeFragment(packService, connectionId, it)
                    findNavController().navigate(action)
                } else {
                    showErrorToast(requireContext(), "Try next day again! or contact with support")
                }
            }
        })

        viewModel.userPackServiceList.observe(viewLifecycleOwner, Observer {
            val userPackServiceAdapter = UserPackServiceListAdapter(it, object : UserPackServiceListAdapter.ChangeButtonCallback {
                override fun onChangeClicked(userPackService: UserPackService) {
                    if (userPackService.packServiceTypeId == 1) {
                        viewModel.changingUserPackage = userPackService
                        viewModel.getConsumeData(userPackService.userPackServiceId ?: 0)
                    } else {
                        showErrorToast(requireContext(), "Please contact with our office!")
                    }
                }
            })
            binding.serviceRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 16))
            binding.serviceRecycler.adapter = userPackServiceAdapter
        })
        viewModel.prepareProfile()
        viewModel.getUserPackServiceList()

//        binding.changePackage.setOnClickListener {
//
//            val actionChooserDialog = PackServiceActionChooserDialog(object : PackServiceActionChooserDialog.ChooserActionCallback {
//                override fun onPackageAdd() {
//                    val action = ProfileFragmentDirections.actionProfileFragmentToPackageAddNewFragment()
//                    findNavController().navigate(action)
//                }
//
//                override fun onPackageChange() {
//
//                }
//
//                override fun onServiceAdd() {
//
//                }
//
//                override fun onServiceChange() {
//
//                }
//
//                override fun onShowUserPackService() {
//
//                }
//
//            })
//            actionChooserDialog.isCancelable = true
//            actionChooserDialog.show(childFragmentManager, "#action_chooser_dialog")
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                mainActivityCallback?.onLogOut()
                true
            }
            R.id.change_password -> {
                showChangePasswordDialog(childFragmentManager)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
