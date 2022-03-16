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

        class Kick( var name: String)

        class Track(
            samplePath: String,
            var sequence: List<Int>,
        ) {
            var sample = minim.loadSample(samplePath, 512)

            fun tick(currentTick: Int) {
                if(sequence[currentTick] == 1) {
                    sample.trigger()
                }
            }
        }

        class Sequencer(

        ) {
            val event = Event<Kick>("trigger", true)
            val isActive = true

            var tick = 0
            val buckets = 4
            var beat = 0

            var interval = 0L
            val tracks = mutableListOf<Track>()

            fun setBPM(bpm: Double) {
                interval = (60000.0 / bpm).toLong()
            }

            init {
                setBPM(280.0)

                launch {
                    while (isActive) {
                        // set interval
                        delay(interval)
                        event.trigger(Kick("HC00"))
                        event.deliver()
                    }
                }

                event.listen {
                    // set new active beat
                    beat = tick % buckets

                    // patch tracks
                    tracks.forEach {
                        it.tick(beat)
                    }
                    tick += 1
                }
            }
        }

        val sequencer = Sequencer()
        sequencer.tracks.add(Track("data/Roland-TR-808/BD/BD0000.WAV", listOf(0, 1, 0, 0)))
        sequencer.tracks.add(Track("data/Roland-TR-808/MT/MT75.WAV", listOf(0, 0, 1, 0)))
        sequencer.tracks.add(Track("data/Roland-TR-808/RS/RS.WAV", listOf(0, 0, 0, 1)))
        sequencer.tracks.add(Track("data/Roland-TR-808/CP/CP.WAV", listOf(1, 0, 1, 0)))
        sequencer.tracks.add(Track("data/Roland-TR-808/HC/HC75.WAV", listOf(1, 0, 1, 0)))

        extend {
            val boxWidth = (width / sequencer.buckets).toDouble()
            val boxHeight = (height / sequencer.tracks.size).toDouble()

            // draw sequencer
            sequencer.tracks.forEachIndexed { index, track ->
                (0 until sequencer.buckets).forEach { x ->
                    if (x == sequencer.beat) {
                        drawer.fill = ColorRGBa.PINK
                    } else {
                        if(track.sequence[x] == 1) {
                            drawer.fill = ColorRGBa.WHITE
                        } else {
                            drawer.fill = ColorRGBa.GRAY
                        }
                    }
                    drawer.rectangle(x * boxWidth, index*boxHeight, boxWidth, boxHeight)
                }
            }

        }
    }
}