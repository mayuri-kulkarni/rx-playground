package com.mayuri.rxjavaplayground

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.provider.Contacts
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.color
import com.mayuri.rxjavaplayground.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.*

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {


    val colorList = listOf(
        Color.BLACK,
        Color.RED,
        Color.GRAY,
        Color.BLUE,
        Color.MAGENTA
    )

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservable()
        updateCurrentTimeUsingRX()
    }


    private fun subscribeToObservable() {
        val messageStreamObserver = createObservableMessageStream();
        messageStreamObserver
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messageModel ->
                println("Received - ${messageModel.message}")
                println("Received on - ${Thread.currentThread().name}")

                val s = SpannableStringBuilder()
                    .color(messageModel.color) { append(messageModel.message) }
                    .append("\n")
                binding.upcomingMessage.append(s)

                //Emit messages when the difference time of the message and current time is <=100ms

                println("Received with difference - ${messageModel.date.time - Date().time}")

            }, {
                println("Received error -${it.message}")

            }, {

            }, {
                println("on subscribed")

            })
    }

    @SuppressLint("SimpleDateFormat")
    private fun createObservableMessageStream(): Observable<MessageModel> {


        return Observable.create() { observer ->
            println("🍄 ~~ Observable logic being triggered ~~")


            GlobalScope.launch() {

                for (i in 0..12) {
                    delay(4000) //artificial delay 1 second
                    println("emitting on - ${Thread.currentThread().name}")

                    val dateTime = Date()
                    dateTime.time = dateTime.time.plus(10000)
                    val simpleFormatter = SimpleDateFormat("hh : mm : ss : SSS")
                    val randomNum = (colorList.indices).random()

                    val message = MessageModel(
                        simpleFormatter.format(dateTime.time),
                        dateTime,
                        colorList.get(randomNum)
                    )

                    println("${i} message -   : ${message.message} color -  ${message.color}")
                    observer.onNext(message)


                }
            }
        }

    }


    fun getCurrentTimeInString(): String {
        var dateTime: Date = Date()
        var simpleFormatter = SimpleDateFormat("hh : mm : ss : SSS")
        return simpleFormatter.format(dateTime.time)
    }

    fun updateCurrentTimeUsingRX() {
        Observable.interval(1000L, TimeUnit.MILLISECONDS)
            .timeInterval()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.currentTime.text = getCurrentTimeInString()
            }
    }


}