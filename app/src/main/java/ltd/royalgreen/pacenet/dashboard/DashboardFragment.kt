package ltd.royalgreen.pacenet.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.appbar.MaterialToolbar
import ltd.royalgreen.pacenet.*
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.DashboardFragmentBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.showChangePasswordDialog
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : MainNavigationFragment(), Injectable {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: DashboardViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<DashboardFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val xAxisLabels = ArrayList<String>()

    private var listener: DashItemInteractionListener? = null

    private var mainActivityCallback: MainActivityCallback? = null

    interface DashItemInteractionListener {
        fun onDashItemClicked(navigation: String)
        fun setupToolbar(toolbar: MaterialToolbar)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashItemInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement DashItemInteractionListener")
        }

        if (context is MainActivityCallback) {
            mainActivityCallback = context
        } else {
            throw RuntimeException("$context must implement MainActivityCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        mainActivityCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            mainActivityCallback?.onAppExit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dashboard_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        //listener?.setupToolbar(binding.toolbar)

        val formatter: ValueFormatter =
            object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    if (value.toInt() in xAxisLabels.indices) {
                        return xAxisLabels[value.toInt()]
                    }
                    return ""
                }
            }
        val xAxis: XAxis = binding.accountStatusChart.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatter

        val description = Description()
        description.text = "Data Traffic"
        binding.accountStatusChart.description = description

        observeDashSessionChartData()

        binding.myAccount.setOnClickListener {
            listener?.onDashItemClicked("PROFILE")
        }

        binding.payHistory.setOnClickListener {
            listener?.onDashItemClicked("PAY_HISTORY")
        }

        binding.ticketHistory.setOnClickListener {
            listener?.onDashItemClicked("TICKET_HISTORY")
        }

        binding.change.setOnClickListener {
            val sessionChartChangeDialog = DashSessionChartChangeDialog(object : DashSessionChartChangeDialog.SessionChartChangeCallback {
                override fun onSessionChartChanged(selectedType: String, selectedMonth: Int) {
                    viewModel.getSessionChartData(selectedMonth, selectedType)
                }

            }, viewModel.selectedType.value ?: "daily", viewModel.selectedMonth.value ?: 1)
            sessionChartChangeDialog.isCancelable = true
            sessionChartChangeDialog.show(childFragmentManager, "#session_change_dialog")
        }

        viewModel.getSessionChartData(viewModel.selectedMonth.value ?: 1, viewModel.selectedType.value ?: "daily")
    }

    private fun observeDashSessionChartData() {
        viewModel.sessionChartData.observe(viewLifecycleOwner, Observer { sessionData ->
            val sessionChartData = sessionData.resdata?.sessionChartData
            sessionChartData?.let {
                val xAxisLabelsMap = HashMap<String, Float>()
                xAxisLabelsMap.clear()
                var mappedValue = 0.0F
                for (data in it) {
                    val xLabel = data.dataName
                    if (xLabel != null && !xAxisLabelsMap.containsKey(xLabel)) {
                        xAxisLabelsMap[xLabel] = mappedValue
                        xAxisLabels.add(xLabel)
                        mappedValue += 1.0F
                    }
                }

                val uploadList = ArrayList<Entry>()
                val downloadList = ArrayList<Entry>()

                for (data in it) {
                    val xLabel = data.dataName
                    val upload = data.dataValueUp
                    val download = data.dataValueDown
                    xLabel?.let {
                        upload?.let {
                            uploadList.add(Entry(xAxisLabelsMap[xLabel]!!, upload))
                        }

                        download?.let {
                            downloadList.add(Entry(xAxisLabelsMap[xLabel]!!, download))
                        }
                    }
                }

                val uploadLineDataSet = LineDataSet(uploadList, "Upload")
                uploadLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
                uploadLineDataSet.colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorGreenTheme))
                uploadLineDataSet.circleColors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorGreenTheme))
                uploadLineDataSet.lineWidth = 2F
                uploadLineDataSet.circleRadius = 4F
                uploadLineDataSet.valueTextSize = 11F
                uploadLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

                val downloadLineDataSet = LineDataSet(downloadList, "Download")
                downloadLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
                downloadLineDataSet.colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorRed))
                downloadLineDataSet.circleColors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorRed))
                downloadLineDataSet.lineWidth = 2F
                downloadLineDataSet.circleRadius = 4F
                downloadLineDataSet.valueTextSize = 11F
                downloadLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

                // IMPORTANT!!!! DO NOT DELETE!!!!
                // FillDrawable is only supported on api level 18 and higher
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    downloadLineDataSet.setDrawFilled(true)
//                    val fillGradient = ContextCompat.getDrawable(requireContext(), R.drawable.red_gradient)
//                    downloadLineDataSet.fillDrawable = fillGradient
//                }

                val dataSets: ArrayList<ILineDataSet> = ArrayList()
                dataSets.add(uploadLineDataSet)
                dataSets.add(downloadLineDataSet)

                val data = LineData(dataSets)
                binding.accountStatusChart.data = data

                // IMPORTANT!!!! DO NOT DELETE!!!!
                // get the paint renderer to create the line shading.
//                val paint: Paint = binding.accountStatusChart.renderer.paintRender
//                val height: Float = binding.accountStatusChart.height.toFloat()
//
//                val linearGradient = LinearGradient(
//                    0F, 0F, 0F, height,
//                    ContextCompat.getColor(requireContext(), R.color.colorGreenTheme),
//                    ContextCompat.getColor(requireContext(), R.color.colorRed),
//                    Shader.TileMode.REPEAT
//                )
//                paint.shader = linearGradient

                binding.accountStatusChart.invalidate()
                binding.accountStatusChart.animateX(900)
            }
        })

//        viewModel.getChartData().observe(viewLifecycleOwner, Observer {
//            val chartData = it.resdata?.dashboardchartdata
//            if (chartData != null) {
//                val xAxisLabelsMap = HashMap<String, Float>()
//                xAxisLabelsMap.clear()
//                var mappedValue = 0.0F
//                for (data in chartData) {
//                    val dataMonth = data.dataMonth
//                    if (dataMonth != null && !xAxisLabelsMap.containsKey(dataMonth)) {
//                        xAxisLabelsMap[dataMonth] = mappedValue
//                        xAxisLabels.add(dataMonth)
//                        mappedValue += 1.0F
//                    }
//                }
//
//                val rechargeEntryList = ArrayList<Entry>()
//                val paymentEntryList = ArrayList<Entry>()
//
//                for (data in chartData) {
//                    val dataMonth = data.dataMonth
//                    val dataRecharge = data.dataRecharge
//                    val dataPayment = data.dataPayment
//                    if (dataMonth != null) {
//                        if (dataRecharge != null) {
//                            rechargeEntryList.add(Entry(xAxisLabelsMap[dataMonth]!!, dataRecharge))
//                        }
//                        if (dataPayment != null) {
//                            paymentEntryList.add(Entry(xAxisLabelsMap[dataMonth]!!, dataPayment))
//                        }
//                    }
//                }
//
//                val rechargeLineDataSet = LineDataSet(rechargeEntryList, "Total Recharge")
//                rechargeLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
//                rechargeLineDataSet.colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorGreenTheme))
//                rechargeLineDataSet.circleColors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorGreenTheme))
//                rechargeLineDataSet.lineWidth = 2F
//                rechargeLineDataSet.circleRadius = 4F
//                rechargeLineDataSet.valueTextSize = 11F
//
//                val paymentLineDataSet = LineDataSet(paymentEntryList, "Total Payment")
//                paymentLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
//                paymentLineDataSet.colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorRed))
//                paymentLineDataSet.circleColors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorRed))
//                paymentLineDataSet.lineWidth = 2F
//                paymentLineDataSet.circleRadius = 4F
//                paymentLineDataSet.valueTextSize = 11F
//
//                val dataSets: ArrayList<ILineDataSet> = ArrayList()
//                dataSets.add(rechargeLineDataSet)
//                dataSets.add(paymentLineDataSet)
//
//                val data = LineData(dataSets)
//                binding.accountStatusChart.data = data
//                binding.accountStatusChart.invalidate()
//                binding.accountStatusChart.animateX(900)
//            }
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                val exitDialog = CustomAlertDialog(object :
                    CustomAlertDialog.YesCallback {
                    override fun onYes() {
                        viewModel.onLogOut(preferences)
                        startActivity(Intent(requireActivity(), SplashActivity::class.java))
                        requireActivity().finish()
                    }
                }, "Do you want to Sign Out?", "")
                exitDialog.show(parentFragmentManager, "#sign_out_dialog")
                true
            }
            R.id.change_password -> {
                showChangePasswordDialog(parentFragmentManager)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
