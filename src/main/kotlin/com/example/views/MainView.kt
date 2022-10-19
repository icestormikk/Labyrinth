package com.example.views

import com.example.domain.CellType
import com.example.utilities.ApplicationUtilities
import com.example.utilities.EMPTY_CELL_COLOR
import com.example.utilities.LabyrinthUtilities
import com.example.utilities.LabyrinthUtilities.getLabyrinth
import com.example.utilities.LabyrinthUtilities.Builder.labyrinthInitialization
import com.example.utilities.PATH_CELL_COLOR
import com.example.utilities.VISITED_CELL_COLOR
import com.example.utilities.WALL_CELL_COLOR
import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.MouseButton
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
    private val enterCellOutput: Label by fxid()
    private val exitCellOutput: Label by fxid()
    private val recreateLabyrinth: Button by fxid()
    private val passLabyrinth: Button by fxid()
    private val emptyCellColor: ColorPicker by fxid()
    private val wallCellColor: ColorPicker by fxid()
    private val visitedCellColor: ColorPicker by fxid()
    private val pathCellColor: ColorPicker by fxid()
    private val acceptNewColors: Button by fxid()
    private val clearLabyrinth: Button by fxid()
    private val serviceButtonsCordsAccept: Button by fxid()
    private val enterCellX: TextField by fxid()
    private val enterCellY: TextField by fxid()
    private val exitCellX: TextField by fxid()
    private val exitCellY: TextField by fxid()

    private lateinit var labyrinth: ResizableCanvas

    init {
        with (canvasContainer) {
            prefWidthProperty().bind(root.widthProperty().multiply(CANVAS_CONTAINER_SCALE_MULTIPLIER))
            prefHeightProperty().bind(root.heightProperty())
        }
        with (controlsContainer) {
            prefWidthProperty().bind(root.widthProperty().multiply(CONTROLS_CONTAINER_SCALE_MULTIPLIER))
            prefHeightProperty().bind(root.heightProperty())
        }
        labyrinthInitialization(DEFAULT_LABYRINTH_SIZE, DEFAULT_LABYRINTH_SIZE)

        configureCanvas()
        configureRightMenu()
        configureTextFields()
        configureColorPickers()
        configureLabyrinthHandlers()
    }

    private fun configureLabyrinthHandlers() {
        labyrinth.onMouseClicked = EventHandler {
            when (it.button) {
                MouseButton.PRIMARY -> {
                    with(ApplicationUtilities.getCellByCoordinates(it.x, it.y)) {
                        if (this != null) {
                            if (this.type == CellType.EMPTY) {
                                enterCellOutput.text = ""
                                LabyrinthUtilities.Pathfinder.setEnterCell(this.row, this.column)
                                enterCellX.text = "${this.column}"; enterCellY.text = "${this.row}"
                                ApplicationUtilities.updateCanvas()
                            } else enterCellOutput.coloredMessage("Недопустимая клетка.", Color.RED)
                        }
                    }
                }

                MouseButton.SECONDARY -> {
                    with(ApplicationUtilities.getCellByCoordinates(it.x, it.y)) {
                        if (this != null) {
                            if (this.type == CellType.EMPTY) {
                                enterCellOutput.text = ""
                                LabyrinthUtilities.Pathfinder.setExitCell(this.row, this.column)
                                exitCellX.text = "${this.column}"; exitCellY.text = "${this.row}"
                                ApplicationUtilities.updateCanvas()
                            } else exitCellOutput.coloredMessage("Недопустимая клетка.", Color.RED)
                        }
                    }
                }

                else -> {
                    // ignore
                }
            }
        }
    }

    private fun configureColorPickers() {
        mapOf(
            emptyCellColor to EMPTY_CELL_COLOR,
            wallCellColor to WALL_CELL_COLOR,
            visitedCellColor to VISITED_CELL_COLOR,
            pathCellColor to PATH_CELL_COLOR
        ).forEach { (colorPicker, color) ->
            colorPicker.value = color
        }

        acceptNewColors.onLeftClick {
            EMPTY_CELL_COLOR = emptyCellColor.value
            WALL_CELL_COLOR = wallCellColor.value
            VISITED_CELL_COLOR = visitedCellColor.value
            PATH_CELL_COLOR = pathCellColor.value
            ApplicationUtilities.updateCanvas()
        }
    }
    private fun configureTextFields() {
        labyrinthWidth.text = "${getLabyrinth()[0].size}"
        labyrinthHeight.text = "${getLabyrinth().size}"
    }

    private fun configureRightMenu() {
        with (LabyrinthUtilities.Pathfinder.START_CELL) {
            enterCellX.text = "$column"; enterCellY.text = "$row"
        }
        with (LabyrinthUtilities.Pathfinder.EXIT_CELL) {
            exitCellX.text = "$column"; exitCellY.text = "$row"
        }

        recreateLabyrinth.onLeftClick {
            if (labyrinthWidth.hasValidInput() && labyrinthHeight.hasValidInput()) {
                labyrinthInitialization(labyrinthWidth.text.toInt(), labyrinthHeight.text.toInt())
                errors.text = ""
                labyrinth.autosize()
                configureTextFields()
            }
            else errors.text = "Некорректный ввод."
        }
        passLabyrinth.onLeftClick {
            with (LabyrinthUtilities.Pathfinder) {
                passLabyrinth(START_CELL)
            }
            ApplicationUtilities.updateCanvas()
        }
    }

    private fun configureCanvas() {
        labyrinth = ResizableCanvas()
        ApplicationUtilities.initiateContext(labyrinth.graphicsContext2D)

        with (labyrinth) {
            widthProperty().bind(canvasContainer.widthProperty().multiply(CANVAS_SCALE_MULTIPLIER))
            heightProperty().bind(canvasContainer.heightProperty().multiply(CANVAS_SCALE_MULTIPLIER))
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
            ApplicationUtilities.drawLabyrinthOnScreen(getLabyrinth())
        }
    }
}
