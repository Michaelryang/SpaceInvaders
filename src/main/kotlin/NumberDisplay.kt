import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import kotlin.math.max

// my own implementation of a number display
// for the purpose of cooler animations.

var digitImages = arrayOf((Image("digit0.png")),
    (Image("digit1.png")),
    (Image("digit2.png")),
    (Image("digit3.png")),
    (Image("digit4.png")),
    (Image("digit5.png")),
    (Image("digit6.png")),
    (Image("digit7.png")),
    (Image("digit8.png")),
    (Image("digit9.png")))

const val MAX_DIGITS = 9

class NumberDisplay(x: Double, y: Double, pane: Pane, n: Int = 0, visible: Boolean = true, maxdigits : Int = MAX_DIGITS)
{
    var number = 0
    var xPos = 0.0
    var yPos = 0.0
    var maxDigits = MAX_DIGITS


    // nine digits, hardcoded
    var displayDigits = arrayOf(AnimatableImage(digitImages[0]), AnimatableImage(digitImages[0]), AnimatableImage(digitImages[0]),
        AnimatableImage(digitImages[0]), AnimatableImage(digitImages[0]), AnimatableImage(digitImages[0]), AnimatableImage(digitImages[0]),
        AnimatableImage(digitImages[0]), AnimatableImage(digitImages[0]))

    /*fun updateDisplay(pane: Pane)
    {
        for ( digit in displayDigits)
        {
            pane.children.remove(digit)
        }
    }*/

    fun setNum(num: Int, pane: Pane)
    {

        removeFromPane(pane)
        var running = num

        if ( running > 999999999 )
        {
            running = 999999999
        }
        else if ( running < 0 )
        {
            running = 0
        }

        number = running

        var runningDigit = MAX_DIGITS - 1

        while ( runningDigit >= 0 )
        {
            var d = running % 10

            displayDigits[runningDigit] = AnimatableImage(digitImages[d])
            running /= 10
            runningDigit -= 1
        }

        setPosition(xPos, yPos)
        addToPane(pane)
    }

    fun setPosition(x: Double, y: Double)
    {
        xPos = x
        yPos = y

        for ( i in 0..displayDigits.lastIndex)
        {
            displayDigits[i].setPosition(xPos + i * 20.0 - ((MAX_DIGITS - maxDigits) * 20.0), yPos)
            //displayDigits[i].layoutX = xPos + i * 20.0 - ((MAX_DIGITS - maxDigits) * 20.0)
            //displayDigits[i].layoutY = yPos
        }
    }

    /*
    fun hide()
    {
        for ( digit in displayDigits)
        {
           digit.isVisible = false
        }
    }

    fun show()
    {
        for ( digit in displayDigits)
        {
            digit.isVisible = true
        }
    }*/

    private fun removeFromPane(pane: Pane)
    {
        var c = maxDigits
        for ( i in displayDigits.lastIndex downTo 0)
        {
            if ( c == 0 )
            {
               break
            }

            c -= 1
            pane.children.remove(displayDigits[i])
        }
    }

    private fun addToPane(pane: Pane)
    {
        var c = maxDigits
        for ( i in displayDigits.lastIndex downTo 0)
        {
            if ( c == 0 )
            {
                break
            }

            c -= 1
            pane.children.add(displayDigits[i])
        }
    }

    fun getNum() : Int
    {
        return number
    }

    init {
        maxDigits = maxdigits
        xPos = x
        yPos = y
        setNum(n, pane)

        //addToPane(pane)

        setIsVisible(visible)
    }

    fun popIn(frameLength: Double)
    {
        var c = maxDigits
        for ( i in displayDigits.lastIndex downTo 0)
        {
            if ( c == 0 )
            {
                break
            }

            c -= 1
            displayDigits[i].popIn(frameLength)
        }
    }

    fun popOut (frameLength: Double)
    {
        var c = maxDigits
        for ( i in displayDigits.lastIndex downTo 0)
        {
            if ( c == 0 )
            {
                break
            }

            c -= 1
            displayDigits[i].popOut(frameLength)
        }
    }

    fun setIsVisible(v: Boolean)
    {
        var c = maxDigits
        for ( i in displayDigits.lastIndex downTo 0)
        {
            if ( c == 0 )
            {
                break
            }

            c -= 1
            displayDigits[i].setIsVisible(v)
        }
    }

    fun stepFrame()
    {
        var c = maxDigits
        for ( i in displayDigits.lastIndex downTo 0)
        {
            if ( c == 0 )
            {
                break
            }

            c -= 1
            displayDigits[i].stepFrame()
        }
    }
}
