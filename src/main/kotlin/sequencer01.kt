import ddf.minim.Minim
import kotlinx.coroutines.delay
import minim.MinimObject
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.events.Event
import org.openrndr.launch

fun main() = application {
    configure {
        title = "OPENRNDR 808"
    }
    program {
        val minim = Minim(MinimObject())

        class Kick()

        class Instrument(
            samplePath: String,
        ) {
            var sample = minim.loadSample(samplePath, 512)
        }

        class Track(
            var sequence: List<Int>,
        ) {
            fun tick(instrument: Instrument, currentTick: Int) {
                if(sequence[currentTick] == 1) {
                    instrument.sample.trigger()
                }
            }
        }

        class Bar(var tracks: List<Track>)

        class Sequencer(
            bpm : Double = 280.0,
            val instruments : List<Instrument>,
            val bars : List<Bar>
        ) {
            val event = Event<Kick>("trigger", true)
            val isActive = true

            var tick = 0
            val buckets = 4
            var beat = 0
            var bar = 0
            var rollingBar = 0
            var interval = 0L
            var activeBar : Bar

            fun setBPM(bpm: Double) {
                interval = (60000.0 / bpm).toLong()
            }

            init {
                setBPM(bpm)
                activeBar = bars[0]

                launch {
                    while (isActive) {
                        delay(interval)
                        event.trigger(Kick())
                        event.deliver()
                    }
                }

                event.listen {
                    // set new active beat
                    beat = tick % buckets

                    // patch bars
                    rollingBar = bar%bars.size
                    activeBar = bars[rollingBar]
                    bars[rollingBar].tracks.forEachIndexed { instrumentIndex, track ->
                        track.tick(instruments[instrumentIndex], beat)
                    }

                    tick += 1
                    if(tick % buckets == 0) {
                        bar++
                    }
                }
            }
        }

        val sequencer = Sequencer(
            290.0,
            listOf(
                Instrument("data/Roland-TR-808/BD/BD0000.WAV"),
                Instrument("data/Roland-TR-808/MT/MT75.WAV"),
                Instrument("data/Roland-TR-808/RS/RS.WAV"),
                Instrument("data/Roland-TR-808/CP/CP.WAV")
            ),
            listOf(
                Bar(listOf(
                        Track(listOf(0, 0, 0, 0)),
                        Track(listOf(0, 1, 0, 1)),
                        Track(listOf(0, 0, 0, 0)),
                        Track(listOf(0, 0, 0, 0))
                )),
                Bar(listOf(
                    Track(listOf(0, 0, 0, 0)),
                    Track(listOf(0, 1, 0, 1)),
                    Track(listOf(0, 0, 0, 0)),
                    Track(listOf(0, 1, 0, 1))
                )),
                Bar(listOf(
                    Track(listOf(0, 0, 0, 0)),
                    Track(listOf(0, 0, 0, 0)),
                    Track(listOf(0, 0, 0, 0)),
                    Track(listOf(0, 0, 1, 1))
                )),
                Bar(listOf(
                    Track(listOf(0, 0, 0, 1)),
                    Track(listOf(0, 0, 1, 0)),
                    Track(listOf(0, 1, 0, 0)),
                    Track(listOf(1, 0, 1, 1))
                )),
            )
        )

        extend {
            // draw active bar
            val barWidth = (width / sequencer.bars.size).toDouble()
            (0 until sequencer.bars.size).forEach {
                val active = sequencer.rollingBar == it
                if(active) {
                    drawer.fill = ColorRGBa.YELLOW
                } else {
                    drawer.fill = ColorRGBa.GRAY
                }
                drawer.rectangle(it * barWidth, 0.0, barWidth, 20.0)
            }

            // draw sequencer
            val boxWidth = (width / sequencer.buckets).toDouble()
            val boxHeight = ((height-20) / sequencer.instruments.size).toDouble()

            sequencer.instruments.forEachIndexed { index, instrument ->
                (0 until sequencer.buckets).forEach { x ->

                    val filled = sequencer.activeBar.tracks[index].sequence[x] == 1
                    val active = sequencer.beat == x

                    if(filled) {
                        drawer.fill = ColorRGBa.PINK
                    } else {
                        drawer.fill = ColorRGBa.GRAY
                    }

                    if(active && !filled) {
                        drawer.fill = ColorRGBa.PINK.opacify(0.5)
                    }
                    drawer.rectangle(x * boxWidth, 20 + index*boxHeight, boxWidth, boxHeight)
                }
            }

        }
    }
}