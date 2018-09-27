package com.algolia.instantsearch.voice.ui

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.algolia.instantsearch.voice.R

/** A View displaying a ripple effect. */
class RippleView : View {

    enum class State {
        None,
        Playing,
        Stopped
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    /** All circles used in this RippleView, generated during [init]. */
    private var circles = listOf<DrawableSprite>()
    /** Current animations, filled when [State.Playing] triggers [animation] and emptied when [State.Stopped]. */
    private val animations = mutableMapOf<Int, AnimatorSet>()

    /** This runnable invalidate the view at 60 fps. */
    private val runnableFps = object : java.lang.Runnable {

        override fun run() {
            invalidate()
            postOnAnimation(this)
        }
    }

    /** This runnable generates an animation at each [interval][circleCount]. */
    private val runnableAnimation = object : Runnable {

        private var index = 0

        override fun run() {
            animations[index] = circles[index].circleAnimation().also { it.start() }
            index = if (index == circles.lastIndex) 0 else index + 1
            postOnAnimationDelayed(this, delay)
        }
    }

    private var delay: Long = 0L
    private var duration: Long = 0L
    private var circleCount: Long = 0L
    private var radius: Float = 0f
    private var size: Int = 0

    private var state = State.None
        set(value) {
            field = value
            when (value) {
                State.Playing -> {
                    animations.values.forEach { it.end() }
                    animations.clear()
                    postOnAnimationDelayed(runnableAnimation, delay)
                }
                State.Stopped -> removeCallbacks(runnableAnimation)
                State.None -> Unit
            }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postOnAnimation(runnableFps)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(runnableAnimation)
        removeCallbacks(runnableFps)
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.RippleView, 0, 0).also {
            val drawable = it.getDrawable(R.styleable.RippleView_drawable)!!
            delay = it.getInt(R.styleable.RippleView_delay, 500).toLong()
            duration = it.getInt(R.styleable.RippleView_duration, 500).toLong()
            size = it.getDimensionPixelSize(R.styleable.RippleView_size, 0)
            radius = it.getFloat(R.styleable.RippleView_radius, 1f)
            circleCount = duration / delay

            circles = (0 until circleCount).map { DrawableSprite(drawable, size, Opacity.p0) }
        }.recycle()
    }

    private fun DrawableSprite.circleAnimation(): AnimatorSet =
        AnimatorSet().also {
            it.duration = duration
            it.playTogether(
                explodeAlpha(Opacity.p0, Opacity.p30, Opacity.p15, Opacity.p0),
                explodeSize(1f, radius)
            )
        }

    fun start() {
        state = State.Playing
    }

    fun cancel() {
        state = State.Stopped
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circles.forEach {
            it.position.let {
                it.x = w / 2
                it.y = h / 2
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        circles.forEach { it.draw(canvas) }
    }

    /***
     * Opacity values from 0% to 100%, extracted from [Recommune/sugar][https://github.com/Recommune/sugar]
     * until we use enough to justify adding it as a dependency.
     */
    object Opacity {

        const val p100 = 0xFF
        const val p95 = 0xF2
        const val p90 = 0xE6
        const val p85 = 0xD9
        const val p80 = 0xCC
        const val p75 = 0xBF
        const val p70 = 0xB3
        const val p65 = 0xA6
        const val p60 = 0x99
        const val p55 = 0x8C
        const val p50 = 0x80
        const val p45 = 0x73
        const val p40 = 0x66
        const val p35 = 0x59
        const val p30 = 0x4D
        const val p25 = 0x40
        const val p20 = 0x33
        const val p15 = 0x26
        const val p10 = 0x1A
        const val p5 = 0x0D
        const val p0 = 0x00
    }
}