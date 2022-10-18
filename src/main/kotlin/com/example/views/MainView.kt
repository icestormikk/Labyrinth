package com.example.views

import com.example.utilities.ApplicationUtilities
import com.example.utilities.LabyrinthUtilities
import com.example.utilities.LabyrinthUtilities.getLabyrinth
import com.example.utilities.LabyrinthUtilities.Builder.labyrinthInitialization
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.*

private const val MIN_LABYRINTH_SIZE = 11
private const val CANVAS_SCALE_MULTIPLIER = 0.9
private const val DEFAULT_LABYRINTH_SIZE = 80
private const val CANVAS_CONTAINER_SCALE_MULTIPLIER = 2.0 / 3
private const val CONTROLS_CONTAINER_SCALE_MULTIPLIER = 1.0 / 3

class MainView : View("My View") {
    override val root: HBox by fxml()

    private val canvasContainer: FlowPane by fxid()
    private val controlsContainer: VBox by fxid()
    private val labyrinthWidth: TextField by fxid()
    private val labyrinthHeight: TextField by fxid()
    private val errors: Label by fxid()
    private val recreateLabyrinth: Button by fxid()

    lateinit var labyrinth: ResizableCanvas

    init {
        with (canvasContainer) {
            prefWidthProperty().bind(root.widthProperty().multiply(CANVAS_CONTAINER_SCALE_MULTIPLIER))
            prefHeightProperty().bind(root.heightProperty())
        }
        with (controlsContainer) {
            prefWidthProperty().bind(root.widthProperty().multiply(CONTROLS_CONTAINER_SCALE_MULTIPLIER))
            prefHeightProperty().bind(root.heightProperty())
        }
        labyrinthInitialization(80, 80)

        configureCanvas()
        configureRightMenu()
    }

    private fun configureRightMenu() {
        recreateLabyrinth.onLeftClick {
            if (labyrinthWidth.hasValidInput() && labyrinthHeight.hasValidInput()) {
                labyrinthInitialization(labyrinthWidth.text.toInt(), labyrinthHeight.text.toInt())
                errors.text = ""
                labyrinth.autosize()
            }
            else errors.text = "Некорректный ввод."
        }
    }

    private fun configureCanvas() {
        labyrinth = ResizableCanvas()
        ApplicationUtilities.initiateContext(labyrinth.graphicsContext2D)

        with (labyrinth) {
            widthProperty().bind(canvasContainer.widthProperty().multiply(CANVAS_SCALE_MULTIPLIER))
            heightProperty().bind(canvasContainer.heightProperty())
        }

        canvasContainer.children.add(labyrinth)
    }

    private fun TextField.hasValidInput(): Boolean =
        try {
            val context = this.text.toInt()
            context > MIN_LABYRINTH_SIZE
        } catch (_: NumberFormatException) {
            false
        }

    inner class ResizableCanvas: Canvas() {
        override fun isResizable(): Boolean =
            true

        override fun minWidth(height: Double): Double =
            1.0

        override fun minHeight(width: Double): Double =
            1.0

        override fun maxWidth(height: Double): Double =
            root.prefWidth

        override fun maxHeight(width: Double): Double =
            root.prefHeight

        override fun resize(width: Double, height: Double) {
            ApplicationUtilities.drawLabyrinthOnScreen(
                context,
                getLabyrinth()
            )
        }
    }
}
