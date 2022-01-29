package com.jingtian.lmemorycleaner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jingtian.lmemorycleaner.databinding.ItemMainFunctionBinding

abstract class CommonAdapter<D,B:ViewBinding>: RecyclerView.Adapter<CommonAdapter.ViewHolder<B>>() {
    open class ViewHolder<B:ViewBinding>(val viewBinding: B): RecyclerView.ViewHolder(viewBinding.root)
    open var data = mutableListOf<D>()
        set(value) {
            field = value
            notifyItemRangeChanged(0, data.size)
        }

    fun MutableList<D>.insertAll(data:List<D>) {
        val start = this.size
        this.addAll(data)
        notifyItemRangeInserted(start, data.size)
    }
    fun MutableList<D>.insert(item:D) {
        this.add(item)
        notifyItemInserted(data.size-1)
    }

    fun MutableList<D>.delete(item:D) {
        val position = this.indexOf(item)
        data.remove(item)
        notifyItemRemoved(position)
    }

    fun updateItem(item:D, updateFunction:(ViewHolder<B>)->Unit) {
        val position = data.indexOf(item)
        notifyItemChanged(position, updateFunction)
    }

    override fun onBindViewHolder(
        holder: ViewHolder<B>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val updateFunction = payloads[0] as (ViewHolder<B>)->Unit
            updateFunction(holder)
        }
    }
    abstract fun getViewBinding(parent: ViewGroup):B
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<B> {
        return ViewHolder(getViewBinding(parent))
    }
    override fun getItemCount(): Int = data.size
}