package com.mayuri.rxjavaplayground

import android.annotation.SuppressLint
import android.content.Context
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable


import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import java.util.concurrent.TimeUnit

object SimpleRx {

    @SuppressLint("CheckResult")
    fun createSimpleRx() {


        val value = BehaviorRelay.createDefault("mayuri")

        value.accept("mayuri dev")

        value.subscribe { newValue ->

            println(newValue)

        }

        value.accept("mayuri app")
        value.accept("mayuri k")




    }



    fun subjects() {
        val behaviorSubject = BehaviorSubject.createDefault(24)

        val disposable = behaviorSubject.subscribe({ newValue -> //onNext
            println("🕺 behaviorSubject subscription: $newValue")
        }, { error -> //onError
            println("🕺 error: ${ error.localizedMessage }")
        }, { //onCompleted
            println("🕺 completed")
        }, { disposable -> //onSubscribed
            println("🕺 subscribed")
        })

        behaviorSubject.onNext(34)
        behaviorSubject.onNext(48)
        behaviorSubject.onNext(48) //duplicates show as new events by default

        //1 onError
//        val someException = IllegalArgumentException("some fake error")
//        behaviorSubject.onError(someException)
//        behaviorSubject.onNext(109) //will never show

        //2 onComplete
        behaviorSubject.onComplete()
        behaviorSubject.onNext(10983) //will never show

    }




}