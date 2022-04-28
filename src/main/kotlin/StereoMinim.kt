import ddf.minim.Minim
import minim.MinimObject
import org.openrndr.application
import org.openrndr.math.clamp
import org.openrndr.math.map

fun main() = application {
    configure { }
    program {

        val minim = Minim(MinimObject())
        val sample = minim.loadSample("data/APM_Adobe_Going Home_v3.WAV", 512)

        keyboard.keyUp.listen {
            sample.trigger()
        }

        mouse.moved.listen {
            sample.balance = map(0.0, width*1.0, -1.0, 1.0, mouse.position.x).clamp(-1.0, 1.0).toFloat()
        }

        extend {
        }
    }
}