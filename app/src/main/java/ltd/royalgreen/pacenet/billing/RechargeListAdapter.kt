package ltd.royalgreen.pacenet.billing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.billing_recharge_row.view.*
import ltd.royalgreen.pacenet.R
import java.math.BigDecimal
import java.math.RoundingMode



class RechargeListAdapter : PagedListAdapter<RechargeTransaction, RechargeListViewHolder>(RechargeHistDiffUtilCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RechargeListViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_recharge_row, parent, false)
    return RechargeListViewHolder(view)
  }

  override fun onBindViewHolder(holder: RechargeListViewHolder, position: Int) {
      val item = getItem(position)
//    val resources = holder.itemView.context.resources
      holder.itemView.particulars.text = item?.particulars
//      holder.itemView.config.text = item?.numberOfCpus.toString()+" CPU, "+item?.memorySize?.toDouble()?.div(1024.0).toString().split(".")[0]+" GB Memory"
      holder.itemView.balance.text = BigDecimal(item?.balanceAmount?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
      holder.itemView.credit.text = BigDecimal(item?.creditAmount?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()

      if (item?.transactionDate != null && item.transactionDate.contains("T")) {
          val tempStringArray = item.transactionDate.split("T")
          var tempString1 = tempStringArray[1]
          if (tempString1.contains(".")){
              tempString1 = tempString1.split(".")[0]
//              tempString1 = if(tempString1.split(":")[0].toInt()>=12) "$tempString1 PM" else "$tempString1 AM"
              tempString1 = when {
                  tempString1.split(":")[0].toInt()>12 -> {
                      val hour = tempString1.split(":")[0].toInt()
                      val minute = tempString1.split(":")[1].toInt()
                      val seconds = tempString1.split(":")[2].toInt()
                      "${hour-12}:$minute:$seconds PM"
                  }
                  tempString1.split(":")[0].toInt() == 0 -> {
                      val hour = tempString1.split(":")[0].toInt()
                      val minute = tempString1.split(":")[1].toInt()
                      val seconds = tempString1.split(":")[2].toInt()
                      "${hour+12}:$minute:$seconds AM"
                  }
                  tempString1.split(":")[0].toInt()==12 -> "$tempString1 PM"
                  else -> "$tempString1 AM"
              }
          } else {
//              tempString1 = if(tempString1.split(":")[0].toInt()>=12) "$tempString1 PM" else "$tempString1 AM"
              tempString1 = when {
                  tempString1.split(":")[0].toInt()>12 -> {
                      val hour = tempString1.split(":")[0].toInt()
                      val minute = tempString1.split(":")[1].toInt()
                      val seconds = tempString1.split(":")[2].toInt()
                      "${hour-12}:$minute:$seconds PM"
                  }
                  tempString1.split(":")[0].toInt() == 0 -> {
                      val hour = tempString1.split(":")[0].toInt()
                      val minute = tempString1.split(":")[1].toInt()
                      val seconds = tempString1.split(":")[2].toInt()
                      "${hour+12}:$minute:$seconds AM"
                  }
                  tempString1.split(":")[0].toInt()==12 -> "$tempString1 PM"
                  else -> "$tempString1 AM"
              }
          }
          val year = tempStringArray[0].split("-")[0]
          val month = tempStringArray[0].split("-")[1]
          val day = tempStringArray[0].split("-")[2]
          holder.itemView.date.text = "$day-$month-$year\n$tempString1"
      }
  }
}

class RechargeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
