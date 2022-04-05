package com.algolia.instantsearch.voice.ui

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/** A [Sprite] implemented as a Drawable. */
public class DrawableSprite(
    private val drawable: Drawable,
    private val size: Int,
    public var alpha : Int
) : Sprite {

    public val position: Point = Point(0, 0)
    public var sizeRatio: Float = 1f

    override fun draw(canvas: Canvas) {
        val offset = (size * sizeRatio / 2).toInt()

        drawable.let {
            it.alpha = alpha
            it.setBounds(
                position.x - offset,
                position.y - offset,
                position.x + offset,
                position.y + offset
            )
            it.draw(canvas)
        }
    }

    public fun explodeAlpha(vararg controls: Int): ValueAnimator =
        ValueAnimator.ofInt(*controls).apply {
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { valueAnimator ->
                alpha = valueAnimator.animatedValue as Int
            }
        }

    public fun explodeSize(vararg controls: Float): ValueAnimator =
        ValueAnimator.ofFloat(*controls).apply {
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { valueAnimator ->
                sizeRatio = valueAnimator.animatedValue as Float
            }
        }
}