package com.algolia.instantsearch.voice.ui

import android.content.Context
import android.content.res.ColorStateList
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.algolia.instantsearch.voice.R

class VoiceFAB(context: Context?, attrs: AttributeSet?) : FloatingActionButton(context, attrs) {

    enum class State {
        Activated,
        Deactivated
    }

    private val green = ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.green))
    private val blue = ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.blue_light))

    private var state = State.Deactivated
        set(value) {
            field = value
            backgroundTintList = when (value) {
                State.Activated -> green
                State.Deactivated -> blue
            }
        }

    fun toggleState() {
        state = when (state) {
            State.Activated -> State.Deactivated
            State.Deactivated -> State.Activated
        }
    }
}