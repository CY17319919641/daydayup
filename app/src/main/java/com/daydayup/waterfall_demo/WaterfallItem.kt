package com.daydayup.waterfall_demo

data class WaterfallItem(
        val id : Long,
        val title :String,
        val heightDp : Int,
        val colorHex : String,
        val isFullSpan : Boolean = false //用于控制某条目是否全跨
)  {
}