package com.certified.autopaper.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.certified.autopaper.databinding.BackButtonBinding

class BackButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int
) :
    ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    //    BackButton
    private val binding =
        BackButtonBinding.inflate(LayoutInflater.from(context), this, true)

    fun setOnClickListener(onClick: () -> Unit) {
        binding.cardBack.setOnClickListener { onClick }
    }
}