package ltd.royalgreen.pacenet.support


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.SupportTicketConversationFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SupportTicketConversationFragment : Fragment(), Injectable {

    private val storagePermissionCode = 1207

    private val fileChooserCode = 1208

    private val fileUriList = ArrayList<Uri>()

    private lateinit var fileAdapter: ConversationFileUriAdapter

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

        fileAdapter = ConversationFileUriAdapter(fileUriList, object : ConversationFileUriAdapter.FileDeleteCallback {
            override fun onFileDeleted(position: Int) {
                viewModel.backupFileUri = fileUriList[position]
                fileUriList.removeAt(position)
                fileAdapter.notifyItemRemoved(position)
                viewModel.fileUriList = fileUriList

                val snackBar = Snackbar.make(binding.mainLayout, "Undo deleted file?", Snackbar.LENGTH_LONG)
                snackBar.setAction("UNDO") {
                    // undo is selected, restore the deleted item
                    viewModel.backupFileUri?.let {
                        val backup = it
                        viewModel.backupFileUri = null
                        fileUriList.add(position, backup)
                        fileAdapter.notifyItemInserted(position)
                        viewModel.fileUriList = fileUriList
                    }
                }
                snackBar.setActionTextColor(Color.YELLOW)
                snackBar.show()
            }
        })

        fileUriList.clear()
        fileUriList.addAll(viewModel.fileUriList)
        fileAdapter.notifyDataSetChanged()

        binding.attachedFileRecycler.layoutManager = LinearLayoutManager(requireActivity())
        binding.attachedFileRecycler.addItemDecoration(VerticalSpaceItemDivider(10))
        binding.attachedFileRecycler.itemAnimator = DefaultItemAnimator()
        binding.attachedFileRecycler.adapter = fileAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messageRecycler.layoutManager = layoutManager
        binding.messageRecycler.addItemDecoration(VerticalSpaceItemDivider(16))
        binding.messageRecycler.adapter = messageAdapter

        viewModel.getTicketConversation(args.ispTicketId)

        viewModel.messageList.observe(viewLifecycleOwner, Observer {
            viewModel.newMessage.postValue("")
            messageList.clear()
            messageList.addAll(it)
            messageAdapter.notifyDataSetChanged()
        })

        viewModel.newMessage.observe(viewLifecycleOwner, Observer {
            binding.send.isEnabled = !it.isNullOrBlank()
        })

        binding.attachFile.setOnClickListener {
            if (viewModel.fileUriList.size < 1) {
                if (PermissionUtils.checkExternalStoragePermission(requireContext())) {
                    showFileChooser()
                } else {
                    PermissionUtils.requestExternalStoragePermission(requireContext(), requireActivity(), parentFragmentManager, storagePermissionCode)
                }
            } else {
                showErrorToast(requireContext(), "You can not attach more than one file")
            }
        }

        binding.send.setOnClickListener {
            viewModel.entryNewComment(args.ispTicketId.toString()).observe(viewLifecycleOwner, Observer {
                if (it.resdata.resstate == true) {
                    viewModel.getTicketConversation(args.ispTicketId)
                    viewModel.fileUriList.clear()
                    fileUriList.clear()
                    fileAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            storagePermissionCode -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser()
                } else {
                    showErrorToast(requireContext(), "Permission must be accepted to process!")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showFileChooser() {
        // Use the GET_CONTENT intent from the utility class
        val targetChooser = FileChooser.createGetContentIntent()
        // Create the chooser Intent
        val intent = Intent.createChooser(targetChooser, "Choose File From")
        try {
            startActivityForResult(intent, fileChooserCode)
        } catch (exception: ActivityNotFoundException) {
            exception.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            fileChooserCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        // Get the URI of the selected file
                        val fileUri: Uri? = it.data
                        fileUri?.let { uri ->
                            fileUriList.add(uri)
                            fileAdapter.notifyDataSetChanged()
                            viewModel.fileUriList = fileUriList
                        }
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

