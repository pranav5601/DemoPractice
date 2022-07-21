package com.example.demopractice

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity() {

    public final val SECONDS_TO_COUNT = 5;
    private lateinit var mCustomHandler: CustomHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clickListeners()


    }

    private fun clickListeners() {

        val btn = findViewById<Button>(R.id.btn_send_job)
        btn.setOnClickListener {
            sendJob()
        }

    }


    override fun onStart() {
        super.onStart()
        mCustomHandler = CustomHandler()
        mCustomHandler.initWorkerThread()
    }

    override fun onStop() {
        super.onStop()

        mCustomHandler.stop()
    }



    private fun sendJob() {
        val job: Runnable = Runnable {

            for (i in 0 until SECONDS_TO_COUNT) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    return@Runnable
                }
                Log.d("CustomHandler", "iteration: $i")
            }
        }
        mCustomHandler.post(job)


    }

     class CustomHandler(){
        val mQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
         val poison: Runnable = Runnable {}

          fun initWorkerThread() {

            Thread(Runnable {
                while (true) {

                    val runnable: Runnable
                    try {
                        runnable = mQueue.take()
                    } catch (e: InterruptedException) {
                        return@Runnable
                    }
                    if (runnable == poison) {
                        return@Runnable
                    }
                    runnable.run()
                }
            }).start()
        }

        fun stop(){
            mQueue.clear()
            mQueue.add(poison)
        }
        fun post(runnable: Runnable){
            mQueue.add(runnable)
        }
    }

}