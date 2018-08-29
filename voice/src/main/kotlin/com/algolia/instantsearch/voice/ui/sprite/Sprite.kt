package com.algolia.instantsearch.voice.ui.sprite

import android.graphics.Canvas

/** Something that can be drawn on a [Canvas].*/
interface Sprite {
    /** Draws this [Sprite] on the given [canvas].*/
    fun draw(canvas: Canvas)
}