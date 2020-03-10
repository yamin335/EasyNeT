package ltd.royalgreen.pacenet.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ltd.royalgreen.pacenet.MainNavigationFragment
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.PackageChangeFragmentBinding
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class PackageChangeFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: PackageChangeViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<PackageChangeFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Support Ticket History
    private lateinit var packageAdapter: PackageAdapter
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var adapter: UserPackageServiceAdapter

    private val packageList = ArrayList<PackageService>()
    private val serviceList = ArrayList<PackageService>()
    private val packServiceList = ArrayList<PackService>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.package_change_fragment,
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

        packageAdapter = PackageAdapter(packageList, object: PackageAdapter.OnItemSelectListener {
            override fun onItemChecked(packageService: PackageService) {
                var temp = viewModel.packageCounter.get()
                viewModel.packageCounter.set(++temp)
            }

            override fun onItemUnChecked(packageService: PackageService) {
                var temp = viewModel.packageCounter.get()
                viewModel.packageCounter.set(--temp)
            }
        })
        serviceAdapter = ServiceAdapter(serviceList, object: ServiceAdapter.OnItemSelectListener {
            override fun onItemChecked(packageService: PackageService) {
                var temp = viewModel.serviceCounter.get()
                viewModel.serviceCounter.set(++temp)
            }

            override fun onItemUnChecked(packageService: PackageService) {
                var temp = viewModel.serviceCounter.get()
                viewModel.serviceCounter.set(--temp)
            }
        })
        adapter = UserPackageServiceAdapter(packServiceList)

        binding.packRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.packRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 15))
        binding.packRecycler.adapter = packageAdapter

        binding.serviceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.serviceRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 15))
        binding.serviceRecycler.adapter = serviceAdapter

        binding.userPackServiceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.userPackServiceRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 15))
        binding.userPackServiceRecycler.adapter = adapter

        viewModel.getUserPackage()
        viewModel.getPackageService()

        binding.save.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.packageList.observe(viewLifecycleOwner, Observer { packList ->
            packageList.clear()
            packList?.let {
                packageList.addAll(it)
                packageAdapter.notifyDataSetChanged()
            }
        })

        viewModel.serviceList.observe(viewLifecycleOwner, Observer { servList ->
            serviceList.clear()
            servList?.let {
                serviceList.addAll(it)
                serviceAdapter.notifyDataSetChanged()
            }
        })

        viewModel.userPackageList.observe(viewLifecycleOwner, Observer { userConnectionList ->
            packServiceList.clear()
            val temp = userConnectionList[0].packServiceList
            temp?.let {
                packServiceList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
    }

}
