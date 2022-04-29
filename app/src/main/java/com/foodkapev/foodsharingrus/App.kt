package com.foodkapev.foodsharingrus

import android.app.Application
import com.appsflyer.AppsFlyerLib
import com.flurry.android.FlurryAgent
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import timber.log.Timber
import timber.log.Timber.DebugTree

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        val config = YandexMetricaConfig.newConfigBuilder(Constants.APP_METRICA_API).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)

        FlurryAgent.Builder()
            .withLogEnabled(true)
            .build(this, Constants.FLURRY_API)

        AppsFlyerLib.getInstance().init(Constants.APPSFLYER_API, null, this)
        AppsFlyerLib.getInstance().start(this)
    }
}