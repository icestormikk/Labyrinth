package com.example.domain

/**
 * Cell type. its function and role depend on the type of cell
 * @property code unique cell type number
 */
enum class CellType(val code: Int) {
    /**
     * An empty cage. You can pass through it freely
     */
    EMPTY(0),

    /**
     * Wall. It is impossible to pass through this cell
     */
    WALL(1),

    /**
     * This type means that pathfinder has already passed through this cell
     */
    VISITED(2),

    /**
     * The cell is the starting cell for pathfinder
     */
    ENTER(3),

    /**
     * In this cell pathfinder ends its journey
     */
    EXIT(4),

    /**
     * This cell is part of the optimal path from the [ENTER] to the [EXIT]
     */
    PATH(5)
}
