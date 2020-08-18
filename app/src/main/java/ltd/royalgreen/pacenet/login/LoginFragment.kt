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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.MainActivity
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.SplashActivity
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.LoginFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.hideKeyboard
import ltd.royalgreen.pacenet.util.showErrorToast
import javax.inject.Inject

class LoginFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private var binding by autoCleared<LoginFragmentBinding>()

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel: LoginViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var windowConfig: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val exitDialog = CustomAlertDialog(object :
                CustomAlertDialog.YesCallback {
                override fun onYes() {
                    viewModel.onAppExit(preferences)
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

    override fun onDestroyView() {
        super.onDestroyView()

//        if (windowConfig != null) {
//            requireActivity().window.decorView.systemUiVisibility = windowConfig!!
//        }

        //For updating status bar color
//        val activity = requireActivity() as SplashActivity
//        activity.updateStatusBarBackgroundColor("#ffffff")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Dark the status bar text.
        //val uiOptions = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //requireActivity().window.decorView.systemUiVisibility = uiOptions

        //For updating status bar color
//        val activity = requireActivity() as SplashActivity
//        activity.updateStatusBarBackgroundColor("#4AAE34")

        // Hide the status bar.
        windowConfig = requireActivity().window.decorView.systemUiVisibility
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

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

        binding.privacy.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_privacyPolicyFragment)
        }

        //binding.contact.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.contactFragment, null))

        binding.contact.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_contactFragment)
        }

        viewModel.errorToast.observe(viewLifecycleOwner, Observer {
            showErrorToast(requireContext(), it)
        })

        viewModel.userName.observe(viewLifecycleOwner, Observer {
            viewModel.errorMessage.value = false
            binding.loginButton.isEnabled = !it.isNullOrEmpty() && !viewModel.password.value.isNullOrEmpty()

        })

        viewModel.password.observe(viewLifecycleOwner, Observer {
            viewModel.errorMessage.value = false
            binding.loginButton.isEnabled = !it.isNullOrEmpty() && !viewModel.userName.value.isNullOrEmpty()
            binding.passwordInputLayout.isEndIconVisible = !it.isNullOrEmpty()
        })

        viewModel.signUpMsg.observe(viewLifecycleOwner, Observer {
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

        viewModel.apiResult.observe(viewLifecycleOwner, Observer { loginResponse->
            if (loginResponse?.resdata?.loggeduser != null) {
                lifecycleScope.launch {
                    val loggedUserSerialized = Gson().toJson(loginResponse.resdata.loggeduser)

                    preferences.edit().apply {
                        putString("LoggedUserPassword", viewModel.password.value)
                        apply()
                    }

                    preferences.edit().apply {
                        putString("LoggedUserID", loggedUserSerialized)
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
