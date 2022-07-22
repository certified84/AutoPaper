package com.certified.autopaper.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.autopaper.data.model.SliderItem
import com.certified.autopaper.databinding.ItemSliderBinding

class ViewPagerAdapter :
    ListAdapter<SliderItem, ViewPagerAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ViewHolder(val binding: ItemSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sliderItem: SliderItem) {
            binding.sliderItem = sliderItem
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SliderItem>() {
            override fun areItemsTheSame(oldItem: SliderItem, newItem: SliderItem) =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: SliderItem, newItem: SliderItem) =
                oldItem == newItem
        }
    }
}