package com.stewardapostol.jfc.ui.utils

object ColorGenerator {
    private val tagColors = listOf(
        0xFFF44336, // Red
        0xFFE91E63, // Pink
        0xFF9C27B0, // Purple
        0xFF2196F3, // Blue
        0xFF00BCD4, // Cyan
        0xFF4CAF50, // Green
        0xFF8BC34A, // Light Green
        0xFFFF9800, // Orange
    )
    fun getRandomColor(): Int = tagColors.random().toInt()
}