package projects.classes

import ddf.minim.Minim
import ddf.minim.analysis.FFT
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.math.map

class Marker(
    val fftIndex: Int = 0,
    val threshold: Double = 0.0
) {
    var triggered = false

    fun update(d: Double) {
        triggered = d > this.threshold
    }
}

class WaveFormFFT(
    val minim : Minim,
    val width : Int,
    val height : Int
) {
    val backgroundNoiseCompensation = mutableListOf<Double>()
    val backgroundNoiseCompensationStored = mutableListOf<Double>()
    val wave = mutableListOf<Double>()
    val microphone = minim.getLineIn(Minim.MONO, 2048, 48000f)
    val bufferSize = 1024
    val fft = FFT(microphone.bufferSize(), microphone.sampleRate())
    val markers = mutableListOf<Marker>()


    fun fixNoise() {
        backgroundNoiseCompensation.forEachIndexed { index, d ->
            backgroundNoiseCompensationStored[index] = d
        }
    }

    init {
        (0 .. bufferSize).forEach {
            wave.add(0.0)
            backgroundNoiseCompensation.add(0.0)
            backgroundNoiseCompensationStored.add(0.0)
        }


    }


    fun draw(drawer: Drawer) {

        // draw waveform
        fft.forward(microphone.mix)

        (0 .. bufferSize).forEach {
            val bandDB = Math.log(1.0 * fft.getBand(it) / fft.timeSize())
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

        markers.forEach {
            it.update(wave[it.fftIndex])

            val step = ((width-20) * 1.0 / bufferSize)
            val x = 10 + it.fftIndex * step
            val y = map(-20.0, 0.0, height*1.0, 0.0, it.threshold)
            drawer.lineSegment(
                x, y,
                x, 0.0
            )

            if(it.triggered) {
                drawer.fill = ColorRGBa.RED
            } else {
                drawer.fill = ColorRGBa.WHITE
            }
            drawer.circle( x, y, 5.0 )
        }



    }

}