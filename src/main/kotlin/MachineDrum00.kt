import ddf.minim.Minim
import minim.MinimObject
import org.openrndr.application
import org.openrndr.draw.loadFont


fun main() = application {
    configure {
        title = "OPENRNDR 808"
        windowResizable = true
    }
    program {
        // load minim library
        val minim = Minim(MinimObject())

        // load font
        val font = loadFont("data/fonts/default.otf", 16.0)

        // create a sequencer
        val sequencer = Sequencer(
            240.0, // beats per minute
            4, // amount of notes
            listOf(  // instruments
                Instrument(minim.loadSample("data/Roland-TR-808/BD/BD0000.WAV", 512)),
                Instrument(minim.loadSample("data/Roland-TR-808/CY/CY0010.WAV", 512))
            ),
            listOf( // bars
                Bar(listOf( // 0
                    Track(listOf(0, 1, 0, 1)), // first instrument
                    Track(listOf(1, 0, 1, 0)), // second instrument
                )),
                Bar(listOf( // 1
                    Track(listOf(1, 1, 1, 1)), // first instrument
                    Track(listOf(0, 0, 1, 0)), // second instrument
                )),
                Bar(listOf( // 2
                    Track(listOf(1, 1, 1, 1)), // first instrument
                    Track(listOf(1, 1, 1, 0)), // second instrument
                ))
            ), // the sequence
            listOf(0, 1, 0, 2)
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