package com.example.utilities

import com.example.domain.Cell
import com.example.domain.CellType
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint
import kotlin.properties.Delegates

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
                    CellType.WALL -> Paint.valueOf("#000000")
                    CellType.VISITED -> Paint.valueOf("#0000FF")
                    CellType.ENTER -> Paint.valueOf("#00FF00")
                    CellType.EXIT -> Paint.valueOf("#FF0000")
                    CellType.PATH -> Paint.valueOf("#FFFF00")
                    else -> Paint.valueOf("#FFFFFF")
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
