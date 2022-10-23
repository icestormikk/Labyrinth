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
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*

private const val MIN_LABYRINTH_SIZE = 11
private const val CANVAS_SCALE_MULTIPLIER = 0.98
private const val DEFAULT_LABYRINTH_SIZE = 80
private const val CANVAS_CONTAINER_SCALE_MULTIPLIER = 2.0 / 3
private const val CONTROLS_CONTAINER_SCALE_MULTIPLIER = 1.0 / 3

private const val LOGO_PATH = "com/example/static/icons/application_logo.png"

/**
 * А class containing the business logic of the application
 * @property root root container of the main window
 * @property canvasContainer canvas storage container
 * @property controlsContainer a container for storing input tools located on the right side of the application
 * @property labyrinthWidth text field for changing the width of the maze (in cells)
 * @property labyrinthHeight text field for changing the height of the maze (in cells)
 * @property errors label for notification of input errors
 * @property enterCellOutput a label for notifying the user about successful or unsuccessful processing
 * of the coordinates of the initial cell
 * @property exitCellOutput a label for notifying the user about successful or unsuccessful processing
 * of the coordinates of the finish cell
 * @property recreateLabyrinth button to build a new maze with updated parameters
 * @property passLabyrinth the button to start the process of passing the maze
 * @property emptyCellColor field for selecting the color of cells with the EMPTY type
 * @property wallCellColor field for selecting the color of cells with the WALL type
 * @property visitedCellColor field for selecting the color of cells with the VISITED type
 * @property pathCellColor field for selecting the color of cells with the PATH type
 * @property acceptNewColors button for updating cell colors
 * @property clearLabyrinth the button to return the maze to the "not passed" state
 * @property serviceButtonsCordsAccept button for updating the coordinates of the start and end points
 * (coordinates are set via fields)
 * @property enterCellX x-coordinate of the start point
 * @property enterCellY y-coordinate of the start point
 * @property exitCellX x-coordinate of the finish point
 * @property exitCellY y-coordinate of the finish point
 * @property labyrinth canvas for drawing a maze
 */
class MainView : View("Labyrinth") {
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
        currentStage?.icons?.add(Image(LOGO_PATH))
        currentStage?.apply {
            minHeight = 800.0; minWidth = 1200.0
        }

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
                    if (!LabyrinthUtilities.IS_SOLVED) {
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
                }
                MouseButton.SECONDARY -> {
                    if (!LabyrinthUtilities.IS_SOLVED) {
                        with(ApplicationUtilities.getCellByCoordinates(it.x, it.y)) {
                            if (this != null) {
                                if (this.type == CellType.EMPTY) {
                                    exitCellOutput.text = ""
                                    LabyrinthUtilities.Pathfinder.setExitCell(this.row, this.column)
                                    exitCellX.text = "${this.column}"; exitCellY.text = "${this.row}"
                                    ApplicationUtilities.updateCanvas()
                                } else exitCellOutput.coloredMessage("Недопустимая клетка.", Color.RED)
                            }
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

    @SuppressWarnings("ComplexMethod")
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
                configureTextFields(); configureRightMenu()
                passLabyrinth.isDisable = false
            }
            else errors.text = "Некорректный ввод."
        }
        passLabyrinth.onLeftClick {
            with (LabyrinthUtilities.Pathfinder) {
                passLabyrinth()
            }
            ApplicationUtilities.updateCanvas()
            passLabyrinth.isDisable = true
        }
        clearLabyrinth.onLeftClick {
            LabyrinthUtilities.Builder.clearLabyrinth()
            ApplicationUtilities.updateCanvas()
            passLabyrinth.isDisable = false
        }
        serviceButtonsCordsAccept.onLeftClick {
            fun Pair<String, String>.isValid(): Boolean =
                try {
                    val row = first.toInt(); val column = second.toInt()
                    row > 0 && row < getLabyrinth().size - 1
                            && column > 0 && column < getLabyrinth()[0].size - 1
                            && getLabyrinth()[row][column].type != CellType.WALL
                } catch (_: NumberFormatException) {
                    false
                }

            with (Pair(enterCellX.text, enterCellY.text)) {
                if (!LabyrinthUtilities.IS_SOLVED) {
                    if (isValid()) {
                        enterCellOutput.text = ""
                        LabyrinthUtilities.Pathfinder.setEnterCell(
                            enterCellY.text.toInt(), enterCellX.text.toInt()
                        )
                    }
                    else enterCellOutput.coloredMessage("Введены недопустимые значения!", Color.RED)
                } else enterCellOutput.coloredMessage(
                    "Вы не можете изменять клетки в решённом лабиринте.", Color.RED
                )
            }
            with (Pair(exitCellX.text, exitCellY.text)) {
                if (!LabyrinthUtilities.IS_SOLVED) {
                    if (isValid()) {
                        exitCellOutput.text = ""
                        LabyrinthUtilities.Pathfinder.setExitCell(
                            exitCellY.text.toInt(), exitCellX.text.toInt()
                        )
                    } else exitCellOutput.coloredMessage("Введены недопустимые значения!", Color.RED)
                } else exitCellOutput.coloredMessage(
                    "Вы не можете изменять клетки в решённом лабиринте.", Color.RED
                )
            }
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

    private fun Label.coloredMessage(message: String, color: Color) {
        text = message; textFill = color
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
