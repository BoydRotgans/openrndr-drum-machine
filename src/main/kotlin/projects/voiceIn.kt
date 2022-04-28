import ddf.minim.Minim
import ddf.minim.analysis.*
import minim.MinimObject
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.rectangleBatch
import org.openrndr.math.Vector2
import org.openrndr.math.map
import java.lang.Math.log

fun main() = application {

    configure {
        width = 512
        height = 512
    }

    program {

        // load minim library
        val minim = Minim(MinimObject())

        // microphone
        val microphone = minim.getLineIn(Minim.MONO, 2048, 48000f)
        val fft = FFT(microphone.bufferSize(), microphone.sampleRate())
        fft.window(CosineWindow())

        ended.listen {
            minim.stop()
        }
        val bufferSize = 1024

        val backgroundNoiseCompensation = mutableListOf<Double>()
        val backgroundNoiseCompensationStored = mutableListOf<Double>()
        val wave = mutableListOf<Double>()

        (0 .. bufferSize).forEach {
            wave.add(0.0)
            backgroundNoiseCompensation.add(0.0)
            backgroundNoiseCompensationStored.add(0.0)
        }

        fun fixNoise() {
            backgroundNoiseCompensation.forEachIndexed { index, d ->
                backgroundNoiseCompensationStored[index] = d
            }
        }
        keyboard.keyUp.listen {
            if(it.name == "n") {
                fixNoise()
            }
        }

        extend {
            // draw waveform
            fft.forward(microphone.mix)

            (0 .. bufferSize).forEach {
                val bandDB = log(1.0*fft.getBand(it)/fft.timeSize())
                if(!bandDB.isInfinite()) {
                    backgroundNoiseCompensation[it] = (bandDB)
                    val barHeight = bandDB - backgroundNoiseCompensationStored[it]
                    wave[it] = (wave[it] * 0.9) + ((barHeight) * 0.1)
                }
            }

            val waveStrip = mutableListOf<Vector2>()
            (0 .. bufferSize).forEach {
                val step = ((width-20) * 1.0 / bufferSize)
                val map = map(-20.0, 0.0, height*1.0, 0.0, wave[it])
                waveStrip.add(Vector2(10+it*step, map))
            }

            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 0.5
            drawer.lineStrip(waveStrip)
        }
    }
}