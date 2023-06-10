package edu.skku.cs.bitcointothemoon

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import timber.log.Timber

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        KakaoSdk.init(this, "Kakao 네이티브 앱 키")
    }
}