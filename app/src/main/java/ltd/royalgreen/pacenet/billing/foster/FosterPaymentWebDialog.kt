package ltd.royalgreen.pacenet.billing.foster

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BillingFosterWebDialogBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

class FosterPaymentWebDialog internal constructor(private val callBack: FosterPaymentCallback, private val fosterProcessUrl: String, private val fosterPaymentStatusUrl: String): DialogFragment(),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: FosterPaymentViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingFosterWebDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun getTheme(): Int {
        return R.style.DialogFullScreenTheme
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.billing_foster_web_dialog,
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

        binding.goBack.setOnClickListener {
            if (binding.mWebView.canGoBack()) {
                binding.mWebView.goBack()
            } else {
                callBack.onFosterPaymentCancelled()
                dismiss()
            }
        }

        viewModel.rechargeSuccessFailureStatus.observe(viewLifecycleOwner, Observer { status ->
            if (status) {
                showSuccessToast(requireContext(), "Payment Successful")
                callBack.onFosterPaymentSuccess()
            } else {
                showErrorToast(requireContext(), "Payment not successful !")
                callBack.onFosterPaymentError()
            }
        })

        viewModel.showMessage.observe(viewLifecycleOwner, Observer { (type, message) ->
            if (type == "SUCCESS") {
                showSuccessToast(requireContext(), message)
//                viewModel.showMessage.postValue(Pair("null", ""))
            } else if (type == "ERROR") {
                showErrorToast(requireContext(), message)
//                viewModel.showMessage.postValue(Pair("null", ""))
            }
        })

        val webSettings: WebSettings = binding.mWebView.settings
        webSettings.javaScriptEnabled = true

        binding.mWebView.isClickable = true
        binding.mWebView.settings.domStorageEnabled = true
        binding.mWebView.settings.setAppCacheEnabled(true)
        binding.mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.mWebView.clearCache(false)
        binding.mWebView.settings.allowFileAccessFromFileURLs = true
        binding.mWebView.settings.allowUniversalAccessFromFileURLs = true

        binding.mWebView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                val url = request?.url
                val status = url?.fragment?.split("?")
                val host = "pacenet.net"
                status?.let {
                    if (host == url.host) {
                        val paymentStatus = it[1].split("=")
                        if (paymentStatus[0] == "paymentStatus" && paymentStatus[1] == "true") {
                            viewModel.checkFosterPaymentStatus(fosterPaymentStatusUrl)
                        } else {
                            viewModel.showMessage.postValue(Pair("ERROR", "Payment not successful !"))
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                try {
                    if (binding.loader != null) {
                        binding.loader.visibility = View.VISIBLE
                    }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                try {
                    if (binding.loader != null) {
                        binding.loader.visibility = View.GONE
                    }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }

        binding.mWebView.loadUrl(fosterProcessUrl)
    }

    interface FosterPaymentCallback {
        fun onFosterPaymentSuccess()
        fun onFosterPaymentError()
        fun onFosterPaymentCancelled()
    }
}