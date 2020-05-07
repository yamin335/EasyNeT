package ltd.royalgreen.pacenet.dashboard

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dash_session_chart_change_dialog.*
import ltd.royalgreen.pacenet.R

class DashSessionChartChangeDialog internal constructor(private val callBack: SessionChartChangeCallback, private var selectedType: String, private var selectedMonth: Int) : DialogFragment() {

    override fun getTheme(): Int {
        return R.style.CustomDialog
    }

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
        return inflater.inflate(R.layout.dash_session_chart_change_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeDialog.setOnClickListener {
            dismiss()
        }

        save.setOnClickListener {
            callBack.onSessionChartChanged(selectedType, selectedMonth)
            dismiss()
        }

        when (selectedType) {
            "monthly" -> toggleButton.check(R.id.monthly)
            "daily" -> toggleButton.check(R.id.daily)
            "hourly" -> toggleButton.check(R.id.hourly)
        }

        when (selectedMonth) {
            1 -> chipGroup.check(R.id.jan)
            2 -> chipGroup.check(R.id.feb)
            3 -> chipGroup.check(R.id.march)
            4 -> chipGroup.check(R.id.april)
            5 -> chipGroup.check(R.id.may)
            6 -> chipGroup.check(R.id.june)
            7 -> chipGroup.check(R.id.july)
            8 -> chipGroup.check(R.id.aug)
            9 -> chipGroup.check(R.id.sep)
            10 -> chipGroup.check(R.id.oct)
            11 -> chipGroup.check(R.id.nov)
            12 -> chipGroup.check(R.id.dec)
        }

        toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            // Respond to button selection
            if (isChecked) {
                when (checkedId) {
                    R.id.monthly -> selectedType = "monthly"
                    R.id.daily -> selectedType = "daily"
                    R.id.hourly -> selectedType = "hourly"
                }
            }
        }

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedMonth = when (checkedId) {
                R.id.jan -> 1
                R.id.feb -> 2
                R.id.march -> 3
                R.id.april -> 4
                R.id.may -> 5
                R.id.june -> 6
                R.id.july -> 7
                R.id.aug -> 8
                R.id.sep -> 9
                R.id.oct -> 10
                R.id.nov -> 11
                R.id.dec -> 12
                else -> 1
            }
        }
    }

    interface SessionChartChangeCallback{
        fun onSessionChartChanged(selectedType: String, selectedMonth: Int)
    }
}