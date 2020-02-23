package ltd.royalgreen.pacenet.support


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.SupportTicketEntryBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.*
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

    private val fileUriList = ArrayList<Uri>()

    private lateinit var fileAdapter: FileUriAdapter

    private val storagePermissionCode = 1207

    private val fileChooserCode = 1207

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
        binding.viewModel = viewModel

        fileAdapter = FileUriAdapter(fileUriList, object : FileUriAdapter.FileDeleteCallback {
            override fun onFileDeleted(position: Int) {
                viewModel.backupFileUri = viewModel.fileUriList.value?.get(position)!!
                viewModel.fileUriList.value?.removeAt(position)
                fileAdapter.notifyItemRemoved(position)

                val snackBar = Snackbar.make(binding.mainLayout, "Undo deleted file?", Snackbar.LENGTH_LONG)
                snackBar.setAction("UNDO") {
                    // undo is selected, restore the deleted item
                    viewModel.fileUriList.value?.add(position, viewModel.backupFileUri)
                    fileAdapter.notifyItemInserted(position)
                }
                snackBar.setActionTextColor(Color.YELLOW)
                snackBar.show()
            }
        })

        binding.attachedFileRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.attachedFileRecycler.itemAnimator = DefaultItemAnimator()
        binding.attachedFileRecycler.adapter = fileAdapter

        ticketCategoryAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleTicketCategoryList)
        ticketCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerticketCategory.adapter = ticketCategoryAdapter

        binding.spinnerticketCategory.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position != 0) {
                    try {
                        viewModel.selectedTicketCategory.value = ticketCategoryList[position - 1]
                    } catch (e: IndexOutOfBoundsException) {

                    }
                } else {
                    viewModel.selectedTicketCategory.value = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.ticketSubject.observe(viewLifecycleOwner, Observer {
            binding.submit.isEnabled = !it.isNullOrEmpty() && !viewModel.ticketDescription.value.isNullOrEmpty() && viewModel.selectedTicketCategory.value != null
            if (it.isNullOrEmpty()) {
                binding.subjectInputLayout.isHelperTextEnabled = true
                binding.subjectInputLayout.helperText = "Required"
            } else {
                binding.subjectInputLayout.isHelperTextEnabled = false
                binding.subjectInputLayout.helperText = ""
            }
        })

        viewModel.ticketDescription.observe(viewLifecycleOwner, Observer {
            binding.submit.isEnabled = !it.isNullOrEmpty() && !viewModel.ticketSubject.value.isNullOrEmpty() && viewModel.selectedTicketCategory.value != null
            if (it.isNullOrEmpty()) {
                binding.descInputLayout.isHelperTextEnabled = true
                binding.descInputLayout.helperText = "Required"
            } else {
                binding.descInputLayout.isHelperTextEnabled = false
                binding.descInputLayout.helperText = ""
            }
        })

        viewModel.selectedTicketCategory.observe(viewLifecycleOwner, Observer {
            binding.submit.isEnabled = !viewModel.ticketSubject.value.isNullOrEmpty() && !viewModel.ticketDescription.value.isNullOrEmpty() && it != null
        })

        binding.submit.setOnClickListener {
            viewModel.entryNewTicket().observe(viewLifecycleOwner, Observer {
                if (it != null && it.resdata.resstate == true) {
                    showSuccessToast(requireContext(), it.resdata.message)
                    findNavController().popBackStack()
                } else {
                    showErrorToast(requireContext(), it.resdata.message)
                }
            })
        }

        binding.reset.setOnClickListener {
            viewModel.ticketSubject.value = ""
            viewModel.ticketDescription.value = ""
            binding.spinnerticketCategory.setSelection(0, true)
        }

        binding.addFile.setOnClickListener {
            if (PermissionUtils.checkExternalStoragePermission(requireContext())) {
                showFileChooser()
            } else {
                PermissionUtils.requestExternalStoragePermission(requireContext(), requireActivity(), parentFragmentManager, storagePermissionCode)
            }
        }

        viewModel.fileUriList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                fileUriList.clear()
                fileUriList.addAll(it)
                fileAdapter.notifyDataSetChanged()
            }
        })

        loadTicketCategoryList()
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
                            viewModel.fileUriList.value?.add(uri)
                        }
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun loadTicketCategoryList() {
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
