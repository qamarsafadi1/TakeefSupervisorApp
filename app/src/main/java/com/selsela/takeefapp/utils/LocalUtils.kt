package com.selsela.takeefapp.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocalUtils {
    fun setLocale(lang: String) {
        LocalData.appLocal = lang
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(LocalData.appLocal)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}