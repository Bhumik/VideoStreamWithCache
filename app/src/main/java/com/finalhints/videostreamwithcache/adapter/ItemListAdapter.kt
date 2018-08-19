package com.finalhints.videostreamwithcache.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finalhints.videostreamwithcache.R
import com.finalhints.videostreamwithcache.models.ItemType
import kotlinx.android.synthetic.main.adapter_item_list.view.*

class ItemListAdapter(val mContext: Context, val mEntityList: ArrayList<ItemType>) : RecyclerView.Adapter<ItemListAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_list, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return mEntityList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    private fun getItem(position: Int): ItemType {
        return mEntityList.get(position)
    }

    inner class ItemHolder(pView: View) : RecyclerView.ViewHolder(pView) {
        val clRoot = pView.clRoot
        val tvTitle = pView.tvTitle
        val tvDescription = pView.tvDescription

        init {
            clRoot.setOnClickListener {
                mActionListener?.onItemClick(adapterPosition, mEntityList[adapterPosition])
            }
        }

        fun bindView(item: ItemType) {
            tvTitle.text = item.title
            tvDescription.text = item.description
        }
    }


    //action listnener
    private var mActionListener: ActionListener? = null

    interface ActionListener {
        fun onItemClick(position: Int, item: ItemType)
    }

    fun setActionListener(pActionListener: ActionListener) {
        mActionListener = pActionListener
    }


}
