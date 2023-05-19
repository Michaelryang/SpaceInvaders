import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

const val playerProjectileSpeed = 30.0
const val enemyProjectileSpeed = 3.0


var rainbowImages = arrayOf(
    (Image("playerprojectilerainbow1.png")),
    (Image("playerprojectilerainbow2.png")),
    (Image("playerprojectilerainbow3.png")),
    (Image("playerprojectilerainbow4.png")),
    (Image("playerprojectilerainbow5.png")),
    (Image("playerprojectilerainbow6.png")),
    (Image("playerprojectilerainbow7.png")))

interface Projectile
{
    var xPos : Double
    var yPos : Double
    var width : Double
    var height : Double
    fun move()

    fun outOfBounds() : Boolean
}

class PlayerProjectile(x: Double, y:Double, color: Int = -1, tilt: Double = 0.0) : Projectile
{
    override var xPos = 0.0
    override var yPos = 0.0
    override var width = 0.0
    override var height = 0.0
    var xTilt = tilt
    var padding = 0.0

    var projectileImage = Image("playerprojectile1.png")
    var projectileView = ImageView( projectileImage)

    init {
        xPos = x - padding/2.0
        yPos = y

        if ( color != -1)
        {
            projectileView = ImageView(rainbowImages[color])
        }

        width = projectileImage.width/ scale + padding
        height = projectileImage.height/ scale

        projectileView.fitWidth = width - padding
        projectileView.fitHeight = height

        projectileView?.layoutX = xPos + padding/2.0
        projectileView?.layoutY = yPos
    }

    override fun move()
    {
        yPos -= playerProjectileSpeed
        projectileView?.layoutY = yPos

        xPos -= xTilt
        projectileView?.layoutX = xPos
    }

    fun spawn(pane: Pane)
    {
        pane.children.add(projectileView)
    }

    fun getPointOfCollision() : Point2D
    {
        return Point2D(xPos + width/2, yPos)
    }

    fun destroy(pane: Pane)
    {
        pane.children.remove(projectileView)
        //projectileView = null
    }

    override fun outOfBounds(): Boolean {
        return xPos + width < 0.0 || xPos > WINDOW_WIDTH || yPos + height < 0.0 || yPos > WINDOW_HEIGHT
    }
}

var enemyProjectileImages = arrayOf((Image("enemyprojectile1.png")),
    (Image("enemyprojectile2.png")),
    (Image("enemyprojectile3.png")),
    (Image("enemyprojectile4.png")),
    (Image("enemyprojectile5.png")))

class EnemyProjectile(x: Double, y:Double) : Projectile {
    override var xPos = 0.0
    override var yPos = 0.0
    override var width = 0.0
    override var height = 0.0

    var projectileView : AnimatableImage? = null

    init {
        xPos = x
        yPos = y

        var selectedProjectileImageIndex = (0 until enemyProjectileImages.size).random()

        width = enemyProjectileImages[selectedProjectileImageIndex].width/ scale
        height = enemyProjectileImages[selectedProjectileImageIndex].height/ scale

        projectileView = AnimatableImage(enemyProjectileImages[selectedProjectileImageIndex], width, height)
        projectileView!!.setPosition(xPos, yPos)
    }

    override fun move() {
        yPos += enemyProjectileSpeed * enemySpeedModifier
        projectileView!!.setPosition(xPos, yPos)
    }

    fun spawn(pane: Pane) {
        pane.children.add(projectileView)
    }

    fun getPointOfCollision() : Point2D
    {
        return Point2D(xPos + width/2, yPos + height)
    }


    fun destroy(pane: Pane)
    {
        pane.children.remove(projectileView)
        //enemyImage = null
    }

    override fun outOfBounds(): Boolean {
        return xPos + width < 0.0 || xPos > WINDOW_WIDTH || yPos + height < 0.0 || yPos > WINDOW_HEIGHT
    }


}
