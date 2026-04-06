package com.cute.anime.avatarmaker

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.cute.anime.avatarmaker.ui.splash.SplashActivity
import com.cute.anime.avatarmaker.utils.music.MusicLocal
import com.lvt.ads.util.AdsApplication
import com.lvt.ads.util.AppOpenManager
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
@HiltAndroidApp
class App  : AdsApplication()  {
    companion object {
        lateinit var instance:App
            private set
        val context: Context
            get() = instance.applicationContext
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                // App vào foreground → mở lại nhạc
                if (!MusicLocal.isInSplashOrTutorial && MusicLocal.home)
                    MusicLocal.play(context)

            }
            override fun onStop(owner: LifecycleOwner) {
                // App ra background → tạm dừng nhạc
                MusicLocal.pause()
            }
        })
    }
    override fun enableAdsResume(): Boolean {
        return true
    }

    override fun getListTestDeviceId(): MutableList<String>? {
        return null
    }

    override fun getResumeAdId(): String {
        return getString(R.string.open_resume)
    }

    override fun buildDebug(): Boolean {
        return true
    }

}