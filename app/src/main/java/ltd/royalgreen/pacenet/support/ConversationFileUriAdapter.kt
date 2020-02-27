package ltd.royalgreen.pacenet.support

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.file_layout_conversation.view.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.util.FileUtils

class ConversationFileUriAdapter internal constructor(private val fileUriList: ArrayList<Uri>, private val listener: FileDeleteCallback) : RecyclerView.Adapter<ConversationFileUriAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.file_layout_conversation, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemPosition = holder.adapterPosition
        val item = fileUriList[itemPosition]
        holder.itemView.fileName.text = FileUtils.getFileName(holder.itemView.context, item)
        holder.itemView.fileSize.text = FileUtils.getFileSize(holder.itemView.context, item)
        holder.itemView.deleteIcon.setOnClickListener {
            listener.onFileDeleted(itemPosition)
        }
    }

    override fun getItemCount(): Int {
        return fileUriList.size
    }

    interface FileDeleteCallback {
        fun onFileDeleted(position: Int)
    }
}
