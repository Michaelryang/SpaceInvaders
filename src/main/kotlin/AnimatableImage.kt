import javafx.animation.AnimationTimer
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

fun lerp(start: Double, end: Double, t: Double): Double {
    return start + (end - start) * t
}

var none = {x: Double -> 0.0}
val linear = { x: Double -> x}
val flip = { x: Double -> 1 - x }
val easeIn = { x: Double -> x.pow(2) }
val easeOut = { x: Double -> flip(easeIn(flip(x))) }
val easeInOut = { x: Double -> lerp(easeIn(x), easeOut(x), x) }



class AnimatableImage(image: Image, sw: Double = image.width, sh: Double = image.height) : ImageView(image) {
    var needsPause = false

    var frameDelay = 0.0

    var inAnimation = false

    var doingFadeInAnimation = false
    var doingFadeOutAnimation = false
    var doingPopInAnimation = false
    var doingPopOutAnimation = false

    var doingFadeInOutAnimation = false

    var start_width = sw
    var start_height = sh

    var start_x = layoutX
    var start_y = layoutY

    var current_width = start_width
    var current_height = start_height

    var current_x = start_x
    var current_y = start_y

    var fadeInAnimationLength = 0.0
    var fadeOutAnimationLength = 0.0
    var popInAnimationLength = 0.0
    var popOutAnimationLength = 0.0
    var fadeInOutAnimationLength = 0.0

    var animationCurrentFrame = 0.0

    var fadeInEasing = linear
    var fadeOutEasing = linear
    var popInEasing = easeOut
    var popOutEasing = easeIn

    var vis = true

    fun setIsVisible(v: Boolean) {
        vis = v
        isVisible = vis
    }

    fun fadeIn(frameLength: Double, pauses: Boolean = false) {
        if (inAnimation) {
            //reset()
        }

        inAnimation = true

        doingFadeInAnimation = true
        needsPause = pauses

        fadeInAnimationLength = frameLength

        //fadeEasing = linear
        stepFrame()
    }

    fun fadeInOut(frameLength: Double, pauses: Boolean = false)
    {
        //println("fade in out")

        inAnimation = true
        doingFadeInOutAnimation = true
        needsPause = pauses

        fadeInOutAnimationLength = frameLength

        opacity = 0.0


        //stepFrame()
    }

    fun fadeOut(frameLength: Double, pauses: Boolean = false) {
        //println("fadeout")
        if (inAnimation) {
            //reset()
        }

        inAnimation = true

        doingFadeOutAnimation = true
        needsPause = pauses

        fadeOutAnimationLength = frameLength

        //easing = easeOut
        stepFrame()
    }

    fun popIn(frameLength: Double, pauses: Boolean = false) {

        reset()

        current_x += current_width / 2
        current_y += current_height / 2

        layoutX = current_x
        layoutY = current_y

        current_width = 0.0
        current_height = 0.0

        fitWidth = 0.0
        fitHeight = 0.0

        inAnimation = true

        doingPopInAnimation = true
        needsPause = pauses

        popInAnimationLength = frameLength

        //println("pop in $fitWidth $fitHeight $popInAnimationLength $animationCurrentFrame")

        //easing = easeOut
        stepFrame()
    }

    fun popOut(frameLength: Double, pauses: Boolean = false) {
        reset()

        //println("pop out")
        current_x = start_x
        current_y = start_y

        layoutX = current_x
        layoutY = current_y

        current_width = start_width
        current_height = start_height

        fitWidth = current_width
        fitHeight = current_height

        inAnimation = true

        doingPopOutAnimation = true
        needsPause = pauses

        popOutAnimationLength = frameLength

        //easing = easeOut
        stepFrame()
    }

    fun reset() {
        doingFadeInAnimation = false
        doingFadeOutAnimation = false
        doingPopInAnimation = false
        doingPopOutAnimation = false

        animationCurrentFrame = 0.0

        fitWidth = start_width
        fitHeight = start_height

        layoutX = start_x
        layoutY = start_y

        current_x = start_x
        current_y = start_y

        current_width = start_width
        current_height = start_height

        opacity = 1.0

        //isVisible = true
    }

    fun setPosition(x: Double, y: Double) {
        start_x = x
        start_y = y
        layoutX = x
        layoutY = y
        current_x = x
        current_y = y
    }

    fun inAnimation(): Boolean {
        return doingPopInAnimation || doingPopOutAnimation || doingFadeInAnimation || doingFadeOutAnimation
    }

    fun setDelay(delay : Double)
    {
        //println("delay set: " + delay)
        frameDelay = delay
    }

    fun stepFrame()
    {
        isVisible = vis

        if ( frameDelay > 0.0 )
        {
            frameDelay -= 1.0
            return
        }

        if ( inAnimation)
        {
            animationCurrentFrame += 1.0
        }

        if ( doingFadeInAnimation)
        {
            if ( animationCurrentFrame >= fadeInAnimationLength)
            {
                doingFadeInAnimation = false

                if (!inAnimation()) {
                    inAnimation = false
                    animationCurrentFrame = 0.0
                    needsPause = false
                }
                opacity = 1.0

                //reset()
            }
            else
            {
                opacity = lerp(0.0, 1.0, fadeInEasing(animationCurrentFrame/fadeInAnimationLength))
            }
        }
        else if ( doingFadeOutAnimation)
        {
            if ( animationCurrentFrame >= fadeOutAnimationLength)
            {
                doingFadeOutAnimation = false

                if (!inAnimation()) {
                    inAnimation = false
                    animationCurrentFrame = 0.0
                    needsPause = false
                }
                opacity = 0.0

                //reset()
            }
            else
            {
                opacity = lerp(1.0,0.0, fadeOutEasing(animationCurrentFrame/fadeOutAnimationLength))
            }
        }
        else if ( doingFadeInOutAnimation)
        {
            if ( animationCurrentFrame >= fadeInOutAnimationLength)
            {
                doingFadeInOutAnimation = false

                if (!inAnimation()) {
                    inAnimation = false
                    animationCurrentFrame = 0.0
                    needsPause = false
                }
                opacity = 0.0
            }
            else if ( animationCurrentFrame >= fadeInOutAnimationLength / 2.0)
            {
                // fading out
                opacity = lerp(1.0,0.0, fadeOutEasing((animationCurrentFrame-fadeInOutAnimationLength/2.0)/(fadeInOutAnimationLength/2.0)))
            }
            else
            {
                // fading in
                opacity = lerp(0.0, 1.0, fadeInEasing(animationCurrentFrame/(fadeInOutAnimationLength/2.0)))
            }
        }

        if ( doingPopInAnimation)
        {
            if ( animationCurrentFrame >= popInAnimationLength) {

                //println("done popin")
                doingPopInAnimation = false

                if (!inAnimation()) {
                    inAnimation = false
                    animationCurrentFrame = 0.0
                    needsPause = false
                }

                //reset()

                layoutX = start_x
                layoutY = start_y

                current_x = start_x
                current_y = start_y

                fitWidth = start_width
                fitHeight = start_height
            }
            else
            {

                //println("animating pop in $fitWidth $fitHeight $current_width $animationCurrentFrame")
                //println("pop out $animationCurrentFrame / $current_width" )
                var prev_w = current_width
                var prev_h = current_height

                current_width = lerp(0.0,start_width, popInEasing(animationCurrentFrame/popInAnimationLength))
                current_height = lerp(0.0,start_height, popInEasing(animationCurrentFrame/popInAnimationLength))

                current_x -= (current_width - prev_w) / 2.0
                current_y -= (current_height - prev_h ) / 2.0

                layoutX = current_x
                layoutY = current_y

                fitWidth = current_width
                fitHeight = current_height
            }
        }
        else if ( doingPopOutAnimation)
        {
            //println("pop out frame " + animationCurrentFrame)

            if ( animationCurrentFrame >= popOutAnimationLength) {
                doingPopOutAnimation = false
                //println("pop out ends")

                if (!inAnimation()) {
                    inAnimation = false
                    animationCurrentFrame = 0.0
                    needsPause = false
                }

                //reset()

                layoutX = start_x
                layoutY = start_y

                current_x = start_x
                current_y = start_y

                fitWidth = 0.0
                fitHeight = 0.0
            }
            else
            {
                var prev_w = current_width
                var prev_h = current_height

                current_width = lerp(start_width, 0.0, popOutEasing(animationCurrentFrame/popOutAnimationLength))
                current_height = lerp(start_height,0.0, popOutEasing(animationCurrentFrame/popOutAnimationLength))

                current_x -= (current_width - prev_w) / 2.0
                current_y -= (current_height - prev_h ) / 2.0

                layoutX = current_x
                layoutY = current_y

                fitWidth = current_width
                fitHeight = current_height
            }

        }
    }

    init {
        fitWidth = start_width
        fitHeight = start_height

        val timer: AnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                stepFrame()
            }
        }
        // start timer
       // timer.start()
    }
}