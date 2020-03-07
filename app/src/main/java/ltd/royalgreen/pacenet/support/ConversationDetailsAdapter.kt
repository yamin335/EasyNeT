package ltd.royalgreen.pacenet.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.support_chat_client_row.view.*
import kotlinx.android.synthetic.main.support_chat_host_row.view.*
import ltd.royalgreen.pacenet.R

class ConversationDetailsAdapter internal constructor(private val messageList: List<TicketConversation>) : RecyclerView.Adapter<ConversationDetailsAdapter.MyViewHolder>() {

    private val HOST = 0
    private val CLIENT = 1

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAppear() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDisappear() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (viewType == CLIENT) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.support_chat_client_row, parent, false)
            MyViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.support_chat_host_row, parent, false)
            MyViewHolder(itemView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].ispUserId != null && messageList[position].ispUserId != 0L) {
            CLIENT
        } else {
            HOST
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = messageList[position]
        val dateTime = formatDateTime(item.createDate)
        if (getItemViewType(position) == CLIENT) {
            holder.itemView.clientMessage.text = item.ticketComment?.trim()
            holder.itemView.clientTime.text = dateTime.second
        } else {
            holder.itemView.hostMessage.text = item.ticketComment?.trim()
            holder.itemView.hostTime.text = dateTime.second
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAppear()
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDisappear()
    }

    private fun formatDateTime(value: String?): Pair<String, String> {
        val months = arrayOf("Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec")
        var formattedDateTime: Pair<String, String>
        if (value != null && value.contains("T")) {
            val tempStringArray = value.split("T")
            var tempString1 = tempStringArray[1]
            if (tempString1.contains(".")){
                tempString1 = tempString1.split(".")[0]
                tempString1 = when {
                    tempString1.split(":")[0].toInt()>12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour-12}:$minute pm"
                    }
                    tempString1.split(":")[0].toInt() == 0 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour+12}:$minute am"
                    }
                    tempString1.split(":")[0].toInt()==12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "$hour:$minute pm"
                    }
                    else -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "$hour:$minute am"
                    }
                }
            } else {
                tempString1 = when {
                    tempString1.split(":")[0].toInt()>12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour-12}:$minute pm"
                    }
                    tempString1.split(":")[0].toInt() == 0 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour+12}:$minute am"
                    }
                    tempString1.split(":")[0].toInt()==12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "$hour:$minute pm"
                    }
                    else -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "$hour:$minute am"
                    }
                }
            }
            val year = tempStringArray[0].split("-")[0]
            val month = tempStringArray[0].split("-")[1]
            val day = tempStringArray[0].split("-")[2]
            formattedDateTime = Pair("$day ${months[month.toInt() - 1]} $year", tempString1)
        } else {
            formattedDateTime = Pair("", "")
        }
        return  formattedDateTime
    }
}
