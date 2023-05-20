import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import javafx.stage.Stage

const val WINDOW_WIDTH = 1920.0
const val WINDOW_HEIGHT = 1082.0

class SpaceInvaders : Application()
{
    override fun start(stage: Stage)
    {
        var game = Game()

        stage.scene = game.gameScene
        stage.icons.add(Image("icon.png"))
        //stage.isFullScreen = true
        //stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        stage.title = "Space Invaders"
        stage.isResizable = false
        stage.requestFocus()
        stage.show()
    }
}