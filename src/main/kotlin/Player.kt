import javafx.geometry.Point2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import kotlinx.serialization.descriptors.PrimitiveKind

class Player {

    var xPos = 0.0
    var yPos = 0.0
    var width = 100.0
    var height = 100.0

    var godMode = false


    private var playerImage = Image("player.png")
    private var playerGodModeImage = Image("playerGodMode.png")

    var playerView = AnimatableImage(playerImage, playerImage.width/ scale, playerImage.height/ scale)

    init {
        width =  playerImage.width/ scale
        height = playerImage.height/ scale

        //playerView.fitWidth = width
        //playerView.fitHeight = height

        xPos = WINDOW_WIDTH / 2 - width/2
        yPos = WINDOW_HEIGHT * (90.0/100.0)

        playerView.layoutX = xPos
        playerView.layoutY = yPos
    }

    fun translate(tx: Double, ty: Double = 0.0 )
    {
        if ( xPos + tx - 20.0 > 0.0 && xPos + width + 20.0 + tx < WINDOW_WIDTH)
        {
            xPos += tx
            playerView.layoutX = xPos //- width/2
        }

        if ( yPos + ty > 0.0 && yPos + ty < WINDOW_HEIGHT)
        {
            yPos += ty
            playerView.layoutY = yPos //- height/2
        }
    }

    fun respawn()
    {
        xPos = WINDOW_WIDTH / 2 - width/2
        yPos = WINDOW_HEIGHT * (90.0/100.0)

        playerView.setPosition(xPos, yPos)
    }

    fun spawn(pane: Pane)
    {
        pane.children.add(playerView)
    }

    fun collides(point: Point2D) : Boolean
    {
        return point.x > xPos && point.x < xPos + width && point.y > yPos && point.y < yPos + height
    }

    fun collides(projectile: EnemyProjectile) : Boolean
    {
        if ( godMode )
        {
            return false
        }

        return collides(Point2D(projectile.xPos, projectile.yPos + height - 18.0)) || collides(Point2D(projectile.xPos, projectile.yPos ))
    }

    fun toggleGodMode(pane: Pane)
    {
        godMode = !godMode

        pane.children.remove(playerView)

        if ( godMode)
        {
            playerView = AnimatableImage(playerGodModeImage, playerImage.width/ scale, playerImage.height/ scale)
        }
        else
        {
            playerView = AnimatableImage(playerImage, playerImage.width/ scale, playerImage.height/ scale)
        }

        playerView.fitWidth = width
        playerView.fitHeight = height

        translate(0.0,0.0)



        pane.children.add(playerView)
    }

    fun destroy()
    {

    }
}
