package com.certified.autopaper.adapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("sliderImage")
fun ImageView.bindSliderImageView(imageUrl: Int?) {
    this.load(imageUrl!!)
}

@BindingAdapter("visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}