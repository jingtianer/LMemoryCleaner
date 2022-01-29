package com.jingtian.lmemorycleaner.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jingtian.lmemorycleaner.adapter.CommonAdapter
import com.jingtian.lmemorycleaner.bean.MainFunctionsBean
import com.jingtian.lmemorycleaner.databinding.ItemMainFunctionBinding

class MainFunctionRVAdapter(data:List<MainFunctionsBean>,private val clickCallback: (id:Int)->Unit) : CommonAdapter<MainFunctionsBean, ItemMainFunctionBinding>() {
    init {
        super.data.insertAll(data)
    }
    override fun onBindViewHolder(
        holder: ViewHolder<ItemMainFunctionBinding>,
        position: Int
    ) {
        holder.viewBinding.icon.setImageDrawable(data[position].icon)
        holder.viewBinding.name.text = data[position].name
        holder.viewBinding.root.setOnClickListener {
            clickCallback(data[position].id)
        }
    }
    override fun getViewBinding(parent: ViewGroup): ItemMainFunctionBinding =
        ItemMainFunctionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
}