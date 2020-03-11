package ltd.royalgreen.pacenet.billing.bkash

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonParser
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BillingBkashWebDialogBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

class BKashPaymentWebDialog internal constructor(private val callBack: BkashPaymentCallback, private val createBkash: CreateBkashModel, private val paymentRequest: PaymentRequest): DialogFragment(),
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var request = ""

    private val viewModel: BKashPaymentViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingBkashWebDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

//    override fun onResume() {
//        super.onResume()
//        val params = dialog?.window?.attributes
//        params?.width = WindowManager.LayoutParams.MATCH_PARENT
//        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog?.window?.attributes = params
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
//        return dialog
//    }

    override fun getTheme(): Int {
        return R.style.DialogFullScreenTheme
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.billing_bkash_web_dialog,
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

        request = Gson().toJson(paymentRequest)

        viewModel.resBkash.observe(viewLifecycleOwner, Observer {
            val errorCode: String? = null
            val errorMessage: String? = null
            if (!it.isNullOrBlank()) {
                val jsonObject = JsonParser.parseString(it).asJsonObject.apply {
                    addProperty("errorCode", errorCode)
                    addProperty("errorMessage", errorMessage)
                }
                viewModel.bkashPaymentExecuteJson = jsonObject
                val jsonString = jsonObject.toString()
                binding.mWebView.loadUrl("javascript:createBkashPayment($jsonString )")
            } else {
                callBack.onPaymentError()
            }
        })

        viewModel.bKashPaymentStatus.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                binding.mWebView.evaluateJavascript("javascript:finishBkashPayment()", null)
            } else {
                showErrorToast(requireContext(), it.second)
                callBack.onPaymentError()
                dismiss()
            }
        })

        binding.goBack.setOnClickListener {
            if (binding.mWebView.canGoBack()) {
                binding.mWebView.goBack()
            } else {
                callBack.onPaymentCancelled()
                dismiss()
            }
        }

        val webSettings: WebSettings = binding.mWebView.settings
        webSettings.javaScriptEnabled = true

        //Below part is for enabling webview settings for using javascript and accessing html files and other assets

        binding.mWebView.isClickable = true
        binding.mWebView.settings.domStorageEnabled = true
        binding.mWebView.settings.setAppCacheEnabled(true)
        binding.mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.mWebView.clearCache(false)
        binding.mWebView.settings.allowFileAccessFromFileURLs = true
        binding.mWebView.settings.allowUniversalAccessFromFileURLs = true

        //To control any kind of interaction from html file

//        mWebView.addJavascriptInterface( JavaScriptInterface(requireContext()), "AndroidNative")

        binding.mWebView.addJavascriptInterface( JavaScriptWebViewInterface(requireContext()), "AndroidNative")

        binding.mWebView.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                handler.proceed()
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                if (binding.loader != null) {
                    binding.loader.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                val paymentRequestJson = "{paymentRequest:$request}"
                binding.mWebView.loadUrl("javascript:callReconfigure($paymentRequestJson )")
                binding.mWebView.loadUrl("javascript:clickPayButton()")
                if (binding.loader != null) {
                    binding.loader.visibility = View.GONE
                }
            }
        }

        binding.mWebView.loadUrl("file:///android_asset/www/checkout_120.html")
    }

    inner class JavaScriptWebViewInterface(context: Context) {
        var mContext: Context = context

        // Handle event from the web page
        @JavascriptInterface
        fun createPayment() {
            viewModel.createBkashCheckout(paymentRequest, createBkash)
            viewModel.bkashToken = createBkash.authToken
        }

        @JavascriptInterface
        fun executePayment() {
            viewModel.executeBkashPayment()
        }

        @JavascriptInterface
        fun finishBkashPayment() {
            showSuccessToast(requireContext(), viewModel.bKashPaymentStatus.value?.second ?: "UNKNOWN Message!")
            callBack.onPaymentSuccess()
        }

    }

    interface BkashPaymentCallback {
        fun onPaymentSuccess()
        fun onPaymentError()
        fun onPaymentCancelled()
    }
}