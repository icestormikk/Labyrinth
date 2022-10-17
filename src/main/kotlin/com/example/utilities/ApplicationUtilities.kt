package com.example.utilities

import com.example.domain.Cell
import com.example.domain.CellType
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint
import java.awt.Color
import kotlin.properties.Delegates

object ApplicationUtilities {
    private var CELL_SIZE by Delegates.notNull<Double>()

    fun drawLabyrinthOnScreen(
        context: GraphicsContext,
        labyrinth: Array<Array<Cell>>,
    ) {
        CELL_SIZE = if (labyrinth.size > labyrinth[0].size) {
            println("${labyrinth.size}, ${labyrinth[0].size}")
            minOf(context.canvas.height, context.canvas.width) / labyrinth.size
        }
        else {
            println("SECOND")
            minOf(context.canvas.height, context.canvas.width) / labyrinth[0].size
        }

        context.clearRect(0.0,0.0, context.canvas.width, context.canvas.height)
        labyrinth.forEach { row ->
            row.forEach { cell ->
                context.fill = when (cell.type) {
                    CellType.WALL -> Paint.valueOf("#000000")
                    else -> Paint.valueOf("#EFEFEF")
                }
                context.fillRect(
                    cell.x * CELL_SIZE,
                    cell.y * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE
                )
            }
        }
    }
}
