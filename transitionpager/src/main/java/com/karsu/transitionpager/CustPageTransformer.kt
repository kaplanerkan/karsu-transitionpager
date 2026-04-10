package com.karsu.transitionpager

import android.content.Context
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * ViewPager2 için parallax + ölçek dönüşümü uygular.
 * Orijinal [xmuSistone/ViewpagerTransition](https://github.com/xmuSistone/ViewpagerTransition)
 * projesindeki `CustPagerTransformer` sınıfının ViewPager2'ye uyarlanmış sürümüdür.
 */
class CustPageTransformer(context: Context) : ViewPager2.PageTransformer {

    private val maxTranslateOffsetX: Int =
        (180f * context.resources.displayMetrics.density + 0.5f).toInt()

    override fun transformPage(page: View, position: Float) {
        val offsetRate = position * 0.38f
        val scaleFactor = 1f - abs(offsetRate)
        if (scaleFactor > 0f) {
            page.scaleX = scaleFactor
            page.scaleY = scaleFactor
            page.translationX = -maxTranslateOffsetX * offsetRate
        }
    }
}
