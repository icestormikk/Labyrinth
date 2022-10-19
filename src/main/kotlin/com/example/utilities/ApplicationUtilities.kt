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

object ApplicationUtilities {
    private var CELL_SIZE by Delegates.notNull<Double>()
    private lateinit var graphicsContext: GraphicsContext

    fun initiateContext(context: GraphicsContext) {
        graphicsContext = context
    }

    fun updateCanvas() =
        graphicsContext.canvas.autosize()

    fun drawLabyrinthOnScreen(
        labyrinth: Array<Array<Cell>>,
    ) {
        if (!this::graphicsContext.isInitialized)
            error("Graphics context is not initialized!")

        CELL_SIZE = if (labyrinth.size > labyrinth[0].size) {
            minOf(graphicsContext.canvas.height, graphicsContext.canvas.width) / labyrinth.size
        }
        else {
            minOf(graphicsContext.canvas.height, graphicsContext.canvas.width) / labyrinth[0].size
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
