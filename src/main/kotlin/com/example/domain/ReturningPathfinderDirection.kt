package com.example.domain

import com.example.configuration.DIRECTION_DOWN_CODE
import com.example.configuration.DIRECTION_LEFT_CODE
import com.example.configuration.DIRECTION_RIGHT_CODE
import com.example.configuration.DIRECTION_UP_CODE

enum class ReturningPathfinderDirection(
    val code: Int,
    val vector: Pair<Int, Int>,
    val predicate: (Array<Array<Cell>>, Cell) -> Boolean
) {
    RETURNING_LEFT(
        DIRECTION_LEFT_CODE,
        Pair(0, -1),
        { matrix, cell ->
            cell.column - 1 > 0 && matrix[cell.row][cell.column - 1].type in
                    com.example.configuration.RETURNING_ALLOWED_CELL_TYPES
        }
    ),
    RETURNING_UP(
        DIRECTION_UP_CODE,
        Pair(-1, 0),
        { matrix, cell ->
            cell.row - 1 > 0 && matrix[cell.row - 1][cell.column].type in
                    com.example.configuration.RETURNING_ALLOWED_CELL_TYPES
        }
    ),
    RETURNING_RIGHT(
        DIRECTION_RIGHT_CODE,
        Pair(0, 1),
        { matrix, cell ->
            cell.column + 1 < matrix[0].size - 1 && matrix[cell.row][cell.column + 1].type in
                    com.example.configuration.RETURNING_ALLOWED_CELL_TYPES
        }
    ),
    RETURNING_DOWN(
        DIRECTION_DOWN_CODE,
        Pair(1, 0),
        { matrix, cell ->
            cell.row + 1 < matrix.size - 1 && matrix[cell.row + 1][cell.column].type in
                    com.example.configuration.RETURNING_ALLOWED_CELL_TYPES
        }
    )
}
