import ddf.minim.Minim
import ddf.minim.analysis.*
import minim.MinimObject
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.rectangleBatch
import org.openrndr.math.Vector2
import org.openrndr.math.map
import projects.classes.Marker
import projects.classes.WaveFormFFT
import java.lang.Math.log

fun main() = application {

    configure {
        width = 512
        height = 512
        title = ""
    }

    program {

        // load minim library
        val minim = Minim(MinimObject())

        // waveform
        val waveFormFFT = WaveFormFFT(minim, width, height)

        ended.listen {
            minim.stop()
        }

        // add markers
        waveFormFFT.markers.add(Marker(10, -7.0))
        waveFormFFT.markers.add(Marker(150, -8.0))
        waveFormFFT.markers.add(Marker(400, -10.0))

        keyboard.keyUp.listen {
            if(it.name == "n") {
                waveFormFFT.fixNoise()
            }
        }

        extend {
            waveFormFFT.draw(drawer)
        }
    }
}