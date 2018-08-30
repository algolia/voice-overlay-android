package com.algolia.instantsearch.voice.ui

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.support.v4.view.animation.FastOutSlowInInterpolator

/** A [Sprite] implemented as a Drawable. */
class DrawableSprite(
    private val drawable: Drawable,
    private val size: Int,
    var alpha : Int
) : Sprite {

    val position: Point = Point(0, 0)
    var sizeRatio = 1f

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

    fun explodeAlpha(vararg controls: Int): ValueAnimator =
        ValueAnimator.ofInt(*controls).apply {
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { valueAnimator ->
                alpha = valueAnimator.animatedValue as Int
            }
        }

    fun explodeSize(vararg controls: Float): ValueAnimator =
        ValueAnimator.ofFloat(*controls).apply {
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { valueAnimator ->
                sizeRatio = valueAnimator.animatedValue as Float
            }
        }
}