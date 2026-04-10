package com.karsu.transitionpager

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs

/**
 * İki çocuğu olan FrameLayout:
 *  - Index 0: arkada gizlenen "bottom" görünüm (bilgi paneli)
 *  - Index 1: dikey olarak sürüklenebilen "top" görünüm (kart/ımage)
 *
 * Üstteki kart yukarı çekildiğinde alttaki görünüm fade-in + scale ile belirir.
 * Kart'a tıklandığında:
 *  - Kapalıysa açılır
 *  - Zaten açıksa [gotoDetailListener] çağrılır.
 *
 * Orijinal Java sürümünün bire bir Kotlin çevirisidir; davranış korunmuştur.
 */
class DragLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val bottomDragVisibleHeight: Int
    private val bottomExtraIndicatorHeight: Int
    private var dragTopDest: Int = 0

    private val dragHelper: ViewDragHelper
    private val moveDetector: GestureDetectorCompat
    private val touchSlop: Int

    private var originX: Int = 0
    private var originY: Int = 0
    private var downState: Int = STATE_CLOSE

    private var bottomView: View? = null
    private var topView: View? = null

    /** Kart açıkken tıklanınca veya açık haldeyken yukarı sürüklenince tetiklenir. */
    var gotoDetailListener: (() -> Unit)? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DragLayout, 0, 0)
        try {
            bottomDragVisibleHeight =
                a.getDimension(R.styleable.DragLayout_bottomDragVisibleHeight, 0f).toInt()
            bottomExtraIndicatorHeight =
                a.getDimension(R.styleable.DragLayout_bottomExtraIndicatorHeight, 0f).toInt()
        } finally {
            a.recycle()
        }
        dragHelper = ViewDragHelper.create(this, 10f, DragHelperCallback()).apply {
            setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP)
        }
        moveDetector = GestureDetectorCompat(context, MoveDetector()).apply {
            setIsLongpressEnabled(false)
        }
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        bottomView = getChildAt(0)
        topView = getChildAt(1)

        topView?.setOnClickListener {
            val top = topView ?: return@setOnClickListener
            if (currentState == STATE_CLOSE) {
                if (dragHelper.smoothSlideViewTo(top, originX, dragTopDest)) {
                    ViewCompat.postInvalidateOnAnimation(this)
                }
            } else {
                gotoDetailListener?.invoke()
            }
        }
    }

    private inner class DragHelperCallback : ViewDragHelper.Callback() {

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            if (changedView === topView) processLinkageView()
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean = child === topView

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val currentTop = child.top
            if (top > child.top) {
                return currentTop + (top - currentTop) / 2
            }
            return when {
                currentTop > DECELERATE_THRESHOLD * 3 -> currentTop + (top - currentTop) / 2
                currentTop > DECELERATE_THRESHOLD * 2 -> currentTop + (top - currentTop) / 4
                currentTop > 0 -> currentTop + (top - currentTop) / 8
                currentTop > -DECELERATE_THRESHOLD -> currentTop + (top - currentTop) / 16
                currentTop > -DECELERATE_THRESHOLD * 2 -> currentTop + (top - currentTop) / 32
                currentTop > -DECELERATE_THRESHOLD * 3 -> currentTop + (top - currentTop) / 48
                else -> currentTop + (top - currentTop) / 64
            }
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = child.left

        override fun getViewHorizontalDragRange(child: View): Int = 600
        override fun getViewVerticalDragRange(child: View): Int = 600

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            var finalY = originY
            if (downState == STATE_CLOSE) {
                if (originY - releasedChild.top > DRAG_SWITCH_DISTANCE_THRESHOLD ||
                    yvel < -DRAG_SWITCH_VEL_THRESHOLD
                ) {
                    finalY = dragTopDest
                }
            } else {
                val gotoBottom =
                    releasedChild.top - dragTopDest > DRAG_SWITCH_DISTANCE_THRESHOLD ||
                        yvel > DRAG_SWITCH_VEL_THRESHOLD
                if (!gotoBottom) {
                    finalY = dragTopDest
                    if (dragTopDest - releasedChild.top > touchSlop) {
                        gotoDetailListener?.invoke()
                        postResetPosition()
                        return
                    }
                }
            }
            if (dragHelper.smoothSlideViewTo(releasedChild, originX, finalY)) {
                ViewCompat.postInvalidateOnAnimation(this@DragLayout)
            }
        }
    }

    private fun postResetPosition() {
        postDelayed({
            val top = topView ?: return@postDelayed
            top.offsetTopAndBottom(dragTopDest - top.top)
        }, 500)
    }

    private fun processLinkageView() {
        val top = topView ?: return
        val bottom = bottomView ?: return
        if (top.top > originY) {
            bottom.alpha = 0f
        } else {
            var alpha = (originY - top.top) * 0.01f
            if (alpha > 1f) alpha = 1f
            bottom.alpha = alpha
            val maxDistance = originY - dragTopDest
            val currentDistance = top.top - dragTopDest
            var scaleRatio = 1f
            if (maxDistance != 0 && currentDistance > 0) {
                val distanceRatio = currentDistance.toFloat() / maxDistance
                scaleRatio =
                    MIN_SCALE_RATIO + (MAX_SCALE_RATIO - MIN_SCALE_RATIO) * (1f - distanceRatio)
            }
            bottom.scaleX = scaleRatio
            bottom.scaleY = scaleRatio
        }
    }

    private inner class MoveDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            dx: Float,
            dy: Float,
        ): Boolean = abs(dy) + abs(dx) > touchSlop
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private val currentState: Int
        get() {
            val top = topView ?: return STATE_CLOSE
            return if (abs(top.top - dragTopDest) <= touchSlop) STATE_EXPANDED else STATE_CLOSE
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!changed) return
        super.onLayout(changed, left, top, right, bottom)
        val topV = topView ?: return
        val bottomV = bottomView ?: return
        originX = topV.x.toInt()
        originY = topV.y.toInt()
        dragTopDest = bottomV.bottom - bottomDragVisibleHeight - topV.measuredHeight
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val yScroll = moveDetector.onTouchEvent(ev)
        val shouldIntercept = runCatching { dragHelper.shouldInterceptTouchEvent(ev) }.getOrDefault(false)
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            downState = currentState
            runCatching { dragHelper.processTouchEvent(ev) }
        }
        return shouldIntercept && yScroll
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val topV = topView ?: return
        val bottomV = bottomView ?: return
        val bottomMarginTop =
            (bottomDragVisibleHeight + topV.measuredHeight / 2 - bottomV.measuredHeight / 2) / 2 -
                bottomExtraIndicatorHeight
        val lp = bottomV.layoutParams as LayoutParams
        lp.setMargins(0, bottomMarginTop, 0, 0)
        bottomV.layoutParams = lp
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        runCatching { dragHelper.processTouchEvent(e) }
        return true
    }

    companion object {
        private const val DECELERATE_THRESHOLD = 120
        private const val DRAG_SWITCH_DISTANCE_THRESHOLD = 100
        private const val DRAG_SWITCH_VEL_THRESHOLD = 800
        private const val MIN_SCALE_RATIO = 0.5f
        private const val MAX_SCALE_RATIO = 1.0f
        private const val STATE_CLOSE = 1
        private const val STATE_EXPANDED = 2
    }
}
