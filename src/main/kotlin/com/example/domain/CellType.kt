package com.example.domain

enum class CellType(val code: Int) {
    EMPTY(0),
    WALL(1),
    VISITED(2),
    ENTER(3),
    EXIT(4),
    PATH(5)
}
