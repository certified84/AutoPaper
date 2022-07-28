package com.certified.autopaper.util

import android.widget.Toast
import androidx.fragment.app.Fragment

object Extensions {

    fun Fragment.showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }
}