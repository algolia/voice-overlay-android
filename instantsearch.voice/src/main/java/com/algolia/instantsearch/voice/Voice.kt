package com.algolia.instantsearch.voice

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.algolia.instantsearch.voice.ui.PermissionDialogFragment.Companion.PermissionRequestRecordAudio

/** Helper functions for voice permission handling. */
object Voice {
    /**
     * Gets whether the [permission results][grantResult] confirm it has been [granted][PackageManager.PERMISSION_GRANTED].
     */
    @JvmStatic
    fun isPermissionGranted(grantResult: IntArray) =
        grantResult[0] == PackageManager.PERMISSION_GRANTED

    /** Gets whether the [request's code][requestCode] and [results][grantResults] are valid, by respectively checking
     * that it matches the [request identifier][PermissionRequestRecordAudio] and that they are not empty.
     *
     */
    @JvmStatic
    fun isRecordPermissionWithResults(requestCode: Int, grantResults: IntArray) =
        requestCode == PermissionRequestRecordAudio && grantResults.isNotEmpty()

    /**
     * Gets whether your application was granted the [recording permission][RECORD_AUDIO].
     */
    @JvmStatic
    fun hasRecordPermission(context: Context) =
        ContextCompat.checkSelfPermission(context, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    /**
     * Gets whether your [activity] should show UI with rationale for requesting the [recording permission][RECORD_AUDIO].
     */
    @JvmStatic
    fun shouldExplainPermission(activity: Activity) =
        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)
}

//TODO: Expose Activity extension methods instead?