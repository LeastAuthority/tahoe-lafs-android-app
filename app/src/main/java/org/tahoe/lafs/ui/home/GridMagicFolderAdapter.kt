package org.tahoe.lafs.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import org.tahoe.lafs.R
import org.tahoe.lafs.network.services.GridNode

internal class GridMagicFolderAdapter(private var nodesList: List<GridNode>) :
    RecyclerView.Adapter<GridMagicFolderAdapter.MagicFolderViewHolder>() {
    internal inner class MagicFolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtFolderName: TextView = view.findViewById(R.id.txtFolderName)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MagicFolderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_view_item, parent, false)
        return MagicFolderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MagicFolderViewHolder, position: Int) {
        val node = nodesList[position]
        holder.txtFolderName.text = node.name.replace("(collective)", "")
    }

    override fun getItemCount(): Int {
        return nodesList.size
    }
}