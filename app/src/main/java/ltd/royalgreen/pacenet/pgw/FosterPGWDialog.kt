package ltd.royalgreen.pacenet.pgw

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
import ltd.royalgreen.pacenet.databinding.FosterPgwDialogBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

class FosterPGWDialog internal constructor(
    private val callBack: FosterPaymentCallback,
    private val fosterProcessUrl: String,
    private val fosterPaymentStatusUrl: String): DialogFragment(),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: FosterPGWViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<FosterPgwDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun getTheme(): Int {
        return R.style.DialogFullScreenTheme
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.foster_pgw_dialog,
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
                viewModel.showMessage.postValue(Pair(false, "Payment cancelled!"))
            }
        }

        viewModel.showMessage.observe(viewLifecycleOwner, Observer { (isSuccess, message) ->
            if (isSuccess) {
                showSuccessToast(requireContext(), message)
                callBack.onFosterPaymentFinished(viewModel.fosterResponse.value)
            } else {
                showErrorToast(requireContext(), message)
                callBack.onFosterPaymentFinished(null)
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
                            viewModel.showMessage.postValue(Pair(false, "Payment not successful, please try again later!"))
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
        fun onFosterPaymentFinished(fosterResponse: String?)
    }
}