package com.mayuri.rxjavaplayground

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.mayuri.rxjavaplayground.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.*

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {


    val colorList = listOf(
        Color.BLACK,
        Color.RED,
        Color.GRAY,
        Color.BLUE,
        Color.MAGENTA,
        Color.DKGRAY,
        Color.GREEN)

    lateinit var binding: ActivityMainBinding

    private var upcomingMessagesList = ArrayList<MessageModel>()
    private var consumingMessagesList = ArrayList<MessageModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerViews()
        updateCurrentTimeUsingRX()


        subscribeToUpcomingObservable()
        subscribeToConsumingObservable()

    }

    private fun setUpRecyclerViews() {

        binding.rvUpcomingMessage.layoutManager = LinearLayoutManager(this)
        binding.rvUpcomingMessage.adapter = MessagesRecyclerView(upcomingMessagesList)

        binding.rvConsumedMessage.layoutManager = LinearLayoutManager(this)
        binding.rvConsumedMessage.adapter = MessagesRecyclerView(consumingMessagesList)

    }


    private fun subscribeToUpcomingObservable() {
        val messageStreamObserver = createObservableMessageStream();
        messageStreamObserver
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messageModel ->

                upcomingMessagesList.add(messageModel)
                binding.rvUpcomingMessage.adapter?.notifyDataSetChanged()


            }, {
                println("Received error -${it.message}")

            }, {

            }, {
                println("on subscribed")

            })
    }

    private fun subscribeToConsumingObservable() {
        val messageStreamObserver = createObservableConsumingMessageStream();
        messageStreamObserver
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messages ->
                println("Received on - ${Thread.currentThread().name}")
                consumingMessagesList.add(messages)
                binding.rvConsumedMessage.adapter?.notifyDataSetChanged()
                binding.rvUpcomingMessage.adapter?.notifyDataSetChanged()
                binding.rvConsumedMessage.scrollToPosition( binding.rvUpcomingMessage.size)

            }, {
                println("Received error Consuming -${it.message}")

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
                while (true) {
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
                    observer.onNext(message)


                }
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun createObservableConsumingMessageStream(): Observable<MessageModel> {
        return Observable.create() { observer ->

            GlobalScope.launch() {
                while (true) {
                    val messages: ArrayList<MessageModel> = ArrayList()
                    messages.addAll(upcomingMessagesList)

                    messages.forEachIndexed { index, i ->
                        if (i!=null && i.date.time - Date().time <= 100) {
                            upcomingMessagesList.removeAt(index)
                            observer.onNext(i)

                        }
                    }

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
        Observable.interval(10L, TimeUnit.MILLISECONDS)
            .timeInterval()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.currentTime.text = getCurrentTimeInString()
            }
    }


}