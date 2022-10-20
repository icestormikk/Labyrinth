package com.example.domain

/**
 * The basic unit of the maze
 * @property row the number of the row in which the cell is located
 * @property column the number of the column in which the cell is located
 * @property type the type of this cell
 * @property depth the distance from the initial cell to the current one (only for the pathfinder)
 */
data class Cell(
    val row: Int,
    val column: Int,
    var type: CellType,
    var depth: Int = 0
)
