package com.certified.autopaper.adapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.certified.autopaper.R
import com.certified.autopaper.util.formatAmount
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

@BindingAdapter("sliderImage")
fun ImageView.bindSliderImageView(imageUrl: Int?) {
    this.load(imageUrl!!)
}

@BindingAdapter("profileImage")
fun ShapeableImageView.bindProfileImageView(imageUrl: String?) {
    if (imageUrl == null || imageUrl.isBlank())
        this.load(R.drawable.no_profile_image) { transformations(CircleCropTransformation()) }
    else this.load(imageUrl) {
        placeholder(R.drawable.no_profile_image)
        transformations(CircleCropTransformation())
    }
}

@BindingAdapter("visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("hi_user")
fun MaterialTextView.hiUser(name: String?) {
    text = if (name == null || name.isBlank()) "Hi there! 👋"
    else "Hi ${name.substringBefore(" ")}! 👋"
}

@BindingAdapter("money_text")
fun MaterialTextView.setMoneyText(value: String?) {
    text = if (value == null || value.isBlank())
        "₦ ${formatAmount("0")}"
    else
        "₦ ${formatAmount(value)}"
}