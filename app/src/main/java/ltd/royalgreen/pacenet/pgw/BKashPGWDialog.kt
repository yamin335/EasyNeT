package ltd.royalgreen.pacenet.pgw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.android.support.DaggerDialogFragment
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.billing.BillPaymentHelper
import ltd.royalgreen.pacenet.billing.bkash.CreateBkashModel
import ltd.royalgreen.pacenet.billing.bkash.PaymentRequest
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BkashPgwDialogBinding
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showErrorToast
import ltd.royalgreen.pacenet.util.showSuccessToast
import javax.inject.Inject

class BKashPGWDialog internal constructor(
    private val callBack: BkashPaymentCallback,
    private val createBkash: CreateBkashModel,
    private val paymentRequest: PaymentRequest,
    private val billPaymentHelper: BillPaymentHelper
): DaggerDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var request = ""

    private val viewModel: BKashPGWViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BkashPgwDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun getTheme(): Int {
        return R.style.DialogFullScreenTheme
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bkash_pgw_dialog,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        request = Gson().toJson(paymentRequest)

        viewModel.dismissListener.observe(viewLifecycleOwner, Observer { (isSuccessful, message) ->
            if (isSuccessful) {
                showSuccessToast(requireContext(), message)
                callBack.onBkashPaymentFinished(viewModel.resExecuteBkash.value)
            } else {
                showErrorToast(requireContext(), message)
                callBack.onBkashPaymentFinished(null)
            }
            binding.mWebView.evaluateJavascript("javascript:finishBkashPayment()") {
                Log.d("JavaScriptReturnValue:", it)
            }
        })

        viewModel.resBkash.observe(viewLifecycleOwner, Observer {
            val errorCode: String? = null
            val errorMessage: String? = null
            val jsonObject = JsonParser.parseString(it).asJsonObject.apply {
                addProperty("errorCode", errorCode)
                addProperty("errorMessage", errorMessage)
            }
            viewModel.bkashPaymentExecuteJson = jsonObject
            val jsonString = jsonObject.toString()
            binding.mWebView.loadUrl("javascript:createBkashPayment($jsonString )")
        })

        binding.goBack.setOnClickListener {
            if (binding.mWebView.canGoBack()) {
                binding.mWebView.goBack()
            } else {
                showErrorToast(requireContext(), "Payment cancelled!")
                callBack.onBkashPaymentFinished(null)
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

        binding.mWebView.addJavascriptInterface( JavaScriptWebViewInterface(requireContext()), "AndroidNative")

        binding.mWebView.webViewClient = object : WebViewClient() {

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
            viewModel.createBkashCheckout(paymentRequest, createBkash, billPaymentHelper)
            viewModel.bkashToken = createBkash.authToken
        }

        @JavascriptInterface
        fun executePayment() {
            viewModel.executeBkashPayment()
        }

    }

    interface BkashPaymentCallback {
        fun onBkashPaymentFinished(bkashResponse: String?)
    }
}