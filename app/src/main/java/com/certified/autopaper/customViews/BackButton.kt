package com.certified.autopaper.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.certified.autopaper.databinding.BackButtonBinding

class BackButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) :
    ConstraintLayout(context, attributeSet, defStyle) {

    //    BackButton
    private val binding =
        BackButtonBinding.inflate(LayoutInflater.from(context), this, true)

    /*listeners*/
    private var backClickedListener: OnClickListener? = null

    init {
        binding.root.setOnClickListener {
            backClickedListener?.onClick(it)
        }
    }

    fun setOnBackClickedListener(listener: OnClickListener) {
        backClickedListener = listener
    }
}

interface OnBackClickedListener {
    fun onClick(click: () -> Unit?)
}