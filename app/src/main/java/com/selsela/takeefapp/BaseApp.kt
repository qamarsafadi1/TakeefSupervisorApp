package com.selsela.takeefapp

import android.app.Application
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.orhanobut.hawk.Hawk
import com.selsela.takeefapp.ui.splash.ConfigViewModel
import com.selsela.takeefapp.utils.LocalData
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp

import java.util.*

@HiltAndroidApp
class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Hawk.init(this).build()
        Lingver.init(this, LocalData.appLocal)
        Lingver.getInstance().setLocale(this, LocalData.appLocal)

    }
}