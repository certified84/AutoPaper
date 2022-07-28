package com.certified.autopaper.adapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.certified.autopaper.util.formatAmount
import com.google.android.material.textview.MaterialTextView

@BindingAdapter("sliderImage")
fun ImageView.bindSliderImageView(imageUrl: Int?) {
    this.load(imageUrl!!)
}

@BindingAdapter("visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("hi_user")
fun MaterialTextView.hiUser(name: String) {
    text = "Hi ${name.substringBefore(" ")}! 👋"
}

@BindingAdapter("money_text")
fun MaterialTextView.setMoneyText(value: String?) {
    text = if (value != null)
        "₦ ${formatAmount(value)}"
    else
        "₦ ${formatAmount("0")}"
}