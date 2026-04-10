package com.karsu.transitionpager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * Tek noktadan kullanılabilen pager bileşeni.
 *
 * Kullanım:
 * ```kotlin
 * val pager = findViewById<TransitionPagerView>(R.id.pager)
 * pager.setItems(items)
 * pager.onItemClick = { item, position, shared ->
 *     // shared.image -> shared element transition için ImageView
 * }
 * ```
 */
class TransitionPagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val viewPager: ViewPager2
    private val indicatorView: TextView

    private var items: List<TransitionItem> = emptyList()

    /** Tıklama callback'i. [SharedViews] üzerinden shared element transition kurulabilir. */
    var onItemClick: ((item: TransitionItem, position: Int, shared: SharedViews) -> Unit)? = null

    /** Sayfa değiştikçe tetiklenen opsiyonel dinleyici. */
    var onPageSelected: ((position: Int) -> Unit)? = null

    /** Göstergenin görünürlüğü. */
    var isIndicatorVisible: Boolean
        get() = indicatorView.visibility == View.VISIBLE
        set(value) {
            indicatorView.visibility = if (value) View.VISIBLE else View.GONE
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.tp_view_transition_pager, this, true)
        viewPager = findViewById(R.id.tp_viewpager)
        indicatorView = findViewById(R.id.tp_indicator)

        viewPager.setPageTransformer(CustPageTransformer(context))
        viewPager.offscreenPageLimit = 3

        val peekPadding = (48f * resources.displayMetrics.density + 0.5f).toInt()
        (viewPager.getChildAt(0) as? RecyclerView)?.apply {
            setPadding(peekPadding, 0, peekPadding, 0)
            clipToPadding = false
            clipChildren = false
        }
        viewPager.clipChildren = false
        viewPager.clipToPadding = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicator()
                this@TransitionPagerView.onPageSelected?.invoke(position)
            }
        })
    }

    fun setItems(items: List<TransitionItem>) {
        this.items = items
        viewPager.adapter = TransitionPageAdapter(items) { position, holder ->
            val cb = onItemClick ?: return@TransitionPageAdapter
            val shared = SharedViews(
                image = holder.imageView,
                title = holder.titleView,
                subtitle = holder.subtitleView,
                rating = holder.ratingBar,
            )
            cb(items[position], position, shared)
        }
        updateIndicator()
    }

    /** Mevcut seçili sayfa indeksi. */
    var currentItem: Int
        get() = viewPager.currentItem
        set(value) {
            viewPager.currentItem = value
        }

    private fun updateIndicator() {
        if (items.isEmpty()) {
            indicatorView.text = ""
            return
        }
        val current = viewPager.currentItem + 1
        indicatorView.text = context.getString(R.string.tp_indicator_format, current, items.size)
    }

    /** Shared element transition için kullanılabilecek view referansları. */
    data class SharedViews(
        val image: ImageView,
        val title: TextView,
        val subtitle: TextView,
        val rating: RatingBar,
    )
}
