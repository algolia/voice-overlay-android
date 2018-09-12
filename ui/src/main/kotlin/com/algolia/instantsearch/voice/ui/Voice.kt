package com.algolia.instantsearch.voice.ui

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView

/** Helper functions for voice permission handling. */
object Voice {

    const val PermissionRequestRecordAudio = 1

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
    fun isRecordAudioPermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(context, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    /**
     * Gets whether your [activity] should show UI with rationale for requesting the [recording permission][RECORD_AUDIO].
     */
    @JvmStatic
    fun shouldExplainPermission(activity: Activity) =
        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)

    /**
     * Requests the [recording permission][RECORD_AUDIO].*/
    @JvmStatic
    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(RECORD_AUDIO), PermissionRequestRecordAudio)
    }

    /** Opens the application's settings, so the user can enable recording permission.*/
    @JvmStatic
    fun openAppSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", context.packageName, null)))
    }

    /** Guides the user to manually enable recording permission in the app's settings.*/
    @JvmStatic
    fun showPermissionManualInstructions(anchor: View) {
        return showPermissionManualInstructions(anchor, null as Int?, null, null)
    }

    /** Guides the user to manually enable recording permission in the app's settings.*/
    @JvmStatic
    fun showPermissionManualInstructions(anchor: View,
                                         @StringRes whyEnable: Int? = null,
                                         @StringRes buttonEnable: Int? = null,
                                         @StringRes howEnable: Int? = null) {
        return showPermissionManualInstructions(anchor,
            whyEnable?.let { anchor.context.getString(whyEnable) },
            buttonEnable?.let { anchor.context.getString(buttonEnable) },
            howEnable?.let { anchor.context.getString(howEnable) })
    }

    /** Guides the user to manually enable recording permission in the app's settings.*/
    @JvmStatic
    fun showPermissionManualInstructions(anchor: View,
                                         whyEnable: String? = null,
                                         buttonEnable: String? = null,
                                         howEnable: String? = null) {
        val snackbar = Snackbar.make(anchor, whyEnable
            ?: anchor.context.getString(R.string.permission_enable_rationale), Snackbar.LENGTH_LONG)
            .setAction(buttonEnable
                ?: anchor.context.getString(R.string.permission_button_enable)) {
                Snackbar.make(anchor, howEnable
                    ?: anchor.context.getString(R.string.permission_enable_instructions), Snackbar.LENGTH_SHORT)
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            openAppSettings(anchor.context)
                        }
                    }).show()
            }
        (snackbar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView).maxLines = 2
        snackbar.show()
    }
}

//TODO: Expose Activity extension methods instead?