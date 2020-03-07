package ltd.royalgreen.pacenet.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
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
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.MainNavigationFragment
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.SplashActivity
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

    interface DashItemInteractionListener {
        fun onDashItemClicked(navigation: String)
        fun setupToolbar(toolbar: MaterialToolbar)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashItemInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement DashItemInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        listener?.setupToolbar(binding.toolbar)

        val formatter: ValueFormatter =
            object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return xAxisLabels[value.toInt()]
                }
            }
        val xAxis: XAxis = binding.accountStatusChart.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatter

        val description = Description()
        description.text = "User's Recharges and Payments"
        binding.accountStatusChart.description = description

        refreshDashChart()

        binding.myAccount.setOnClickListener {
            listener?.onDashItemClicked("PROFILE")
        }

        binding.payNow.setOnClickListener {
            listener?.onDashItemClicked("PAY_NOW")
        }

        binding.payHistory.setOnClickListener {
            listener?.onDashItemClicked("PAY_HISTORY")
        }

        binding.openTicket.setOnClickListener {
            listener?.onDashItemClicked("OPEN_TICKET")
        }

        binding.ticketHistory.setOnClickListener {
            listener?.onDashItemClicked("TICKET_HISTORY")
        }

//        logout.setOnClickListener {
//            startActivity(Intent(requireActivity(), SplashActivity::class.java))
//            requireActivity().finish()
//        }
    }

    private fun refreshDashChart() {
        viewModel.getChartData().observe(viewLifecycleOwner, Observer {
            val chartData = it.resdata?.dashboardchartdata
            if (chartData != null) {
                val xAxisLabelsMap = HashMap<String, Float>()
                xAxisLabelsMap.clear()
                var mappedValue = 0.0F
                for (data in chartData) {
                    val dataMonth = data.dataMonth
                    if (dataMonth != null && !xAxisLabelsMap.containsKey(dataMonth)) {
                        xAxisLabelsMap[dataMonth] = mappedValue
                        xAxisLabels.add(dataMonth)
                        mappedValue += 1.0F
                    }
                }

                val rechargeEntryList = ArrayList<Entry>()
                val paymentEntryList = ArrayList<Entry>()

                for (data in chartData) {
                    val dataMonth = data.dataMonth
                    val dataRecharge = data.dataRecharge
                    val dataPayment = data.dataPayment
                    if (dataMonth != null) {
                        if (dataRecharge != null) {
                            rechargeEntryList.add(Entry(xAxisLabelsMap[dataMonth]!!, dataRecharge))
                        }
                        if (dataPayment != null) {
                            paymentEntryList.add(Entry(xAxisLabelsMap[dataMonth]!!, dataPayment))
                        }
                    }
                }

                val rechargeLineDataSet = LineDataSet(rechargeEntryList, "Total Recharge")
                rechargeLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
                rechargeLineDataSet.colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorGreenTheme))
                rechargeLineDataSet.circleColors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorGreenTheme))
                rechargeLineDataSet.lineWidth = 2F
                rechargeLineDataSet.circleRadius = 4F
                rechargeLineDataSet.valueTextSize = 11F

                val paymentLineDataSet = LineDataSet(paymentEntryList, "Total Payment")
                paymentLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
                paymentLineDataSet.colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorRed))
                paymentLineDataSet.circleColors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorRed))
                paymentLineDataSet.lineWidth = 2F
                paymentLineDataSet.circleRadius = 4F
                paymentLineDataSet.valueTextSize = 11F

                val dataSets: ArrayList<ILineDataSet> = ArrayList()
                dataSets.add(rechargeLineDataSet)
                dataSets.add(paymentLineDataSet)

                val data = LineData(dataSets)
                binding.accountStatusChart.data = data
                binding.accountStatusChart.invalidate()
                binding.accountStatusChart.animateX(900)
            }
        })
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
