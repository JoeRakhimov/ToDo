package com.joerakhimov.todo.data.source.prefs

import android.content.Context
import android.content.SharedPreferences
import com.joerakhimov.todo.ui.navigation.PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrefsModule {

    @Provides
    @Singleton
    fun providePrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

}