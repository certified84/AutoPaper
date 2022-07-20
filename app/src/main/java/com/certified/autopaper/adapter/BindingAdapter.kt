package com.certified.autopaper.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("sliderImage")
fun ImageView.bindSliderImageView(imageUrl: Int?) {
    this.load(imageUrl!!)
}