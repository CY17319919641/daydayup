package com.daydayup.recyclerviewdemo

data class WithdrawTier(
    val conditionLevel: Int,
    val multiple: Float,
    var status: Int = STATUS_PENDING,
    var fixedAmount: String? = null
) {
    companion object {
        const val STATUS_PENDING = 1
        const val STATUS_PAYING = 2
        const val STATUS_DELIVERING = 3
        const val STATUS_DONE = 4
    }
}