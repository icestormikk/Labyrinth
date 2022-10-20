package com.example.domain

import com.example.configuration.DIRECTION_DOWN_CODE
import com.example.configuration.DIRECTION_LEFT_CODE
import com.example.configuration.DIRECTION_RIGHT_CODE
import com.example.configuration.DIRECTION_UP_CODE
import com.example.configuration.ALLOWED_CELL_TYPES

/**
 * Directions along which the pathfinder can move
 * @property code direction identification number
 * @property vector a vector indicating which cell relative to the current one the pathfinder will move to
 * @property predicate checking whether this movement is possible
 */
enum class PathfinderDirection(
    val code: Int,
    val vector: Pair<Int, Int>,
    val predicate: (Array<Array<Cell>>, Cell) -> Boolean
) {
    LEFT(
        DIRECTION_LEFT_CODE,
        Pair(0, -1),
        { matrix, cell ->
            cell.column - 1 > 0 && matrix[cell.row][cell.column - 1].type in ALLOWED_CELL_TYPES
        }
    ),
    UP(
        DIRECTION_UP_CODE,
        Pair(-1, 0),
        { matrix, cell ->
            cell.row - 1 > 0 && matrix[cell.row - 1][cell.column].type in ALLOWED_CELL_TYPES
        }
    ),
    RIGHT(
        DIRECTION_RIGHT_CODE,
        Pair(0, 1),
        { matrix, cell ->
            cell.column + 1 < matrix[0].size - 1 &&
                    matrix[cell.row][cell.column + 1].type  in ALLOWED_CELL_TYPES
        }
    ),
    DOWN(
        DIRECTION_DOWN_CODE,
        Pair(1, 0),
        { matrix, cell ->
            cell.row + 1 < matrix.size - 1 && matrix[cell.row + 1][cell.column].type in ALLOWED_CELL_TYPES
        }
    )
}
