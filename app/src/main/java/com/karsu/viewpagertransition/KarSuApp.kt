package com.karsu.viewpagertransition

import android.app.Application
import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient

/**
 * Coil'in global [ImageLoader]'ını Wikimedia'nın [User-Agent politikasına]
 * (https://meta.wikimedia.org/wiki/User-Agent_policy) uyan bir OkHttp client
 * ile kurar. Bu olmadan Wikimedia 403 döndürür ve resimler yüklenmez.
 */
class KarSuApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", USER_AGENT)
                    .build()
                chain.proceed(request)
            }
            .build()

        val imageLoader = ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .crossfade(true)
            .build()

        Coil.setImageLoader(imageLoader)
    }

    companion object {
        private const val USER_AGENT =
            "KarSuTransitionPager/1.0 " +
                "(https://github.com/kaplanerkan/karsu-transitionpager; kaplanerkan@github.com)"
    }
}
