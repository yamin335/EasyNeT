package ltd.royalgreen.pacenet.login

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.ForgotPasswordDialogBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.hideKeyboard
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

class ForgotPasswordDialog : DialogFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: ForgotPassDialogViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(ForgotPassDialogViewModel::class.java)
    }

    private var binding by autoCleared<ForgotPasswordDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var isOldPasswordValid = false

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.forgot_password_dialog,
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

        binding.passwordLayout.isEndIconVisible = false
        binding.confPasswordLayout.isEndIconVisible = false

        val oldPassword = preferences.getString("LoggedUserPassword", null)

        fun enableOrDisableSaveButton() {
            val newPassword = viewModel.newPassword.value ?: ""
            binding.save.isEnabled = isOldPasswordValid == true &&
                    !viewModel.newPassword.value.isNullOrBlank() &&
                    !viewModel.confirmPassword.value.isNullOrBlank() &&
                    viewModel.newPassword.value == viewModel.confirmPassword.value &&
                    viewModel.newPassword.value != viewModel.oldPassword.value &&
                    newPassword.length >= 5 && newPassword.length <= 24
        }

        viewModel.errorToast.observe(this, Observer {
            showErrorToast(requireContext(), it)
            dismiss()
        })

        viewModel.successToast.observe(this, Observer {
            showSuccessToast(requireContext(), it)
            dismiss()
        })

        viewModel.oldPassword.observe(this, Observer {

            if (it.isNullOrBlank() || oldPassword.isNullOrBlank()) {
                isOldPasswordValid = false
                binding.oldPasswordLayout.isErrorEnabled = false
            } else {
                if (it == oldPassword) {
                    isOldPasswordValid = true
                    binding.oldPasswordLayout.isErrorEnabled = false
                } else {
                    isOldPasswordValid = false
                    binding.oldPasswordLayout.isErrorEnabled = true
                    binding.oldPasswordLayout.error = "Incorrect Old Password!"
                }
            }

            enableOrDisableSaveButton()
        })

        viewModel.newPassword.observe(this, Observer {
            enableOrDisableSaveButton()

            binding.passwordLayout.isErrorEnabled = !it.isNullOrBlank() && !viewModel.oldPassword.value.isNullOrBlank()
                    && (it == viewModel.oldPassword.value || it.length < 5 || it.length > 24)

            if (!it.isNullOrBlank() && !viewModel.oldPassword.value.isNullOrBlank()) {
                if (it == viewModel.oldPassword.value) {
                    binding.passwordLayout.error = "Same as old password"
                } else if (it.length < 5 || it.length > 24) {
                    binding.passwordLayout.error = "Password length must range from 5 to 24"
                }
            }

            binding.confPasswordLayout.isErrorEnabled = !it.isNullOrBlank() && !viewModel.confirmPassword.value.isNullOrBlank()
                    && it != viewModel.confirmPassword.value

            if (!it.isNullOrBlank() && !viewModel.confirmPassword.value.isNullOrBlank() && it != viewModel.confirmPassword.value) {
                binding.confPasswordLayout.error = "Password doesn't match"
            }

            binding.passwordLayout.isEndIconVisible = !it.isNullOrEmpty()

        })

        viewModel.confirmPassword.observe(this, Observer {
            enableOrDisableSaveButton()

            binding.confPasswordLayout.isErrorEnabled = !it.isNullOrBlank() &&
                    !viewModel.newPassword.value.isNullOrBlank() && it != viewModel.newPassword.value

            if (!it.isNullOrBlank() && !viewModel.newPassword.value.isNullOrBlank() && it != viewModel.newPassword.value){
                binding.confPasswordLayout.error = "Password doesn't match"
            }

            binding.confPasswordLayout.isEndIconVisible = !it.isNullOrBlank()
        })

        binding.save.setOnClickListener {
            hideKeyboard()
            viewModel.processChangePassword()
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }
    }
}