package com.example.utilities

import com.example.domain.Cell
import com.example.domain.CellType
import com.example.domain.Direction
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
