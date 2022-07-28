package com.certified.autopaper.util

import java.text.DecimalFormat

fun formatAmount(amount: String?): String {
    val money = amount?.toDouble()
    val formatter = DecimalFormat("###,###,###.00")
    return if (amount != "0") formatter.format(money) else "0.00"
}