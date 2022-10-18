package com.example.utilities

import com.example.domain.Cell
import com.example.domain.CellType
import com.example.domain.Direction
import kotlin.math.abs

private const val START_CELL_X = 1
private const val START_CELL_Y = 1

object LabyrinthUtilities {
    private lateinit var labyrinth: Array<Array<Cell>>

    fun labyrinthInitialization(labyrinthSize: Int) {
        labyrinthInitialization(labyrinthSize, labyrinthSize)
    }

    fun labyrinthInitialization(labyrinthWidth: Int, labyrinthHeight: Int) {
        labyrinth = createLabyrinthBase(labyrinthWidth, labyrinthHeight)
        fillLabyrinth(labyrinth[START_CELL_X][START_CELL_Y])
        /*labyrinth.forEach { row ->
            row.forEach { cell ->
                print("(${cell.x}, ${cell.y}) ")
            }
            println()
        }*/
    }

    fun getLabyrinth() = labyrinth.copyOf()

    private fun fillLabyrinth(cell: Cell) {
        do {
            with (getRandomDirection()) {
                cell.type = CellType.VISITED
                if (predicate(labyrinth, cell)) {
                    val nextCell = labyrinth[cell.x + vector.first][cell.y + vector.second]
                    removeWall(cell, nextCell)
                    fillLabyrinth(nextCell)
                }
            }
        } while (cell.hasNotVisitedNeighbours())
    }

    private fun Cell.hasNotVisitedNeighbours(): Boolean =
        Direction.values().any { it.predicate(labyrinth, this) }

        @Suppress("ComplexCondition")
        private fun createLabyrinthBase(labyrinthWidth: Int, labyrinthHeight: Int): Array<Array<Cell>> {
            val compatibleWidth = if (labyrinthWidth % 2 == 0) labyrinthWidth - 1 else labyrinthWidth
            val compatibleHeight = if (labyrinthHeight % 2 == 0) labyrinthHeight - 1 else labyrinthHeight

            return Array(compatibleHeight) { rowIndex ->
                Array(compatibleWidth) { columnIndex ->
                    Cell(
                        row = rowIndex,
                        column = columnIndex,
                        type = if (columnIndex % 2 == 0 || rowIndex % 2 == 0)
                            CellType.WALL
                        else EMPTY
                    )
                }
            }
        }

    private fun removeWall(firstCell: Cell, secondCell: Cell) {
        val xDifference = secondCell.x - firstCell.x
        val yDifference = secondCell.y - firstCell.y

        val addX = if (xDifference != 0) xDifference / abs(xDifference) else 0
        val addY = if (yDifference != 0) yDifference / abs(yDifference) else 0

        labyrinth[firstCell.x + addX][firstCell.y + addY].type = CellType.VISITED
    }

    private fun getRandomDirection(): Direction =
        with (Direction.values()) {
            val randomDirectionCode = (START_CELL_X..this.size).random()
            first { it.code == randomDirectionCode }
        }
}
