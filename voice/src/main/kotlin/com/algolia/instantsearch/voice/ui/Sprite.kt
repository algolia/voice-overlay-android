package com.algolia.instantsearch.voice.ui

import android.graphics.Canvas

/** Something that can be drawn on a [Canvas].*/
public interface Sprite {
    /** Draws this [Sprite] on the given [canvas].*/
    public fun draw(canvas: Canvas)
}