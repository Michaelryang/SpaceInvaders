import javafx.geometry.Point2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import kotlinx.serialization.descriptors.PrimitiveKind

const val columnsOfEnemies = 10
const val rowsOfEnemies = 5
const val enemyMovementSpeed = 1.8
const val enemyMoveDownDistance = 30.0

const val matrixXGap = 90.0
const val matrixYGap = 60.0

const val topGap = 120.0


class EnemyMatrix {
    private var matrix: Array<Array<Enemy?>> =
        Array(columnsOfEnemies) { col -> Array(rowsOfEnemies) { row -> Enemy(col, row, row) } }

    private var xMovementSign = 0.0
    private var yMovement = 0.0

    var numEnemies = 0

    init {
        xMovementSign = 1.0
        yMovement = 0.0

        numEnemies = columnsOfEnemies * rowsOfEnemies
    }

    fun inAnim() : Boolean
    {
        for ( i in 0..matrix.lastIndex )
        {
            for ( j in 0..matrix[i].lastIndex)
            {
                if ( matrix[i][j]!!.enemyImage!!.inAnimation )
                {
                    return true
                }
            }
        }

        return false
    }

    fun stepFrame()
    {
        for ( i in 0..matrix.lastIndex )
        {
            for ( j in 0..matrix[i].lastIndex)
            {
                matrix[i][j]?.stepFrame()
            }
        }
    }

    fun popIn(frameLength: Double, stagger: Double)
    {
        for ( i in 0..matrix.lastIndex )
        {
            for ( j in 0..matrix[i].lastIndex)
            {
                matrix[i][j]!!.popIn(frameLength)
                matrix[i][j]!!.setAnimDelay(stagger*(i+j*matrix.size))
            }
        }
    }

    fun clear(pane: Pane)
    {
        for ( enemyRow in matrix )
        {
            for ( enemy in enemyRow )
            {
                if (enemy != null) {
                    //pane.children.remove(enemy.enemyImage)

                    enemy.destroy(pane)
                }
            }

        }
    }

    private fun contactingLeftWall() : Boolean

    {
        for ( enemyRow in matrix )
        {
            for ( enemy in enemyRow )
            {
                if (enemy != null) {
                    if ( enemy.contactingLeftWall()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun contactingRightWall() : Boolean
    {
        for ( enemyRow in matrix )
        {
            for ( enemy in enemyRow )
            {
                if (enemy != null) {
                    if ( enemy.contactingRightWall()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun spawnProjectile(pane: Pane) : EnemyProjectile?
    {
        if ( numEnemies == 0)
        {
            return null
        }

        var random = (1 .. numEnemies).random()


        while ( random > 0 )
        {
            for (enemyRow in matrix) {
                for (enemy in enemyRow) {
                    if ( enemy != null )
                    {
                        random -= 1
                        if ( random == 0)
                        {
                            return enemy.spawnProjectile(pane)
                        }
                    }
                }
            }
        }


        println("BIG PROBLEM")
        return null
    }

    fun checkPlayerCollision(player: Player) : Boolean
    {
        for (enemyRow in matrix) {
            for (enemy in enemyRow) {
                if ( enemy != null )
                {
                    if ( enemy.collides(player))
                    {
                        return true
                    }
                }

            }
        }

        return false
    }

    fun checkPlayerProjectileCollision(projectile: PlayerProjectile, pane: Pane) : Boolean
    {
        for (enemyRow in matrix) {
            for (enemy in enemyRow) {
                if ( enemy != null )
                {
                    if ( enemy.collides(projectile))
                    {
                        enemy.destroy(pane)
                        matrix[enemy.xIndex][enemy.yIndex] = null
                        numEnemies -= 1

                        projectile.destroy(pane)

                        return true
                    }
                }

            }
        }

        return false
    }

    fun move(pane: Pane) : EnemyProjectile?
    {
        var projectile : EnemyProjectile? = null


        if ( yMovement > 0.0 )
        {
            yMovement -= enemyMovementSpeed * enemySpeedModifier
        }
        else
        {
            for (enemyRow in matrix)
            {
                for ( enemy in enemyRow)
                {
                    if ( enemy != null && enemy.contactingRightWall() && xMovementSign == 1.0)
                    {
                        xMovementSign = -1.0
                        yMovement = enemyMoveDownDistance

                        projectile = spawnProjectile(pane)

                    }
                    else if ( enemy != null && enemy.contactingLeftWall() && xMovementSign == -1.0 )
                    {
                        xMovementSign = 1.0
                        yMovement = enemyMoveDownDistance
                        projectile = spawnProjectile(pane)

                    }
                }
            }
        }


        for (enemyRow in matrix)
        {
            for (enemy in enemyRow)
            {
                if ( yMovement > 0.0)
                {
                    enemy?.move(0.0, enemyMovementSpeed * enemySpeedModifier)
                }
                else
                {
                    enemy?.move(enemyMovementSpeed*xMovementSign * enemySpeedModifier, 0.0)
                }
            }
        }

        return projectile
    }

    fun respawn()
    {
        matrix = Array(columnsOfEnemies) { col -> Array(rowsOfEnemies) { row -> Enemy(col, row, row) } }

        xMovementSign = 1.0
        yMovement = 0.0

        numEnemies = columnsOfEnemies * rowsOfEnemies

        println(matrix)
        println(numEnemies)
    }

    fun spawn(pane: Pane)
    {
        for (enemyRow in matrix)
        {
            for ( enemy in enemyRow)
            {
                enemy?.spawn(pane)
            }
        }
    }
}

var invaders = arrayOf((Image("invader1.png")),
    (Image("invader2.png")),
    (Image("invader3.png")),
    (Image("invader4.png")),
    (Image("invader5.png")))

class Enemy(x: Int, y: Int, t: Int) {

    var xIndex = x // row
    var yIndex = y // col

    var width = 0.0
    var height = 0.0

    var xPos = 0.0
    var yPos = 0.0

    var enemyImage : AnimatableImage? = null
    var invaderType = t


    init {

        width = invaders[invaderType].width / scale
        height = invaders[invaderType].height / scale

        xPos = xIndex * matrixXGap + ( matrixXGap - width)/2.0
        yPos = yIndex * matrixYGap + topGap

        enemyImage = AnimatableImage(invaders[invaderType], width, height)
        //enemyImage!!.fitHeight = height
        //enemyImage!!.fitWidth = width


        enemyImage?.setPosition(xPos, yPos)
        //enemyImage?.layoutX = xPos
        //enemyImage?.layoutY = yPos
    }

    fun contactingLeftWall() : Boolean {
        return xPos - 20.0 <= 0.0
    }

    fun contactingRightWall() : Boolean {
        return  xPos + width + 20.0 >= WINDOW_WIDTH
    }

    fun move(tx: Double, ty: Double)
    {
        xPos += tx
        yPos += ty

        enemyImage!!.setPosition(xPos, yPos)
        //enemyImage?.layoutX = xPos
        //enemyImage?.layoutY = yPos
    }

    fun spawnProjectile(pane: Pane) : EnemyProjectile
    {
        var projectile = EnemyProjectile(xPos + width/ 2,  yPos + height )
        projectile.spawn(pane)

        return projectile
    }

    fun spawn(pane: Pane)
    {
        pane.children.add(enemyImage)
    }

    fun collides(player: Player) : Boolean
    {
        return collides(player.xPos, player.yPos, player.width, player.height)
    }

    fun collides(projectile: Projectile) : Boolean
    {
        return collides(projectile.xPos, projectile.yPos, projectile.width, projectile.height)
    }

    fun collides(point: Point2D) : Boolean
    {
        return point.x > xPos && point.x < xPos + width && point.y > yPos && point.y < yPos + height
    }

    fun collides(x: Double, y: Double, w: Double, h: Double ) : Boolean
    {
        return xPos < x + w &&
                xPos + width > x &&
                yPos < y + h &&
                height + yPos > y
    }

    fun destroy(pane: Pane)
    {
        pane.children.remove(enemyImage)
        //enemyImage = null
    }

    fun popIn(frameLength: Double)
    {
        enemyImage!!.popIn(frameLength)

    }

    fun setAnimDelay(frameDelay: Double)
    {
        enemyImage!!.setDelay(frameDelay)
    }

    fun stepFrame()
    {
        enemyImage!!.stepFrame()
    }
}