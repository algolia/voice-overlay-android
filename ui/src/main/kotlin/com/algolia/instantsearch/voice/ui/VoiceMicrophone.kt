package com.algolia.instantsearch.voice.ui

import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.ImageView
import com.algolia.instantsearch.voice.ui.R


class VoiceMicrophone(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    enum class State {
        Activated,
        Deactivated
    }

    private val white = ContextCompat.getColor(context, R.color.white)
    private val blue = ContextCompat.getColor(context, R.color.blue_dark)

    var state = State.Deactivated
        set(value) {
            field = value
            imageTintList = ColorStateList.valueOf(when (value) {
                State.Activated -> white
                State.Deactivated -> blue
            })
            backgroundTintList = when (value) {
                State.Activated -> null
                State.Deactivated -> ColorStateList.valueOf(white)
            }
        }
}