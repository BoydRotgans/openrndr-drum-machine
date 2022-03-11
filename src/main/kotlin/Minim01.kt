import ddf.minim.Minim
import ddf.minim.signals.Oscillator
import ddf.minim.ugens.Oscil
import ddf.minim.ugens.Waves
import minim.MinimObject
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.math.map

fun main() = application {
    configure {
        width = 1280
        height = 720
    }
    program {

        val minim = Minim(MinimObject())
        val out = minim.lineOut

        val wave = Oscil(440f, 0.4f, Waves.SINE)
        wave.patch(out)

        extend {
            wave.setAmplitude(
                map(0.0, width*1.0, 0.0, 1.0, mouse.position.x).toFloat()
            )
            drawer.stroke = ColorRGBa.WHITE

            val linesLeft = mutableListOf<Vector2>()
            val linesRight = mutableListOf<Vector2>()

            (0 until out.bufferSize()-1).forEach {
                linesLeft.add( Vector2(it.toDouble(), 50.0 - out.left.get(it)*50.0))
                linesRight.add( Vector2(it.toDouble(), 150.0 - out.right.get(it)*50.0))
            }

            drawer.stroke = ColorRGBa.BLUE
            drawer.lineStrip(linesLeft)
            drawer.stroke = ColorRGBa.RED
            drawer.lineStrip(linesRight)
        }
    }
}