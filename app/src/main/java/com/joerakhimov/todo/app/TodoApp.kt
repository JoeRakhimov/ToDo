package com.joerakhimov.todo.app

import android.app.Application

class TodoApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        DaggerAppComponent.builder()
    }

}