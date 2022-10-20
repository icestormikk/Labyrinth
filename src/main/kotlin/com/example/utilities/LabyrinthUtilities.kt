package com.example.utilities

import com.example.configuration.SERVICE_CELL_TYPES
import com.example.domain.Cell
import com.example.domain.CellType.EMPTY
import com.example.domain.CellType.ENTER
import com.example.domain.CellType.EXIT
import com.example.domain.CellType.VISITED
import com.example.domain.BuilderDirection
import com.example.domain.CellType.PATH
import com.example.domain.CellType.WALL
import com.example.domain.PathfinderDirection
import com.example.domain.ReturningPathfinderDirection
import tornadofx.*
import java.util.Queue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.error
import kotlin.math.abs

private const val START_CELL_X = 1
private const val START_CELL_Y = 1
private const val RANDOM_BREAKING_WALL_COEF = 0.05

/**
 * Functions for managing the state of the maze
 */
object LabyrinthUtilities {
    private var labyrinth: Array<Array<Cell>> = arrayOf(arrayOf())

    /**
     * Stores the completion state of the maze solution
     */
    var IS_SOLVED = false
        private  set

    /**
     * Functions for building a maze and other operations not related to finding the way to the exit
     */
    object Builder {

        /**
         * Initializing a square maze of size [labyrinthSize] x [labyrinthSize]
         * @param labyrinthSize the size of the maze
         */
        fun labyrinthInitialization(labyrinthSize: Int) {
            labyrinthInitialization(labyrinthSize, labyrinthSize)
        }

        /**
         * Initializing a square maze of size [labyrinthWidth] x [labyrinthHeight]
         * @param labyrinthWidth desired width of the maze
         * @param labyrinthHeight desired height of the maze
         */
        fun labyrinthInitialization(labyrinthWidth: Int, labyrinthHeight: Int) {
            IS_SOLVED = false
            with (createLabyrinthBase(labyrinthWidth, labyrinthHeight)) {
                labyrinth = this
                fillLabyrinth(this[START_CELL_X][START_CELL_Y])

                Pathfinder.setEnterCell(1, 1)
                Pathfinder.setExitCell(size - 2,this[0].size - 2)

                clearLabyrinth()
            }
        }

        /**
         * Replacing all maze cells with the [VISITED] or [PATH] type with cells with the [EMPTY] type
         */
        fun clearLabyrinth() {
            IS_SOLVED = false
            labyrinth.forEach {
                it.forEach { cell -> if (cell.type !in SERVICE_CELL_TYPES) cell.type = EMPTY }
            }
        }

        @SuppressWarnings("NestedBlockDepth", "ComplexCondition")
        private fun fillLabyrinth(cell: Cell) {
            do {
                with (getRandomDirection<BuilderDirection>()) {
                    cell.type = VISITED
                    if (predicate(labyrinth, cell) || Math.random() < RANDOM_BREAKING_WALL_COEF) {
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
                        type = if (columnIndex % 2 == 0 || rowIndex % 2 == 0) WALL else EMPTY
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

    /**
     * Declaration of functions related to finding the path from the [ENTER] cell to the [EXIT] cell
     */
    object Pathfinder {
        private val cellsQueue: Queue<Cell> = LinkedBlockingQueue()

        /**
         * The point from which pathfinder starts its journey
         */
        lateinit var START_CELL: Cell
            private set

        /**
         * The point at which pathfinder should end its journey
         */
        lateinit var EXIT_CELL: Cell
            private set

        /**
         * Sets the starting point
         * @param rowIndex the number of the line containing the point to be replaced
         * @param columnIndex the number of the column containing the point to be replaced
         */
        fun setEnterCell(rowIndex: Int, columnIndex: Int) {
            if (labyrinth[rowIndex][columnIndex].type != WALL) {
                if (this::START_CELL.isInitialized)
                    START_CELL.type = EMPTY
                START_CELL = labyrinth[rowIndex][columnIndex]
                START_CELL.type = ENTER
            }
        }
        /**
         * Sets the final point
         * @param rowIndex the number of the line containing the point to be replaced
         * @param columnIndex the number of the column containing the point to be replaced
         */
        fun setExitCell(rowIndex: Int, columnIndex: Int) {
            if (labyrinth[rowIndex][columnIndex].type != WALL) {
                if (this::EXIT_CELL.isInitialized)
                    EXIT_CELL.type = EMPTY
                EXIT_CELL = labyrinth[rowIndex][columnIndex]
                EXIT_CELL.type = EXIT
            }
        }

        /**
         * Start the process of passing through the maze and finding the optimal path from
         * the [ENTER] point to the [EXIT] point
         */
        fun passLabyrinth() {
            IS_SOLVED = true
            if (!this::START_CELL.isInitialized) {
                println("Enter cell was not initialized! Default initialization: [1,1]")
                setEnterCell(1, 1)
            }
            if (!this::EXIT_CELL.isInitialized) {
                with (labyrinth) {
                    println("Exit cell was not initialized! Default initialization: [${size - 2}, ${size - 2}]")
                    setExitCell(size - 2, this[0].size - 2)
                }
            }

            passLabyrinth(START_CELL)
            drawPath(EXIT_CELL)
        }
        private fun passLabyrinth(cell: Cell) {
            cellsQueue.add(cell)
            while (cellsQueue.isNotEmpty()) {
                val currentCell = cellsQueue.poll()
                currentCell.getNeighbourCells<PathfinderDirection>().forEach {
                    with (it) {
                        this.depth = currentCell.depth + 1
                        type = VISITED
                        cellsQueue.add(this)
                    }
                }
            }
        }

        private fun drawPath(cell: Cell) {
            if (cell.type != ENTER) {
                with (cell.getNeighbourCells<ReturningPathfinderDirection>()) {
                    val nextCell = this.minBy { it.depth }
                    if (cell.type != EXIT)
                        cell.type = PATH
                    drawPath(labyrinth[nextCell.row][nextCell.column])
                }
            }
        }
    }

    /**
     * Get the current state of the labyrinth
     * @return Labyrinth
     */
    fun getLabyrinth() = labyrinth.copyOf()

    private inline fun <reified T> Cell.getNeighbourCells(): Collection<Cell> =
        when (T::class) {
            PathfinderDirection::class -> {
                PathfinderDirection.values().filter {
                    it.predicate(labyrinth, this)
                }.map { labyrinth[this.row + it.vector.first][this.column + it.vector.second] }
            }
            ReturningPathfinderDirection::class -> {
                ReturningPathfinderDirection.values().filter {
                    it.predicate(labyrinth, this)
                }.map { labyrinth[this.row + it.vector.first][this.column + it.vector.second] }
            }
            else -> error("ERROR")
        }

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
