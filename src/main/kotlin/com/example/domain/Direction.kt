package com.example.domain

private const val DISTANCE = 2
private const val DIRECTION_LEFT_CODE = 1
private const val DIRECTION_UP_CODE = 2
private const val DIRECTION_RIGHT_CODE = 3
private const val DIRECTION_DOWN_CODE = 4

enum class Direction(
    val code: Int,
    val vector: Pair<Int, Int>,
    val predicate: (Array<Array<Cell>>, Cell) -> Boolean
) {
    LEFT(
        DIRECTION_LEFT_CODE,
        Pair(-DISTANCE, 0),
        { matrix, cell ->
            cell.x - DISTANCE > 0 && matrix[cell.x - DISTANCE][cell.y].type == CellType.EMPTY
        }
    ),
    UP(
        DIRECTION_UP_CODE,
        Pair(0, -DISTANCE),
        { matrix, cell ->
            cell.y - DISTANCE > 0 && matrix[cell.x][cell.y - DISTANCE].type == CellType.EMPTY
        }
    ),
    RIGHT(
        DIRECTION_RIGHT_CODE,
        Pair(DISTANCE, 0),
        { matrix, cell ->
            cell.x + DISTANCE < matrix.size && matrix[cell.x + DISTANCE][cell.y].type == CellType.EMPTY
        }
    ),
    DOWN(
        DIRECTION_DOWN_CODE,
        Pair(0, DISTANCE),
        { matrix, cell ->
            cell.y + DISTANCE < matrix[0].size - 1 && matrix[cell.x][cell.y + DISTANCE].type == CellType.EMPTY
        }
    )
}
