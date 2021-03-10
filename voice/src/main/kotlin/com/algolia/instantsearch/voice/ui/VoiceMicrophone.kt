package com.algolia.instantsearch.voice.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import com.algolia.instantsearch.voice.R


class VoiceMicrophone(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    enum class State {
        Activated,
        Deactivated
    }

    private val white = ContextCompat.getColor(context, R.color.white)
    private val blue = ContextCompat.getColor(context, R.color.blue_dark)

    var state = State.Deactivated
        set(value) {
            field = value
            drawable.colorFilter = PorterDuffColorFilter(when (value) {
                State.Activated -> white
                State.Deactivated -> blue
            }, PorterDuff.Mode.SRC_IN)

            background.colorFilter = when (value) {
                State.Activated -> null
                State.Deactivated -> PorterDuffColorFilter(white, PorterDuff.Mode.SRC_IN)
            }
        }
}