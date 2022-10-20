package com.example.utilities

import com.example.domain.Cell
import com.example.domain.CellType
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import kotlin.properties.Delegates

var EMPTY_CELL_COLOR: Color = Color.WHITE
var WALL_CELL_COLOR: Color = Color.BLACK
var VISITED_CELL_COLOR: Color = Color.LIGHTGRAY
var PATH_CELL_COLOR: Color = Color.RED
var ENTER_CELL_COLOR: Color = Color.web("#04a60b")
var EXIT_CELL_COLOR: Color = Color.web("#b60202")

/**
 * Functions for managing interface elements
 */
object ApplicationUtilities {
    private var CELL_SIZE by Delegates.notNull<Double>()
    private lateinit var graphicsContext: GraphicsContext

    /**
     * Canvas initialization (opens access to other object functions)
     * @param context the context of the canvas to be linked to
     */
    fun initiateContext(context: GraphicsContext) {
        graphicsContext = context
    }

    /**
     * Synchronize the state of the canvas inside the application with the state on the screen
     */
    fun updateCanvas() =
        graphicsContext.canvas.autosize()

    /**
     * Get the maze cell by cursor coordinates at the moment of clicking the mouse button
     * @param x the X-axis coordinate of the cursor
     * @param y the Y-axis coordinate of the cursor
     * @return the cell over which the cursor was located at the time of pressing
     * or null if the event occurred outside the maze
     */
    fun getCellByCoordinates(x: Double, y: Double): Cell? =
        if (x in 0.0..graphicsContext.canvas.height && y in 0.0..graphicsContext.canvas.height) {
            val rowIndex = (y / CELL_SIZE).toInt()
            val columnIndex = (x / CELL_SIZE).toInt()
            with (LabyrinthUtilities.getLabyrinth()) {
                if (rowIndex in 0 until this.size && columnIndex in 0 until this[0].size)
                    this[rowIndex][columnIndex]
                else null
            }
        } else null

    /**
     * Draws an image of the maze on the canvas (in its current state)
     * @param labyrinth the object to be drawn
     */
    fun drawLabyrinthOnScreen(
        labyrinth: Array<Array<Cell>>,
    ) {
        if (!this::graphicsContext.isInitialized)
            error("Graphics context is not initialized!")

        CELL_SIZE = if (labyrinth.size >= labyrinth[0].size) {
            minOf(graphicsContext.canvas.height, graphicsContext.canvas.width) / labyrinth.size
        }
        else {
            maxOf(graphicsContext.canvas.height, graphicsContext.canvas.width) / labyrinth[0].size
        }

        graphicsContext.clearRect(0.0,0.0, graphicsContext.canvas.width, graphicsContext.canvas.height)
        labyrinth.forEach { row ->
            row.forEach { cell ->
                graphicsContext.fill = when (cell.type) {
                    CellType.WALL -> Paint.valueOf(WALL_CELL_COLOR.toString())
                    CellType.VISITED -> Paint.valueOf(VISITED_CELL_COLOR.toString())
                    CellType.ENTER -> Paint.valueOf(ENTER_CELL_COLOR.toString())
                    CellType.EXIT -> Paint.valueOf(EXIT_CELL_COLOR.toString())
                    CellType.PATH -> Paint.valueOf(PATH_CELL_COLOR.toString())
                    else -> Paint.valueOf(EMPTY_CELL_COLOR.toString())
                }
                graphicsContext.fillRect(
                    cell.column * CELL_SIZE,
                    cell.row * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE
                )
            }
        }
    }
}
