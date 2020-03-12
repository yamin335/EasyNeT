package ltd.royalgreen.pacenet

import android.os.Bundle
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
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.PackageAddFragmentBinding
import ltd.royalgreen.pacenet.profile.PackService
import ltd.royalgreen.pacenet.profile.PackServiceListAdapter
import ltd.royalgreen.pacenet.profile.PackageAddViewModel
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import javax.inject.Inject

class PackageAddFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: PackageAddViewModel by viewModels {
        viewModelFactory
    }

    private var binding by autoCleared<PackageAddFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var adapter: PackServiceListAdapter
    private val packServiceList = ArrayList<PackService>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.package_add_fragment,
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

        adapter = PackServiceListAdapter(packServiceList)

        binding.packServiceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.packServiceRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 15))
        binding.packServiceRecycler.adapter = adapter

        binding.save.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.userPackageList.observe(viewLifecycleOwner, Observer { userConnectionList ->
            val temp = userConnectionList[0].packServiceList
            temp?.let {
                packServiceList.clear()
                packServiceList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.getUserPackage()
    }


}
