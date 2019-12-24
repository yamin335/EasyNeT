package ltd.royalgreen.pacenet.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.MainActivity
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.LoginFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.hideKeyboard
import javax.inject.Inject

class LoginFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private var binding by autoCleared<LoginFragmentBinding>()

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel: LoginViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val exitDialog = CustomAlertDialog(object :
                CustomAlertDialog.YesCallback {
                override fun onYes() {

                    preferences.edit().apply {
                        putString("LoggedUserPassword",null)
                        apply()
                    }

                    preferences.edit().apply {
                        putString("LoggedUser", null)
                        apply()
                    }

                    preferences.edit().apply {
                        putBoolean("goToLogin", false)
                        apply()
                    }
                    requireActivity().finish()
                }
            }, "Do you want to exit?", "")
            exitDialog.show(parentFragmentManager, "#app_exit_dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Hide the status bar.
        val uiOptions = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        requireActivity().window.decorView.systemUiVisibility = uiOptions

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.login_fragment,
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

        binding.passwordInputLayout.isEndIconVisible = false

        binding.username.setOnFocusChangeListener { _, _ ->
            viewModel.errorMessage.value = false
        }

        binding.password.setOnFocusChangeListener { _, _ ->
            viewModel.errorMessage.value = false
        }

        binding.loginButton.setOnClickListener {
            hideKeyboard()
            viewModel.errorMessage.value = false
            viewModel.processSignIn()
        }

        binding.forgotPassword.setOnClickListener {
//            val forgotPasswordDialog = ForgotPasswordDialog()
//            forgotPasswordDialog.isCancelable = true
//            forgotPasswordDialog.show(parentFragmentManager, "#sign_up_dialog")
        }

        binding.signUp.setOnClickListener {
//            val signUpDialog = SignUpDialog(object : SignUpDialog.SignUpCallback {
//                override fun onSignUp(newUser: JsonObject) {
//                    val parent: ViewGroup? = null
//                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
//                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
//                    toastView.message.text = requireContext().getString(R.string.loading_msg)
//                    toast.view = toastView
//                    toast.show()
//                    viewModel.processSignUp(newUser)
//                }
//            })
//            signUpDialog.isCancelable = true
//            signUpDialog.show(parentFragmentManager, "#sign_up_dialog")
        }

        binding.faq.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFragment_to_faqsFragment)
        }

        binding.privacy.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFragment_to_privacyFragment)
        }

        binding.contact.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFragment_to_contactFragment)
        }

        viewModel.userName.observe(this, Observer {
            viewModel.errorMessage.value = false
            binding.loginButton.isEnabled = !it.isNullOrEmpty() && !viewModel.password.value.isNullOrEmpty()

        })

        viewModel.password.observe(this, Observer {
            viewModel.errorMessage.value = false
            binding.loginButton.isEnabled = !it.isNullOrEmpty() && !viewModel.userName.value.isNullOrEmpty()
            binding.passwordInputLayout.isEndIconVisible = !it.isNullOrEmpty()
        })

        viewModel.signUpMsg.observe(this, Observer {
//            val parent: ViewGroup? = null
//            if (it == "Save successfully.") {
//                val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
//                val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                val toastView = inflater.inflate(R.layout.toast_custom_green, parent)
//                toastView.message.text = requireContext().getString(R.string.acc_successful)
//                toast.view = toastView
//                toast.show()
//            } else {
//                val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
//                val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
//                toastView.message.text = it
//                toast.view = toastView
//                toast.show()
//            }
        })

        viewModel.apiResult.observe(this, Observer { loginResponse->
            if (loginResponse?.resdata?.loggeduser != null) {
                val handler = CoroutineExceptionHandler { _, exception ->
                    exception.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch(handler) {
                    val loggedUserSerialized = Gson().toJson(loginResponse.resdata.loggeduser)

                    preferences.edit().apply {
                        putString("LoggedUserPassword", viewModel.password.value)
                        apply()
                    }

                    preferences.edit().apply {
                        putString("LoggedUser", loggedUserSerialized)
                        apply()
                    }
                }
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                viewModel.errorMessage.value = true
            }
        })
    }
}
