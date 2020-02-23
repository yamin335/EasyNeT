package ltd.royalgreen.pacenet.support

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.file_layout.view.*
import kotlinx.android.synthetic.main.support_chat_client_row.view.*
import kotlinx.android.synthetic.main.support_chat_host_row.view.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.util.FileUtils

class FileUriAdapter internal constructor(private val fileUriList: List<Uri>, private val listener: FileDeleteCallback) : RecyclerView.Adapter<FileUriAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.file_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = fileUriList[position]
        holder.itemView.fileName.text = FileUtils.getFileName(holder.itemView.context, item)
        holder.itemView.fileSize.text = "Size: ${FileUtils.getFileSize(holder.itemView.context, item)}"
        holder.itemView.deleteIcon.setOnClickListener {
            listener.onFileDeleted(position)
        }
    }

    override fun getItemCount(): Int {
        return fileUriList.size
    }

    interface FileDeleteCallback {
        fun onFileDeleted(position: Int)
    }
}
