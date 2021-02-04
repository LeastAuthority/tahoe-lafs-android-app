package org.tahoe.lafs.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.getShortCollectiveFolderName
import org.tahoe.lafs.extension.hide
import org.tahoe.lafs.extension.show
import org.tahoe.lafs.network.services.GridNode

internal class GridFolderAdapter(
    private var nodesList: List<GridNode>,
    private val gridItemClickListener: GridItemClickListener
) :
    RecyclerView.Adapter<GridFolderAdapter.MagicFolderViewHolder>() {
    internal inner class MagicFolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtFolderName: AppCompatTextView = view.findViewById(R.id.txtFolderName)
        var txtFolderDesc: AppCompatTextView = view.findViewById(R.id.txtFolderDesc)
        var imgFolder: AppCompatImageView = view.findViewById(R.id.imgFolder)
        var btnDownload: AppCompatImageButton = view.findViewById(R.id.btnDownload)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MagicFolderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_view_item, parent, false)
        return MagicFolderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MagicFolderViewHolder, position: Int) {
        val node = nodesList[position]
        holder.txtFolderName.text = node.name.getShortCollectiveFolderName()

        if (node.isDir) {
            holder.imgFolder.setImageResource(R.drawable.ic_folder)
            holder.btnDownload.hide()
            holder.txtFolderDesc.text = node.getFormattedDescription()
        } else {
            holder.imgFolder.setImageResource(R.drawable.ic_file)
            holder.txtFolderDesc.text = node.getFormattedFileSize()
            if (node.size > 0) holder.btnDownload.show() else holder.btnDownload.hide()
        }

        holder.itemView.setOnClickListener {
            gridItemClickListener.onGridItemClickListener(node)
        }

        holder.btnDownload.setOnClickListener {
            gridItemClickListener.onDownloadItemClickListener(node)
        }
    }

    override fun getItemCount(): Int {
        return nodesList.size
    }
}

interface GridItemClickListener {
    fun onGridItemClickListener(gridNode: GridNode)
    fun onDownloadItemClickListener(gridNode: GridNode)
}