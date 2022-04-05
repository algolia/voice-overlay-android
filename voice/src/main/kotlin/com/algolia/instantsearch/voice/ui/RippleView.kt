package com.algolia.instantsearch.voice.ui

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.algolia.instantsearch.voice.R

/** A View displaying a ripple effect. */
public class RippleView : View {

    public enum class State {
        None,
        Playing,
        Stopped
    }

    public constructor(context: Context) : super(context)

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    /** All circles used in this RippleView, generated during [init]. */
    private var circles = listOf<DrawableSprite>()

    /** Current animations, filled when [State.Playing] triggers animation and emptied when [State.Stopped]. */
    private val animations = mutableMapOf<Int, AnimatorSet>()

    /** This runnable invalidate the view at 60 fps. */
    private val runnableFps = object : Runnable {

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

    public fun start() {
        state = State.Playing
    }

    public fun cancel() {
        state = State.Stopped
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circles.forEach { drawableSprite ->
            drawableSprite.position.let {
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
    public object Opacity {

        public const val p100: Int = 0xFF
        public const val p95: Int = 0xF2
        public const val p90: Int = 0xE6
        public const val p85: Int = 0xD9
        public const val p80: Int = 0xCC
        public const val p75: Int = 0xBF
        public const val p70: Int = 0xB3
        public const val p65: Int = 0xA6
        public const val p60: Int = 0x99
        public const val p55: Int = 0x8C
        public const val p50: Int = 0x80
        public const val p45: Int = 0x73
        public const val p40: Int = 0x66
        public const val p35: Int = 0x59
        public const val p30: Int = 0x4D
        public const val p25: Int = 0x40
        public const val p20: Int = 0x33
        public const val p15: Int = 0x26
        public const val p10: Int = 0x1A
        public const val p5: Int = 0x0D
        public const val p0: Int = 0x00
    }
}