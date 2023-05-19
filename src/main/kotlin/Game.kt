import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.media.AudioClip
import kotlin.random.Random
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaPlayer.INDEFINITE
import javafx.scene.paint.Color
import java.io.File

// from jeff avery timer git repo code example
fun animationTimer(aniScene: AnimatableScene) {

    // create timer using JavaFX 60FPS execution thread
    val timer: AnimationTimer = object : AnimationTimer() {
        override fun handle(now: Long) {
            aniScene.frameCount += 1.0       // animate parameter
            aniScene.draw()         // redraw updated scene
        }
    }
    // start timer
    timer.start()
}

// from jeff avery timer git repo code example
interface AnimatableScene
{
    fun draw()

    var frameCount : Double
}

const val fireRateFrameDelay = 30.0
const val playerMovementSpeed = 10.0
const val RANDOM_ENEMY_FIRE_TIMER = 120.0

const val scale = 4.0
var playerFireSound = AudioClip(File("${System.getProperty("user.dir")}/src/main/resources/playerfire.mp3").toURI().toString())//Media(File("${System.getProperty("user.dir")}/src/main/resources/playerfire.mp3").toURI().toString())
var enemyFireSound = AudioClip(File("${System.getProperty("user.dir")}/src/main/resources/enemyfire.mp3").toURI().toString())
var enemyDeathSound = AudioClip(File("${System.getProperty("user.dir")}/src/main/resources/enemydeath.mp3").toURI().toString())
var playerDeathSound = AudioClip(File("${System.getProperty("user.dir")}/src/main/resources/death.mp3").toURI().toString())
var victorySound = AudioClip(File("${System.getProperty("user.dir")}/src/main/resources/victory.mp3").toURI().toString())
var optionSelectedSound = AudioClip(File("${System.getProperty("user.dir")}/src/main/resources/startgame.mp3").toURI().toString())

var youWinSound = AudioClip(File("${System.getProperty("user.dir")}/src/main/resources/youwin.mp3").toURI().toString())
var youLoseSound = AudioClip(File("${System.getProperty("user.dir")}/src/main/resources/youlose.mp3").toURI().toString())

var menuSong = Media(File("${System.getProperty("user.dir")}/src/main/resources/menusong.mp3").toURI().toString())
var gameSong = Media(File("${System.getProperty("user.dir")}/src/main/resources/gamesong.mp3").toURI().toString())
var menuSongPlayer = MediaPlayer(menuSong)
var gameSongPlayer = MediaPlayer(gameSong)

var enemySpeedModifier = 1.0

var playerIsDying = false
var playerIsRespawning = false

class Game : AnimatableScene {

    var gamePane = Pane()
    var gameScene = Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT)

    //private var labelScore = Text("Score: 0")
    //private var labelLevel = Text("Level: ")
    //private var labelLives = Text("Lives: ")
    //var labelInstructions = Label("Enter: Start Game\nA/D: Move Left/Right\nSpace: Fire\nQ: Quit Game\n1/2/3: Start Game At Level")

    var titleImage = Image("titlelogo.png")
    var title = AnimatableImage(titleImage)

    var invaderImage = Image("titleinvader.png")
    var titleInvader = AnimatableImage(invaderImage)

    var menuTextImage = Image("menutext.png")
    var titleMenuText = AnimatableImage(menuTextImage)

    var nameIdImage = Image("id.png")
    var nameId = AnimatableImage(nameIdImage)

    var scoreTextImage = Image("scoretext.png")
    var scoreText = AnimatableImage(scoreTextImage)

    var livesTextImage = Image("livestext.png")
    var livesText = AnimatableImage(livesTextImage)

    var levelTextImage = Image("leveltext.png")
    var levelText = AnimatableImage(levelTextImage)

    var heartImage = Image("heart.png")
    var heartArray : Array<AnimatableImage?> = arrayOf(null, null, null)

    var loseScreenImage = Image("youlose.png")
    var loseScreen = AnimatableImage(loseScreenImage)

    var winScreenImage = Image("victory.png")
    var winScreen = AnimatableImage(winScreenImage)

    var restartOrQuitText = Image("restartquit.png")
    var restartOrQuit = AnimatableImage(restartOrQuitText)

    var readyImage = Image("ready.png")
    var readyText = AnimatableImage(readyImage, )//readyImage.width * 2, readyImage.height * 2)

    var goImage = Image("go.png")
    var goText = AnimatableImage(goImage, )//goImage.width * 2, goImage.height * 2)

    var animatableImages : ArrayList<AnimatableImage> = ArrayList()
    var animatableNumberDisplays : ArrayList<NumberDisplay> = ArrayList()

    //var gameFont = Font.loadFont("file:PressStart2P.ttf", 20.0)
    //var labelScore = Label("score: 0")

    private var gameStarted = false

    var paused = false

    private var score = 0
    private var currentLevel = 0
    private var lives = 0

    var scoreDisplay = NumberDisplay(WINDOW_WIDTH * (13.0/100.0), WINDOW_HEIGHT * ( 5.0/100.0), gamePane, score, false)
    var levelDisplay = NumberDisplay(WINDOW_WIDTH * (94.0/100.0), WINDOW_HEIGHT * ( 5.0/100.0), gamePane, currentLevel, false, 2)


    private var fireRateTimer = fireRateFrameDelay

    var isMovingLeft = false
    var isMovingRight = false
    var isShooting = false
    var player = Player()
    var enemyMatrix = EnemyMatrix()
    var playerProjectiles: ArrayList<PlayerProjectile> = ArrayList()
    var enemyProjectiles: ArrayList<EnemyProjectile> = ArrayList()
    var inMenu = true
    var rainbow = 0


    var menuSongFadeOut = false
    var gameSongFadeIn = false
    var gameSongFadeOut = false

    var doingExitMenuAnimation = false

    var doingRestartMenuAnimation = false

    var doingSetupLevelAnimation = false
    var doingSetupLevelTextAnimation = false

    var disableBackgroundMusic = false

    var restartPressed = false
    var gameStartPressed = false

    init {
        gameScene.fill = Color.GREY
        //gameSongPlayer.setOnEndOfMedia { gameSongPlayer.play() }
        //menuSongPlayer.setOnEndOfMedia { menuSongPlayer.play() }
        gameSongPlayer.cycleCount = INDEFINITE
        menuSongPlayer.cycleCount = INDEFINITE

        animatableImages.add(titleInvader)
        animatableImages.add(title)
        animatableImages.add(titleMenuText)
        animatableImages.add(nameId)
        animatableImages.add(loseScreen)
        animatableImages.add(winScreen)
        animatableImages.add(restartOrQuit)
        animatableImages.add(scoreText)
        animatableImages.add(levelText)
        animatableImages.add(livesText)
        animatableImages.add(readyText)
        animatableImages.add(goText)

        animatableNumberDisplays.add(scoreDisplay)
        animatableNumberDisplays.add(levelDisplay)


        menuSongPlayer.volume = 0.8
        gameSongPlayer.volume = 0.0

        if ( !disableBackgroundMusic )
        {
            menuSongPlayer.play()
        }

        var backgroundImage = BackgroundImage(
            Image("game_wallpaper.png"), BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT)

        gamePane.background = Background(backgroundImage)
        gamePane.maxWidth = WINDOW_WIDTH
        gamePane.prefWidth = WINDOW_WIDTH
        gamePane.maxHeight = WINDOW_HEIGHT
        gamePane.prefHeight = WINDOW_HEIGHT

        readyText.setPosition(WINDOW_WIDTH/2.0- readyText.start_width/2.0, WINDOW_HEIGHT/2.0 - readyText.start_height/2.0)
        goText.setPosition(WINDOW_WIDTH/2.0 - goText.start_width/2.0, WINDOW_HEIGHT/2.0 - goText.start_height/2.0)
        readyText.setIsVisible(false)
        goText.setIsVisible(false)
        //readyText.popIn(60.0)

        titleInvader.setPosition(WINDOW_WIDTH/2.0 - invaderImage.width/2, WINDOW_HEIGHT * (20.0/100.0))
        //titleInvader.layoutX = WINDOW_WIDTH/2.0 - invaderImage.width/2
        //titleInvader.layoutY = WINDOW_HEIGHT * (25.0/100.0)

        title.setPosition(WINDOW_WIDTH/2.0 - titleImage.width/2, WINDOW_HEIGHT * (60.0/100.0))
        //title.layoutX = WINDOW_WIDTH/2.0 - titleImage.width/2
        //title.layoutY = WINDOW_HEIGHT * (60.0/100.0)

        titleMenuText.setPosition(WINDOW_WIDTH/2.0 - menuTextImage.width/2, WINDOW_HEIGHT * (75.0/100.0))
        //titleMenuText.layoutX = WINDOW_WIDTH/2.0 - menuTextImage.width/2
        //titleMenuText.layoutY = WINDOW_HEIGHT * (75.0/100.0)

        nameId.setPosition(WINDOW_WIDTH * (8.0 / 100.0), WINDOW_HEIGHT * ( 8.0/100.0))
        //nameId.layoutX = WINDOW_WIDTH * (8.0 / 100.0)
        //nameId.layoutY = WINDOW_HEIGHT * ( 8.0/100.0)

        titleInvader.popIn(60.0, true)
        title.popIn(60.0)
        titleMenuText.popIn(60.0)
        nameId.popIn(60.0)

        titleInvader.fadeIn(120.0, true)
        title.fadeIn(120.0)
        titleMenuText.fadeIn(120.0)
        nameId.fadeIn(120.0)


        /*labelInstructions.layoutX = 0.0
        labelInstructions.prefWidth = WINDOW_WIDTH
        labelInstructions.layoutY = WINDOW_HEIGHT * ( 80.0/100.0)
        labelInstructions.textAlignment = TextAlignment.CENTER
        labelInstructions.alignment = Pos.CENTER
        labelInstructions.font = Font("Arial", 22.0)
        labelInstructions.textFill = Color.WHITE
*/
        //labelScore.font = gameFont
        //labelScore.textFill = Color.BLACK
        //labelScore.fill = Color.WHITE
        //scoreText.layoutX = gamePane.width * ( 5.0/100.0 )
        //scoreText.layoutY = gamePane.height * ( 5.0/100.0)
        scoreText.setPosition(gamePane.width * ( 5.0/100.0 ), gamePane.height * ( 5.0/100.0))
        scoreText.setIsVisible(false)
        //scoreText.isVisible = false

        //labelLevel.font = gameFont
        //labelLevel.fill = Color.WHITE
        //levelText.layoutX = gamePane.width * ( 86.0/100.0 )
        //levelText.layoutY = gamePane.height * ( 5.0/100.0)
        levelText.setPosition(gamePane.width * ( 86.0/100.0 ), gamePane.height * ( 5.0/100.0))
        levelText.setIsVisible(false)

        livesText.setPosition(gamePane.width * ( 72.0/100.0 ), gamePane.height * ( 5.0/100.0))
        //livesText.layoutX = gamePane.width * ( 72.0/100.0 )
        //livesText.layoutY = gamePane.height * ( 5.0/100.0)
        livesText.setIsVisible(false)
        //livesText.isVisible = false

        //winScreen.layoutX = gamePane.width/2 - winScreenImage.width/2
        //winScreen.layoutY = gamePane.height * (40.0/100.0)
        //loseScreen.layoutX = gamePane.width/2 - loseScreenImage.width/2
        //loseScreen.layoutY = gamePane.height * (40.0/100.0)
        winScreen.setPosition(WINDOW_WIDTH/2 - winScreenImage.width/2, WINDOW_HEIGHT * (40.0/100.0))
        loseScreen.setPosition(WINDOW_WIDTH/2 - loseScreenImage.width/2, WINDOW_HEIGHT * (40.0/100.0))
        winScreen.setIsVisible(false)
        loseScreen.setIsVisible(false)

        //restartOrQuit.layoutX = gamePane.width/2 - restartOrQuitText.width/2
        //restartOrQuit.layoutY = gamePane.height * (55.0/100.0)
        restartOrQuit.setPosition(WINDOW_WIDTH/2 - restartOrQuitText.width/2, WINDOW_HEIGHT * (55.0/100.0))
        restartOrQuit.setIsVisible(false)

        lives = 3
        for ( i in 0 until lives)
        {
            heartArray[i] = AnimatableImage(heartImage, heartImage.width*2, heartImage.height *2)
            //heartArray[i]!!.fitWidth = heartImage.width*2
            //heartArray[i]!!.fitHeight = heartImage.height *2
            heartArray[i]!!.setPosition(livesText.start_x + livesText.start_width + 10.0 + i*(heartImage.width*2 + 5.0), gamePane.height * ( 5.0/100.0 ))
            //heartArray[i]!!.layoutX = gamePane.width * ( (78.5 + i*1.75)/100.0 )
            //heartArray[i]!!.layoutY = gamePane.height * ( 5.0/100.0 )

            //gamePane.children.add(heartArray[i])

        }

        //textPane.children.addAll(labelScore, labelInstructions)
        gamePane.children.addAll(titleInvader, title, titleMenuText, scoreText, livesText, levelText, winScreen, loseScreen, restartOrQuit, nameId, readyText, goText)

        for ( i in 0 until lives )
        {
            //heartArray[i]!!.isVisible = false
            heartArray[i]!!.setIsVisible(false)
        }

        gameScene.setOnKeyPressed {keyEvent->
            if (keyEvent.code == KeyCode.Q) {
                if ( inMenu || !gameStarted )
                {
                    Platform.exit()
                }

            } else if (keyEvent.code == KeyCode.ENTER) {

                if ( inMenu )
                {
                    optionSelectedSound.play(0.8)
                    if ( !disableBackgroundMusic)
                    {
                        gameSongPlayer.play()
                    }

                    gameSongFadeIn = true
                    menuSongFadeOut = true
                    exitMainMenu(1)
                }

            } else if (keyEvent.code == KeyCode.DIGIT1) {
                if ( inMenu )
                {
                    optionSelectedSound.play(0.8)
                    if ( !disableBackgroundMusic)
                    {
                        gameSongPlayer.play()
                    }
                    menuSongFadeOut = true
                    gameSongFadeIn = true

                    exitMainMenu(1)
                }
            } else if (keyEvent.code == KeyCode.DIGIT2) {
                if ( inMenu )
                {
                    optionSelectedSound.play(0.8)
                    if ( !disableBackgroundMusic)
                    {
                        gameSongPlayer.play()
                    }
                    menuSongFadeOut = true
                    gameSongFadeIn = true
                    exitMainMenu(2)
                }
            } else if (keyEvent.code == KeyCode.DIGIT3) {
                if ( inMenu )
                {
                    optionSelectedSound.play(0.8)
                    if ( !disableBackgroundMusic)
                    {
                        gameSongPlayer.play()
                    }
                    menuSongFadeOut = true
                    gameSongFadeIn = true
                    exitMainMenu(3)
                }
            } else if (keyEvent.code == KeyCode.A || keyEvent.code == KeyCode.LEFT) {
                //if ( gameStarted)
                {

                }
                isMovingLeft = true
            } else if (keyEvent.code == KeyCode.D || keyEvent.code == KeyCode.RIGHT ) {
                //if ( gameStarted)
                {

                }
                isMovingRight = true
            } else if (keyEvent.code == KeyCode.SPACE) {

                //if ( gameStarted )
                {

                }
                isShooting = true
            } else if (keyEvent.code == KeyCode.R )
            {
                restartGame()
            }
            else if ( keyEvent.code == KeyCode.G)
            {
                if ( gameStarted)
                {
                    player.toggleGodMode(gamePane)
                }
            }
            else if ( keyEvent.code == KeyCode.M)
            {
                disableBackgroundMusic = true
                gameSongPlayer.stop()
                menuSongPlayer.stop()
            }
        }

        gameScene.setOnKeyReleased {keyEvent->
            if (keyEvent.code == KeyCode.A || keyEvent.code == KeyCode.LEFT) {
                isMovingLeft = false
            } else if (keyEvent.code == KeyCode.D || keyEvent.code == KeyCode.RIGHT) {
                isMovingRight = false
            } else if (keyEvent.code == KeyCode.SPACE) {
                isShooting = false
            }

        }

        animationTimer(this)
    }


    fun restartGame()
    {
        if ( !gameStarted && !inMenu && !restartPressed)
        {
            restartPressed = true
            exitRestartMenu()
        }
    }

    fun showWinScreen()
    {
        //println("you win")
        youWinSound.play()
        gameSongFadeOut = true

        restartOrQuit.isVisible = false
        //levelDisplay.hide()
        levelDisplay.setIsVisible(false)

        //restartOrQuit.fitWidth = 0.0
        //restartOrQuit.fitHeight = 0.0

        winScreen.isVisible = false

        player.playerView.setIsVisible(false)


        //restartOrQuit.popIn(60.0)
        //restartOrQuit.fadeIn(60.0)
        //winScreen.popIn(60.0)
        //winScreen.fadeIn(60.0)

        for ( i in 0 until lives)
        {
            //heartArray[i]!!.isVisible = false
            heartArray[i]!!.setIsVisible(false)
        }
        player.playerView.isVisible = false
        //scoreText.isVisible = false
        levelText.setIsVisible(false)
        livesText.setIsVisible(false)

        clearProjectiles()
    }

    fun clearProjectiles()
    {
        for ( projectile in enemyProjectiles)
        {
            projectile.destroy(gamePane)
        }
        for ( projectile in playerProjectiles)
        {
            projectile.destroy(gamePane)
        }

        enemyProjectiles.clear()
        playerProjectiles.clear()

    }

    fun showLoseScreen()
    {
        //println("you lose")

        playerDeathSound.stop()
        youLoseSound.play()

        gameSongFadeOut = true

        restartOrQuit.setIsVisible(true)
        enemyMatrix.clear(gamePane)
        //levelDisplay.hide()
        levelDisplay.setIsVisible(false)

        loseScreen.setIsVisible(true)
        //loseScreen.fitHeight = 0.0
        //loseScreen.fitWidth = 0.0


        for ( i in 0 until lives)
        {
            heartArray[i]!!.setIsVisible(false)//.isVisible = false
        }
        player.playerView.isVisible = false
        //scoreText.isVisible = false
        levelText.setIsVisible(false)
        livesText.setIsVisible(false)

        restartOrQuit.popIn(60.0)
        restartOrQuit.fadeIn(60.0)
        loseScreen.popIn(60.0)
        loseScreen.fadeIn(60.0)

        clearProjectiles()

    }

    fun getRandomProjectileTime() : Double
    {
        return RANDOM_ENEMY_FIRE_TIMER + Random.nextDouble(-30.0 * enemySpeedModifier, 30.0 / enemySpeedModifier)
    }

    fun startLevelAnimation()
    {
        //println("start level animation")



        doingSetupLevelAnimation = true
        gamePane.children.clear()
        gamePane.children.addAll(titleInvader, title, titleMenuText, scoreText, livesText, levelText, winScreen, loseScreen, restartOrQuit, nameId, goText, readyText)


        scoreDisplay.setNum(score, gamePane)
        //scoreDisplay.addToPane(gamePane)
        //scoreDisplay.show()
        scoreDisplay.setIsVisible(true)
        levelDisplay.setNum(currentLevel, gamePane)
        //levelDisplay.addToPane(gamePane)
        // levelDisplay.show()
        levelDisplay.setIsVisible(true)


        if (currentLevel == 1) {
            lives = 3
        }

        enemySpeedModifier = 0.8 + (currentLevel - 1) * 0.5


        for (i in 0 until lives) {
            gamePane.children.add(heartArray[i])
            heartArray[i]!!.setIsVisible(true)//.isVisible = true
        }

        if (player.godMode)
        {
            player = Player()
            player.toggleGodMode(gamePane)
        }
        else
        {
            player = Player()
            player.spawn(gamePane)
        }

        player.playerView.setIsVisible(true)
        player.playerView.fadeIn(60.0)


        enemyMatrix = EnemyMatrix()
        enemyMatrix.spawn(gamePane)

        title.setIsVisible(false)
        titleMenuText.setIsVisible(false)
        titleInvader.setIsVisible(false)
        nameId.setIsVisible(false)

        enemyProjectiles.clear()
        playerProjectiles.clear()

        readyText.setIsVisible(true)
        goText.setIsVisible(true)

        readyText.popOut(80.0)
        readyText.fadeInOut(80.0)

        readyText.setDelay(40.0)

        //goText.fadeIn(0.0)
        goText.popOut(80.0)
        goText.fadeInOut(80.0)

        goText.setDelay(120.0)

        var backgroundImage = BackgroundImage(
            Image("game_wallpaper.png"), BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT)

        gamePane.background = Background(backgroundImage)


        //scoreText.setIsVisible(true)//.isVisible = true
        //levelText.setIsVisible(true)//.isVisible = true
        //livesText.setIsVisible(true)//.isVisible = true

        loseScreen.setIsVisible(false)
        winScreen.setIsVisible(false)

        restartOrQuit.setIsVisible(false)

        enemyMatrix.popIn(60.0, 2.0)
    }

    fun startLevelUIAnimation()
    {
        //println("started level ui anim")
        doingSetupLevelTextAnimation = true

        levelText.setIsVisible(true)
        livesText.setIsVisible(true)
        scoreText.setIsVisible(true)
        levelText.popIn(60.0)
        livesText.popIn(60.0)
        scoreText.popIn(60.0)
        levelText.fadeIn(40.0)
        livesText.fadeIn(40.0)
        scoreText.fadeIn(40.0)

        scoreDisplay.setIsVisible(true)
        scoreDisplay.popIn(60.0)
        levelDisplay.setIsVisible(true)
        levelDisplay.popIn(60.0)
    }

    fun exitMainMenu(level: Int)
    {
        if ( !gameStartPressed)
        {
            gameStartPressed = true
        }
        else
        {
            return
        }

        inMenu = false
        currentLevel = level
        doingExitMenuAnimation = true
        titleInvader.popOut(60.0, true)
        title.popOut(60.0, true)
        titleMenuText.popOut(60.0, true)
        nameId.popOut(60.0, true)
        titleInvader.fadeOut(45.0, true)
        title.fadeOut(45.0, true)
        titleMenuText.fadeOut(45.0, true)
        nameId.fadeOut(45.0, true)
    }

    fun exitRestartMenu()
    {

        doingRestartMenuAnimation = true
        loseScreen.popOut(60.0, )
        winScreen.popOut(60.0, )
        loseScreen.fadeOut(45.0, )
        winScreen.fadeOut(45.0, )
        restartOrQuit.popOut(60.0)
        restartOrQuit.fadeOut(45.0)
    }


    fun startGame(level: Int)
    {
        //println("started game")

        if ( !gameStarted)
        {
            gameStarted = true
            restartPressed = false
            gameStartPressed = false
            randomEnemyProjectileTimer = getRandomProjectileTime()
            //currentLevel = level

            //labelLevel.text = "Level: $level"

        }
    }

    override var frameCount = 0.0


    var randomEnemyProjectileTimer = 0.0

    override fun draw() {

        for ( image in animatableImages)
        {
            image.stepFrame()
        }

        for ( image in animatableNumberDisplays)
        {
            image.stepFrame()
        }

        enemyMatrix.stepFrame()
        player.playerView.stepFrame()

        for ( p in enemyProjectiles)
        {
            p.projectileView?.stepFrame()
        }

        if ( playerIsDying)
        {
           // println("player is dying")
            paused = true

            if ( !player.playerView.inAnimation)
            {
                //println("player has died")
                clearProjectiles()
                playerIsRespawning = true
                playerIsDying = false
                player.respawn()
                player.playerView.fadeIn(60.0)
            }


        }

        if ( playerIsRespawning)
        {
            paused = true
            if ( !player.playerView.inAnimation)
            {
                playerIsRespawning = false
                paused = false
            }
        }

        if ( doingSetupLevelTextAnimation)
        {
           // println("setting up text")
            var doneAnims = true

            for ( image in animatableImages)
            {
                if ( image.inAnimation )
                {
                    doneAnims = false
                }
            }

            if ( doneAnims)
            {
                doingSetupLevelTextAnimation = false
                //println("finished ui anims")

            }
        }

        if ( doingSetupLevelAnimation)
        {
            var doneAnims = true

            for ( image in animatableImages)
            {
                if ( image.inAnimation )
                {
                    doneAnims = false
                }
            }

            if ( enemyMatrix.inAnim())
            {
                doneAnims = false
            }

            if ( doneAnims)
            {
                //println("done setup level anims")
                doingSetupLevelAnimation = false

                readyText.setIsVisible(false)
                goText.setIsVisible(false)

                startGame(currentLevel)
            }
        }

        if ( doingExitMenuAnimation)
        {
            var doneAnims = true

            for ( image in animatableImages)
            {
                if ( image.inAnimation )
                {
                    doneAnims = false
                }
            }

            if ( doneAnims)
            {
                doingExitMenuAnimation = false

                startLevelAnimation()

                startLevelUIAnimation()
            }
        }

        if ( doingRestartMenuAnimation)
        {
            var doneAnims = true

            for ( image in animatableImages)
            {
                if ( image.inAnimation )
                {
                    doneAnims = false
                }
            }

            if ( doneAnims) {
                doingRestartMenuAnimation = false
                gameSongPlayer.volume = 0.0

                if ( !disableBackgroundMusic)
                {
                    gameSongPlayer.play()
                }

                gameSongFadeIn = true
                optionSelectedSound.play(0.8)
                lives = 3
                score = 0


                startLevelAnimation()
                startLevelUIAnimation()

            }
        }



        if ( menuSongFadeOut )
        {
            if ( menuSongPlayer.volume > 0.05 )
            {
                menuSongPlayer.volume -= 0.01
            }
            else
            {
                menuSongFadeOut = false
                menuSongPlayer.stop()
            }
        }

        if ( gameSongFadeIn )
        {
            if ( gameSongPlayer.volume < 0.1 )
            {
                gameSongPlayer.volume += 0.005
            }
            else
            {
                gameSongFadeIn = false
                //menuSongPlayer.stop()
            }
        }

        if ( gameSongFadeOut )
        {
            if ( gameSongPlayer.volume > 0.05 )
            {
                gameSongPlayer.volume -= 0.01
            }
            else
            {
                gameSongFadeOut = false
                gameSongPlayer.stop()
            }
        }

        //println(frameCount)
        if ( !gameStarted || paused )
        {
            return
        }

        var mx = 0.0

        if ( isMovingLeft && isMovingRight)
        {

        }
        else if ( isMovingLeft )
        {
            mx -= playerMovementSpeed
        }
        else if ( isMovingRight )
        {
            mx += playerMovementSpeed
        }

        if ( player.godMode)
        {
            player.translate(mx)
        }
        player.translate(mx)


        for ( projectile in playerProjectiles)
        {
            projectile.move()
        }

        for ( projectile in enemyProjectiles )
        {
            projectile.move()
        }


        if (fireRateTimer > 0.0)
        {
            fireRateTimer -= 1.0
        }

        if ( fireRateTimer <= 0.0 && isShooting)
        {
            if ( player.godMode )
            {
                fireRateTimer = 1.0
            }
            else
            {
                fireRateTimer = fireRateFrameDelay

            }


            if ( player.godMode) {
                var newProjectile = PlayerProjectile(player.xPos + player.width / 2 - 8.0, player.yPos - 25.0, rainbow, Random.nextDouble(-12.0,12.0))
                rainbow += 1
                if (rainbow == 7)
                {
                    rainbow = 0
                }
                playerFireSound.play(0.16)

                newProjectile.spawn(gamePane)

                playerProjectiles.add(newProjectile)
            }
            else
            {
                //var playerFire = MediaPlayer(playerFireSound)
                //playerFire.volume = 0.5
                //playerFire.play()

                playerFireSound.play(0.5)

                var newProjectile = PlayerProjectile(player.xPos + player.width/2 - 8.0, player.yPos - 25.0)
                newProjectile.spawn(gamePane)

                playerProjectiles.add(newProjectile)
            }


        }

        // random projectile
        randomEnemyProjectileTimer -= 1.0
        if ( randomEnemyProjectileTimer < 0.0)
        {
            randomEnemyProjectileTimer = getRandomProjectileTime()

            //var enemyfire = MediaPlayer(enemyFireSound)
            //enemyfire.volume = 0.5
            //enemyfire.play()
            enemyFireSound.play(0.5)
            enemyMatrix.spawnProjectile(gamePane)?.let { enemyProjectiles.add(it) }
        }

        // get an enemy projectile
        var newEnemyProjectile = enemyMatrix.move(gamePane)

        if ( newEnemyProjectile != null )
        {
            //println("spawned projectile")
            //var enemyfire = MediaPlayer(enemyFireSound)
            //enemyfire.volume = 0.5
            //enemyfire.play()
            enemyFireSound.play(0.5)
            enemyProjectiles.add(newEnemyProjectile)
        }

        // a player projectile hits an enemy
        for ( i in playerProjectiles.lastIndex downTo  0 )
        {
            if ( enemyMatrix.checkPlayerProjectileCollision(playerProjectiles[i], gamePane))
            {
                playerProjectiles.remove(playerProjectiles[i])

                score += 1000 * currentLevel
                scoreDisplay.setNum(score, gamePane)

                enemySpeedModifier += 0.02 + 0.02* (currentLevel - 1)

               // var enemydeath = MediaPlayer(enemyDeathSound)
                //enemydeath.volume = 0.5
                //enemydeath.play()
                enemyDeathSound.play(0.5)
            //scoreDisplay.refresh(gamePane)
            }
        }

        // an enemy projectile hits the player
        for ( i in enemyProjectiles.lastIndex downTo 0 ) {
            if (enemyProjectiles.size > 0)
            {
                if ( player.collides(enemyProjectiles[i])) {
                    if (lives > 1) {
                        lives -= 1
                        //player.respawn()

                        gamePane.children.remove(heartArray[lives])

                        //var death = MediaPlayer(playerDeathSound)
                        //death.volume = 0.5
                        //death.play()
                        playerDeathSound.play(0.5)

                        for ( enemyProjectile in enemyProjectiles )
                        {
                            enemyProjectile.projectileView?.fadeOut(60.0)

                        }

                        player.playerView.fadeOut(60.0)
                        playerIsDying = true

                        if (enemyProjectiles.size > 0) {
                            gamePane.children.remove(enemyProjectiles[i].projectileView)
                        }
                    } else {
                        lives -= 1
                        currentLevel = 1
                        showLoseScreen()
                        gameStarted = false
                        gamePane.children.remove(heartArray[lives])
                        player.playerView.setIsVisible(false)

                        if (enemyProjectiles.size > 0) {
                            gamePane.children.remove(enemyProjectiles[i].projectileView)
                        }


                        player.destroy()
                    }

                    //gamePane.children.remove(player.playerView)
                    //
                }
            }
        }

        // if an alien makes contact with the player
        if ( enemyMatrix.checkPlayerCollision(player))
        {
            gamePane.children.remove(player.playerView)

            currentLevel = 1
            showLoseScreen()
            gameStarted = false
            player.destroy()

        }

        // cleaning up projectiles that are off screen
        for ( i in playerProjectiles.lastIndex downTo  0 )
        {
            if ( playerProjectiles[i].outOfBounds())
            {
                playerProjectiles[i].destroy(gamePane)
                playerProjectiles.remove(playerProjectiles[i])
                //println("removed player projectile")
            }
        }

        for ( i in enemyProjectiles.lastIndex downTo  0 )
        {
            if ( enemyProjectiles[i].outOfBounds())
            {
                enemyProjectiles[i].destroy(gamePane)
                enemyProjectiles.remove(enemyProjectiles[i])

                //println("removed projectile")
            }
        }

        if ( enemyMatrix.numEnemies == 0 )
        {
            if ( currentLevel < 3 )
            {
                //var victory = MediaPlayer(victorySound)
                //victory.volume = 0.5
                //victory.play()
                victorySound.play(0.5)


                gameStarted = false
                currentLevel += 1
                startLevelAnimation()
                println(currentLevel)
                score += currentLevel * 10000
                scoreDisplay.setNum(score, gamePane)
                //scoreDisplay.refresh(gamePane)
            }
            else
            {
                gameStarted = false
                score += currentLevel * 10000
                scoreDisplay.setNum(score, gamePane)
                //scoreDisplay.refresh(gamePane)


                winScreen.opacity = 0.0
                winScreen.fitWidth = 0.0
                winScreen.fitHeight = 0.0


                currentLevel = 1
                showWinScreen()

                restartOrQuit.setIsVisible(true)
                winScreen.setIsVisible(true)
                restartOrQuit.popIn(60.0)
                winScreen.popIn(60.0)
                restartOrQuit.fadeIn(60.0)
                winScreen.fadeIn(60.0)

            }
        }


    }

}

