package com.example.configuration

import com.example.domain.CellType

const val DIRECTION_LEFT_CODE = 1
const val DIRECTION_UP_CODE = 2
const val DIRECTION_RIGHT_CODE = 3
const val DIRECTION_DOWN_CODE = 4

/**
 * Types of cells through which movement is possible
 */
val ALLOWED_CELL_TYPES = arrayOf(CellType.EMPTY)

/**
 * Types of cells that can be moved during the return to the starting point
 */
val RETURNING_ALLOWED_CELL_TYPES = arrayOf(CellType.VISITED, CellType.ENTER)
/**
 * Immutable on [CellType.VISITED] cell types
 */
val SERVICE_CELL_TYPES = arrayOf(CellType.ENTER, CellType.EXIT, CellType.WALL)
