package com.daydayup.waterfalldemo

import kotlin.random.Random

object WaterfallMockData {

    private val colorList = listOf(
        "#FFE4E1", "#E0F7FA", "#FFF3E0", "#E8F5E9", "#F3E5F5",
        "#E1F5FE", "#F1F8E9", "#FCE4EC", "#EDE7F6", "#FFF8E1"
    )

    fun createInitialList(count: Int = 30) : MutableList<WaterfallItem>{
        val result = mutableListOf<WaterfallItem>()

        // 顶部全宽提示卡片
        result.add(
            WaterfallItem(
                id = 1L,
                title = "📌 全宽条目（Banner）：演示 fullSpan",
                heightDp = 88,
                colorHex = "#D1C4E9",
                isFullSpan = true
            )
        )

        val baseId = 2L
        repeat(count) { index ->
            result.add(
                WaterfallItem(
                    id = baseId + index,
                    title = "第 ${index + 1} 个卡片",
                    heightDp = Random.Default.nextInt(130, 280),
                    colorHex = colorList.random(),
                    isFullSpan = false
                )
            )
        }
        return result
    }

    fun createMoreList(startId: Long, count: Int = 20): List<WaterfallItem> {
        return List(count) { index ->
            WaterfallItem(
                id = startId + index,
                title = "加载更多 #${startId + index}",
                heightDp = Random.Default.nextInt(130, 300),
                colorHex = colorList.random(),
                isFullSpan = false
            )
        }
    }
}