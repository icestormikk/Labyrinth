package com.example.utilities

import com.example.domain.Cell
import com.example.domain.CellType
import com.example.domain.CellType.EMPTY
import com.example.domain.CellType.ENTER
import com.example.domain.CellType.EXIT
import com.example.domain.CellType.VISITED
import com.example.domain.BuilderDirection
import com.example.domain.CellType.PATH
import com.example.domain.PathfinderDirection
import tornadofx.*
import kotlin.error
import kotlin.math.abs

private const val START_CELL_X = 1
private const val START_CELL_Y = 1

object LabyrinthUtilities {
    private var labyrinth: Array<Array<Cell>> = arrayOf(arrayOf())

    object Builder {

        fun labyrinthInitialization(labyrinthSize: Int) {
            labyrinthInitialization(labyrinthSize, labyrinthSize)
        }
        fun labyrinthInitialization(labyrinthWidth: Int, labyrinthHeight: Int) {
            labyrinth = createLabyrinthBase(labyrinthWidth, labyrinthHeight)
            fillLabyrinth(labyrinth[START_CELL_X][START_CELL_Y])

            labyrinth.forEach {
                it.forEach { cell -> if (cell.type == VISITED) cell.type = EMPTY }
            }
            /*labyrinth.forEach {
                it.forEach { cell ->
                    print("(${cell.row}, ${cell.column}) ")
                }
                println()
            }*/
        }

        private fun fillLabyrinth(cell: Cell) {
            do {
                with (getRandomDirection<BuilderDirection>()) {
                    cell.type = VISITED
                    if (predicate(labyrinth, cell) || Math.random() < 0.1) {
                        if (cell.row + vector.first > 0 && cell.row + vector.first < labyrinth.size &&
                            cell.column + vector.second > 0 && cell.column + vector.second < labyrinth[0].size
                        ) {
                            val nextCell = labyrinth[cell.row + vector.first][cell.column + vector.second]
                            removeWall(cell, nextCell)
                            fillLabyrinth(nextCell)
                        }
                    }
                }
            } while (cell.hasNotVisitedNeighbours<BuilderDirection>())
        }

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
            val xDifference = secondCell.row - firstCell.row
            val yDifference = secondCell.column - firstCell.column

            val addX = if (xDifference != 0) xDifference / abs(xDifference) else 0
            val addY = if (yDifference != 0) yDifference / abs(yDifference) else 0

            labyrinth[firstCell.row + addX][firstCell.column + addY].type = VISITED
        }
    }

    object Pathfinder {
        private lateinit var START_CELL: Cell
        private lateinit var EXIT_CELL: Cell

        fun setStartCell(cell: Cell) { START_CELL = cell; START_CELL.type = ENTER }
        fun setExitCell(cell: Cell) { EXIT_CELL = cell; EXIT_CELL.type = EXIT }
        fun passLabyrinth(cell: Cell) {
            if (!this::START_CELL.isInitialized) {
                println("Start cell was not initialized! Default initialization: [1,1]")
                setStartCell(labyrinth[1][1])
            }
            if (!this::EXIT_CELL.isInitialized) {
                with (labyrinth) {
                    println("Exit cell was not initialized! Default initialization: [${size - 2}, ${size - 2}]")
                    setExitCell(this[size - 2][this[0].size - 2])
                }
            }
            setStartCell(labyrinth[1][1])
            with (labyrinth) {
                setExitCell(this[size - 2][this[0].size - 2])
            }

            passLabyrinthRecursive(cell)
        }

        private fun passLabyrinthRecursive(cell: Cell) {
            if (cell.type != EXIT) {
                cell.type = VISITED
                while (cell.hasNotVisitedNeighbours<PathfinderDirection>()) {
                    with(cell.getNeighbourCells().random()) {
                        previousCellCoordinates = Pair(cell.row, cell.column)
                        passLabyrinthRecursive(this)
                    }
                }
            } else drawPath(cell)
        }

        private fun drawPath(cell: Cell) {
            if (cell.previousCellCoordinates != null) {
                cell.type = PATH
                drawPath(labyrinth[cell.previousCellCoordinates!!.first][cell.previousCellCoordinates!!.second])
            }
        }
    }

    fun getLabyrinth() = labyrinth.copyOf()

    private fun Cell.getNeighbourCells(): Collection<Cell> =
        PathfinderDirection.values().filter {
            it.predicate(labyrinth, this)
        }.map { labyrinth[this.row + it.vector.first][this.column + it.vector.second] }

    private inline fun <reified T> Cell.hasNotVisitedNeighbours(): Boolean =
        when (T::class) {
            BuilderDirection::class -> BuilderDirection.values().any { it.predicate(labyrinth, this) }
            PathfinderDirection::class -> PathfinderDirection.values().any { it.predicate(labyrinth, this) }
            else -> error("Unrecognized class: ${T::class}")
        }

    private inline fun <reified T> getRandomDirection(): T =
        when (T::class) {
            BuilderDirection::class -> {
                with (BuilderDirection.values()) {
                    val randomDirectionCode = (START_CELL_X..this.size).random()
                    first { it.code == randomDirectionCode } as T
                    }
            }
            PathfinderDirection::class -> {
                with (PathfinderDirection.values()) {
                    val randomDirectionCode = (START_CELL_X..this.size).random()
                    first { it.code == randomDirectionCode } as T
                }
            }
            else -> error("Unrecognized class: ${T::class}")
        }
}
