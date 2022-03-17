import ddf.minim.AudioSample
import org.openrndr.events.Event
import org.openrndr.launch
import kotlinx.coroutines.delay
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.FontMap
import org.openrndr.draw.loadFont

class Kick
class Instrument(val sample: AudioSample)
class Bar(var tracks: List<Track>)

class Track(
    var sequence: List<Int>,
) {
    fun tick(instrument: Instrument, currentTick: Int) {
        if(sequence[currentTick] == 1) {
            instrument.sample.trigger()
        }
    }
}

class Sequencer (
            bpm : Double = 280.0,
            val notes : Int = 16,
            val instruments : List<Instrument> = listOf(),
            val bars : List<Bar> = listOf(),
            val sequence : List<Int> = listOf()
        ) {
            val event = Event<Kick>("trigger", true)

            val isActive = true
            var tick = 0
            var beat = 0
            var bar = 0
            var rollingBar = 0
            var interval = 0L
            var activeBar : Bar
            var activeSequence = 0

            fun setBPM(bpm: Double) {
                interval = (60000.0 / bpm).toLong()
            }

            fun start(program: Program) {
                program.launch {
                    while (isActive) {
                        delay(interval)
                        event.trigger(Kick())
                        event.deliver()
                    }
                }
            }

            init {
                setBPM(bpm)
                activeBar = bars[0]

                event.listen {
                    // set new active beat
                    beat = tick % notes

                    // patch bars
                    activeSequence = bar%sequence.size
                    rollingBar = sequence[activeSequence]
                    activeBar = bars[rollingBar]
                    bars[rollingBar].tracks.forEachIndexed { instrumentIndex, track ->
                        track.tick(instruments[instrumentIndex], beat)
                    }

                    // trigger next
                    tick += 1
                    if(tick % notes == 0) {
                        bar++
                    }
                }
            }

            fun drawSequences(drawer: Drawer, font: FontMap) {
                val width = drawer.width

                val seqBarWidth = (width / sequence.size).toDouble()
                sequence.forEachIndexed { index, it ->
                    val active = activeSequence == index
                    if (active) {
                        drawer.fill = ColorRGBa.PINK
                    } else {
                        drawer.fill = ColorRGBa.GRAY
                    }

                    drawer.rectangle(index * seqBarWidth, 0.0, seqBarWidth, 20.0)
                    drawer.fill = ColorRGBa.BLACK
                    drawer.fontMap = font
                    drawer.text("$it", 5 + index * seqBarWidth, 14.0)
                }
            }

            fun drawBars(drawer: Drawer, font: FontMap) {
                val width = drawer.width

                val barWidth = (width / bars.size).toDouble()
                (0 until bars.size).forEach {
                    val active = rollingBar == it
                    if (active) {
                        drawer.fill = ColorRGBa.PINK
                    } else {
                        drawer.fill = ColorRGBa.GRAY
                    }
                    drawer.rectangle(it * barWidth, 20.0, barWidth, 20.0)

                    drawer.fill = ColorRGBa.BLACK
                    drawer.fontMap = font
                    drawer.text("Bar $it", 5 + it * barWidth, 14.0 + 20.0)
                }
            }

            fun drawInstruments(drawer: Drawer, font: FontMap) {
                val width = drawer.width
                val height = drawer.height

                val boxWidth = (width / notes).toDouble()
                val boxHeight = ((height - 40) / instruments.size).toDouble()

                instruments.forEachIndexed { index, instrument ->
                    (0 until notes).forEach { x ->
                        val filled = activeBar.tracks[index].sequence[x] == 1
                        val active = beat == x

                        if (filled) {
                            drawer.fill = ColorRGBa.PINK
                        } else {
                            drawer.fill = ColorRGBa.GRAY
                        }

                        if (active && !filled) {
                            drawer.fill = ColorRGBa.PINK.opacify(0.5)
                        }
                        drawer.rectangle(x * boxWidth, 40 + index * boxHeight, boxWidth, boxHeight)
                    }
                }
            }
}

