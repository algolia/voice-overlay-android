package com.algolia.instantsearch.voice.ui

import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.algolia.instantsearch.voice.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/** Helper functions for voice permission handling. */
public object Voice {

    public const val PermissionRequestRecordAudio: Int = 1

    /**
     * Gets whether the [permission results][grantResult] confirm it has been [granted][PackageManager.PERMISSION_GRANTED].
     */
    @JvmStatic
    public fun isPermissionGranted(grantResult: IntArray): Boolean =
        grantResult[0] == PackageManager.PERMISSION_GRANTED

    /** Gets whether the [request's code][requestCode] and [results][grantResults] are valid, by respectively checking
     * that it matches the [request identifier][PermissionRequestRecordAudio] and that they are not empty.
     *
     */
    @JvmStatic
    public fun isRecordPermissionWithResults(requestCode: Int, grantResults: IntArray): Boolean =
        requestCode == PermissionRequestRecordAudio && grantResults.isNotEmpty()

    /**
     * Gets whether your application was granted the [recording permission][RECORD_AUDIO].
     */
    @JvmStatic
    public fun Context.isRecordAudioPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    /**
     * Gets whether your activity should show UI with rationale for requesting the [recording permission][RECORD_AUDIO].
     */
    @JvmStatic
    public fun Activity.shouldExplainPermission(): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(this, RECORD_AUDIO)

    /**
     * Requests the [recording permission][RECORD_AUDIO] from your activity.*/
    @JvmStatic
    public fun Activity.requestRecordingPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), PermissionRequestRecordAudio)
    }

    /** Opens the application's settings from a given context, so the user can enable the recording permission.*/
    @JvmStatic
    public fun Context.openAppSettings() {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
        )
    }

    /** Displays the rationale behind requesting the recording permission via a Snackbar.
     * @param anchor the view on which the SnackBar will be anchored.
     * @param whyAllow a description of why the permission should be granted.
     * @param buttonAllow a call to action for granting the permission.
     * */
    @JvmStatic
    @JvmOverloads
    public fun Activity.showPermissionRationale(
        anchor: View,
        whyAllow: CharSequence? = null,
        buttonAllow: CharSequence? = null
    ) {
        val whyText = whyAllow ?: getString(R.string.alg_permission_rationale)
        val buttonText = (buttonAllow ?: getString(R.string.alg_permission_button_again))
        Snackbar.make(anchor, whyText, Snackbar.LENGTH_LONG)
            .setAction(buttonText) { requestRecordingPermission() }.show()
    }

    /** Guides the user to manually enable recording permission in the app's settings.
     * @param anchor the view on which the SnackBar will be anchored.
     * @param whyEnable a description of why the permission should be enabled.
     * @param buttonEnable a call to action for enabling the permission.
     * @param howEnable instructions to manually enable the permission in settings.
     * */
    @JvmStatic
    @JvmOverloads
    public fun showPermissionManualInstructions(
        anchor: View,
        whyEnable: CharSequence? = null,
        buttonEnable: CharSequence? = null,
        howEnable: CharSequence? = null
    ) {
        val context = anchor.context
        val whyText = (whyEnable ?: context.getText(R.string.alg_permission_enable_rationale))
        val buttonText = (buttonEnable ?: context.getText(R.string.alg_permission_button_enable))
        val howText = (howEnable ?: context.getText(R.string.alg_permission_enable_instructions))

        val snackbar = Snackbar.make(anchor, whyText, Snackbar.LENGTH_LONG).setAction(buttonText) {
            Snackbar.make(anchor, howText, Snackbar.LENGTH_SHORT)
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) =
                        context.openAppSettings()
                }).show()
        }
        snackbar.view.findViewById<TextView>(R.id.snackbar_text).maxLines = 2
        snackbar.show()
    }
}