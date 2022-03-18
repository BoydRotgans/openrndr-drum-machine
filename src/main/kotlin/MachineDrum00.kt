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
            120.0, // beats per minute
            4, // amount of notes
            listOf(  // instruments
                Instrument(minim.loadSample("data/Roland-TR-808/BD/BD0000.WAV", 512))
            ),
            listOf( // bars
                Bar(listOf(
                    Track(listOf(0, 1, 0, 1))
                ))
            ), // the sequence
            listOf(0)
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