package com.example.domain

data class Cell(
    val row: Int,
    val column: Int,
    var type: CellType,
    var previousCellCoordinates: Pair<Int, Int>? = null
)
