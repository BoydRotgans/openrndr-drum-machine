import ddf.minim.Minim
import minim.MinimObject
import org.openrndr.application
import org.openrndr.draw.loadFont


fun main() = application {
    configure {
        title = "OPENRNDR 808"
    }
    program {
        // load minim library
        val minim = Minim(MinimObject())

        // load font
        val font = loadFont("data/fonts/default.otf", 16.0)

        // create a sequencer
        val sequencer = Sequencer(
            320.0, // beats per minute
            16, // amount of notes
            listOf(  // instruments
                Instrument(minim.loadSample("data/Roland-TR-808/BD/BD0000.WAV", 512)),
                Instrument(minim.loadSample("data/Roland-TR-808/MT/MT75.WAV", 512)),
                Instrument(minim.loadSample("data/Roland-TR-808/RS/RS.WAV", 512)),
                Instrument(minim.loadSample("data/Roland-TR-808/CP/CP.WAV", 512)),
                Instrument(minim.loadSample("data/Roland-TR-808/CB/CB.WAV", 512))
            ),
            listOf( // bars
                Bar(listOf( // bar 1
                    Track(listOf(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1)), // instrument 1
                    Track(listOf(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1)), // instrument 2
                    Track(listOf(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1)), // instrument 3
                    Track(listOf(0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0)), // instrument 4
                    Track(listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))  // instrument 5
                )),
                Bar(listOf( // bar 2
                    Track(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)), // instrument 1
                    Track(listOf(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1)), // instrument 2
                    Track(listOf(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1)), // instrument 3
                    Track(listOf(0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0)), // instrument 4
                    Track(listOf(0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0))  // instrument 5
                ))
            ), // the sequence
            listOf(0, 1, 1, 0)
        )

        // start sequencer
        sequencer.start(program)

        extend {

            // draw sequence
            sequencer.drawSequences(drawer, font)

            // draw bars
            sequencer.drawBars(drawer, font)

            // draw notes
            sequencer.drawInstruments(drawer, font)
        }
    }
}