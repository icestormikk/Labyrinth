package com.example.domain

import com.example.configuration.DIRECTION_DOWN_CODE
import com.example.configuration.DIRECTION_LEFT_CODE
import com.example.configuration.DIRECTION_RIGHT_CODE
import com.example.configuration.DIRECTION_UP_CODE
import com.example.configuration.ALLOWED_CELL_TYPES

private const val DISTANCE = 2

/**
 * Directions along which the maze builder can move
 * @property code direction identification number
 * @property vector a vector indicating which cell relative to the current one the builder will move to
 * @property predicate checking whether this movement is possible
 */
enum class BuilderDirection(
    val code: Int,
    val vector: Pair<Int, Int>,
    val predicate: (Array<Array<Cell>>, Cell) -> Boolean
) {
    LEFT(
        DIRECTION_LEFT_CODE,
        Pair(0, -DISTANCE),
        { matrix, cell ->
            cell.column - DISTANCE > 0 && matrix[cell.row][cell.column - DISTANCE].type in ALLOWED_CELL_TYPES
        }
    ),
    UP(
        DIRECTION_UP_CODE,
        Pair(-DISTANCE, 0),
        { matrix, cell ->
            cell.row - DISTANCE > 0 && matrix[cell.row - DISTANCE][cell.column].type in ALLOWED_CELL_TYPES
        }
    ),
    RIGHT(
        DIRECTION_RIGHT_CODE,
        Pair(0, DISTANCE),
        { matrix, cell ->
            cell.column + DISTANCE < matrix[0].size - 1 &&
                    matrix[cell.row][cell.column + DISTANCE].type in ALLOWED_CELL_TYPES
        }
    ),
    DOWN(
        DIRECTION_DOWN_CODE,
        Pair(DISTANCE, 0),
        { matrix, cell ->
            cell.row + DISTANCE < matrix.size - 1 && matrix[cell.row + DISTANCE][cell.column].type in ALLOWED_CELL_TYPES
        }
    )
}
