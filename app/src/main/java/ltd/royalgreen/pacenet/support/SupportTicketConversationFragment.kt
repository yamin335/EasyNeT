package ltd.royalgreen.pacenet.support


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.SupportTicketConversationFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import ltd.royalgreen.pacenet.util.autoCleared
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SupportTicketConversationFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ConversationDetailViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<SupportTicketConversationFragmentBinding>()

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val args: SupportTicketConversationFragmentArgs by navArgs()

    private val messageList = ArrayList<TicketConversation>()

    private val messageAdapter = ConversationDetailsAdapter(messageList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.support_ticket_conversation_fragment,
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

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messageRecycler.layoutManager = layoutManager
        binding.messageRecycler.adapter = messageAdapter

        viewModel.getTicketConversation(args.ispTicketId)

        viewModel.messageList.observe(viewLifecycleOwner, Observer {
            viewModel.newMessage.postValue("")
            messageList.clear()
            messageList.addAll(it)
            messageAdapter.notifyDataSetChanged()
        })

        viewModel.newMessage.observe(viewLifecycleOwner, Observer {
            binding.submit.isEnabled = !it.isNullOrBlank()
        })

        binding.submit.setOnClickListener {
            viewModel.entryNewComment(args.ispTicketId.toString()).observe(viewLifecycleOwner, Observer {
                viewModel.getTicketConversation(args.ispTicketId)
            })
        }
    }
}

