package com.algolia.instantsearch.voice

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.algolia.instantsearch.voice.PermissionDialogFragment.ID_REQ_VOICE_PERM

class VoiceHelp {
    companion object {
        @JvmStatic
        fun isPermissionGranted(grantResult: IntArray): Boolean {
            return grantResult[0] == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        fun isRecordPermissionWithResults(requestCode: Int, grantResults: IntArray): Boolean {
            return requestCode == ID_REQ_VOICE_PERM && grantResults.isNotEmpty()
        }

        /**
         * Gets whether your application was granted the [RECORD_AUDIO] recording permission.
         */
        @JvmStatic
        fun hasRecordPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        }

        /**
         * Gets whether your [activity] should show UI with rationale for requesting the [RECORD_AUDIO] recording permission.
         */
        @JvmStatic
        fun shouldExplainPermission(activity: Activity): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)
        }
    }
}

//TODO: Expose Activity extension methods instead?