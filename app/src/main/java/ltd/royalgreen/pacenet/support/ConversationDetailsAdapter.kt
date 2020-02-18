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
        if (getItemViewType(position) == CLIENT) {
            holder.itemView.clientMessage.text = item.ticketComment?.trim()
        } else {
            holder.itemView.hostMessage.text = item.ticketComment?.trim()
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
}
