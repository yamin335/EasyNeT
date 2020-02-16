package ltd.royalgreen.pacenet.support


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.SupportFragmentBinding
import ltd.royalgreen.pacenet.databinding.SupportTicketEntryBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TicketEntryFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TicketEntryViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<SupportTicketEntryBinding>()

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val ticketCategoryList = ArrayList<TicketCategory>()
    private var titleTicketCategoryList = arrayOf("--Select Category--")

    lateinit var ticketCategoryAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.support_ticket_entry,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        ticketCategoryAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleTicketCategoryList)
        ticketCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerticketCategory.adapter = ticketCategoryAdapter

        viewModel.getTicketCategory().observe(viewLifecycleOwner, Observer {
            val temp = Array(it.size + 1){""}
            temp[0] = "--Select Category--"
            ticketCategoryList.clear()
            ticketCategoryList.addAll(it)

            it.forEachIndexed { index, ticketCategory ->
                temp[index + 1] = ticketCategory.ticketCategory ?: "Unknown"
            }

            titleTicketCategoryList = temp
            ticketCategoryAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleTicketCategoryList)
            ticketCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerticketCategory.adapter = ticketCategoryAdapter
        })
    }
}
