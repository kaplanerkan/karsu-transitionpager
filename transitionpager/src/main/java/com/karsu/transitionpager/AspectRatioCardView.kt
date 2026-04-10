package com.karsu.transitionpager

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView

/**
 * Genişliğine oranla sabit bir yüksekliğe sahip kart. Varsayılan olarak
 * yükseklik = genişlik * 1.2f. [ratio] ile değiştirilebilir.
 */
class AspectRatioCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle,
) : MaterialCardView(context, attrs, defStyleAttr) {

    var ratio: Float = 1.2f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (ratio > 0f) {
            val ratioHeight = (measuredWidth * ratio).toInt()
            setMeasuredDimension(measuredWidth, ratioHeight)
            layoutParams?.let {
                it.height = ratioHeight
                layoutParams = it
            }
        }
    }
}
