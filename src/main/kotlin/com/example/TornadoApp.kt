package com.example

import com.example.views.MainView
import tornadofx.*

class TornadoApp: App(MainView::class) {
    fun main(args: Array<String>) {
        launch<TornadoApp>(args)
    }
}
