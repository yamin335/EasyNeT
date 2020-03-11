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
            override fun onItemChecked(packageService: PackageService, position: Int) {
                var temp = viewModel.packageCounter.get()
                viewModel.packageCounter.set(++temp)
                val tempUserConnectionList = viewModel.userPackageList.value
                val tempUserConnection = tempUserConnectionList?.get(0)!!
                val newPackService = PackService(0, tempUserConnection.connectionNo,
                    tempUserConnection.connectionTypeId, tempUserConnection.ispUserId,
                    tempUserConnection.parentUserId, tempUserConnection.accountId,
                    packageService.packId, packageService.packServiceId,
                    packageService.packServiceName, packageService.packServiceTypeId,
                    tempUserConnection.zoneId, packageService.packServiceType,
                    packageService.packServicePrice, packServiceInstallCharge = 0,
                    packServiceOthersCharge = 0, isDefault = false,
                    expireDate = "", activeDate = "",
                    isNew = true, isUpdate = false, isDelete = false)

                tempUserConnection.packServiceList?.add(newPackService)
                tempUserConnectionList[0] = tempUserConnection
                viewModel.isUpdatingUserPackage = true
                viewModel.userPackageList.postValue(tempUserConnectionList)
            }

            override fun onItemUnChecked(packageService: PackageService, position: Int) {
                var temp = viewModel.packageCounter.get()
                viewModel.packageCounter.set(--temp)

                val tempUserConnectionList = viewModel.userPackageList.value
                val tempUserConnection = tempUserConnectionList?.get(0)!!

                val removePosition = viewModel.userPurchasedPackageIdsMap[packageService.packServiceId]
                removePosition?.let {
                    viewModel.userPurchasedPackageIdsMap.remove(packageService.packServiceId)
                    tempUserConnection.packServiceList?.removeAt(it)
                    tempUserConnectionList[0] = tempUserConnection
                    viewModel.isUpdatingUserPackage = true
                    viewModel.userPackageList.postValue(tempUserConnectionList)
                }
            }
        })
        serviceAdapter = ServiceAdapter(serviceList, object: ServiceAdapter.OnItemSelectListener {
            override fun onItemChecked(packageService: PackageService, position: Int) {
                var temp = viewModel.serviceCounter.get()
                viewModel.serviceCounter.set(++temp)

                val tempUserConnectionList = viewModel.userPackageList.value
                val tempUserConnection = tempUserConnectionList?.get(0)!!
                val newPackService = PackService(0, tempUserConnection.connectionNo,
                    tempUserConnection.connectionTypeId, tempUserConnection.ispUserId,
                    tempUserConnection.parentUserId, tempUserConnection.accountId,
                    packageService.packId, packageService.packServiceId,
                    packageService.packServiceName, packageService.packServiceTypeId,
                    tempUserConnection.zoneId, packageService.packServiceType,
                    packageService.packServicePrice, packServiceInstallCharge = 0,
                    packServiceOthersCharge = 0, isDefault = false,
                    expireDate = "", activeDate = "",
                    isNew = true, isUpdate = false, isDelete = false)

                tempUserConnection.packServiceList?.add(newPackService)
                tempUserConnectionList[0] = tempUserConnection
                viewModel.isUpdatingUserPackage = true
                viewModel.userPackageList.postValue(tempUserConnectionList)
            }

            override fun onItemUnChecked(packageService: PackageService, position: Int) {
                var temp = viewModel.serviceCounter.get()
                viewModel.serviceCounter.set(--temp)

                val tempUserConnectionList = viewModel.userPackageList.value
                val tempUserConnection = tempUserConnectionList?.get(0)!!

                val removePosition = viewModel.userPurchasedPackageIdsMap[packageService.packServiceId]
                removePosition?.let {
                    viewModel.userPurchasedPackageIdsMap.remove(packageService.packServiceId)
                    tempUserConnection.packServiceList?.removeAt(it)
                    tempUserConnectionList[0] = tempUserConnection
                    viewModel.isUpdatingUserPackage = true
                    viewModel.userPackageList.postValue(tempUserConnectionList)
                }
            }
        })
        adapter = UserPackageServiceAdapter(packServiceList)

        binding.packRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.packRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 15))
        binding.packRecycler.adapter = packageAdapter

        binding.serviceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.serviceRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 15))
        binding.serviceRecycler.adapter = serviceAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.userPackServiceRecycler.layoutManager = layoutManager
        binding.userPackServiceRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 15))
        binding.userPackServiceRecycler.adapter = adapter

        viewModel.getUserPackage()

        binding.save.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.packageList.observe(viewLifecycleOwner, Observer { packList ->
            packList?.let {
                packageList.clear()
                var i = 0
                while (i < it.size) {
                    if (viewModel.userPurchasedPackageIds.contains(it[i].packServiceId)) {
                        it[i].isPurchased = true
                        var temp = viewModel.packageCounter.get()
                        viewModel.packageCounter.set(++temp)
                    }
                    i++
                }
                packageList.addAll(it)
                packageAdapter.notifyDataSetChanged()
            }
        })

        viewModel.serviceList.observe(viewLifecycleOwner, Observer { servList ->
            servList?.let {
                serviceList.clear()
                var i = 0
                while (i < it.size) {
                    if (viewModel.userPurchasedPackageIds.contains(it[i].packServiceId)) {
                        it[i].isPurchased = true
                        var temp = viewModel.serviceCounter.get()
                        viewModel.serviceCounter.set(++temp)
                    }
                    i++
                }
                serviceList.addAll(it)
                serviceAdapter.notifyDataSetChanged()
            }
        })

        viewModel.userPackageList.observe(viewLifecycleOwner, Observer { userConnectionList ->
            val temp = userConnectionList[0].packServiceList
            temp?.let {
                packServiceList.clear()
                packServiceList.addAll(it)
                adapter.notifyDataSetChanged()
                val purchasedPackageIds: ArrayList<Int> = ArrayList()
                val purchasedPackageIdsMap: HashMap<Int, Int> = HashMap()
                var i = 0
                while (i < it.size) {
                    it[i].packServiceId?.let { packServiceId ->
                        //if (it[i].is)
                        purchasedPackageIds.add(packServiceId)
                        purchasedPackageIdsMap[packServiceId] = i
                    }
                    i++
                }
                viewModel.userPurchasedPackageIds = purchasedPackageIds
                viewModel.userPurchasedPackageIdsMap = purchasedPackageIdsMap

                if (!viewModel.isUpdatingUserPackage) {
                    viewModel.getPackageService()
                }
            }
        })
    }

}
