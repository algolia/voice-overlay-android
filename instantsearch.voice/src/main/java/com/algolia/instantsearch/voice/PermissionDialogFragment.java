package com.algolia.instantsearch.voice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import static android.Manifest.permission.RECORD_AUDIO;

public class PermissionDialogFragment extends DialogFragment {
    public static final int ID_REQ_VOICE_PERM = 1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            throw new IllegalStateException("PermissionDialogFragment must be used within an activity.");
        }
        return new AlertDialog.Builder(activity)
                .setTitle("You can use voice search to find products")
                .setMessage("Can we access your device's microphone to enable voice search?")
                .setPositiveButton("Allow microphone access", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{RECORD_AUDIO}, ID_REQ_VOICE_PERM);
                        dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
    }
}
