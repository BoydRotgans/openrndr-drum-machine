import ddf.minim.AudioPlayer
import ddf.minim.Minim
import kotlinx.coroutines.delay
import minim.MinimObject
import org.openrndr.application
import org.openrndr.events.Event
import org.openrndr.extra.timer.timeOut
import org.openrndr.launch


fun main() = application {
    configure {
        width = 1280
        height = 720
    }
    program {

        val minim = Minim(MinimObject())

        class Sequencer {
            val event = Event<Any?>().postpone(true)

            init {

//                repeat(2) {
//                    event.trigger("kick")
//                    println("ola")
//                }

//                val task: TimerTask = object : TimerTask() {
//                    override fun run() {
//                        println(
//                            "Task performed on: " + Date() + "n" +
//                                    "Thread's name: " + Thread.currentThread().name
//                        )
//                        trigger()
//                    }
//                }
//
//                Timer().scheduleAtFixedRate(task, 0, 1000)
            }
        }

        val base1 = minim.loadSample("data/Roland-TR-808/BD/BD0000.WAV", 512)
        val base2 = minim.loadSample("data/Roland-TR-808/HC/HC10.WAV", 512)


        var sequencer = Sequencer()

        keyboard.keyDown.listen {
            if(it.name == "h") {
                base1.trigger()
            }
            if(it.name == "j") {
                base2.trigger()
            }
        }

        repeat(2) {
            println("ola $it")

        }

//        fun kick() {
//            timeOut(0.5) {
//                base2.play()
//                base2.rewind()
//                println("kick")
//                kick()
//            }
//        }
//
//        kick()
        var once = true
        extend {

            if (once) {
                once = false
                launch {
                    for (i in 0 until 100) {
                        base1.trigger()
                        if(i%4==0) {
                            base2.trigger()
                        }

                        delay(450)
                    }
                }
            }



        }
    }
}