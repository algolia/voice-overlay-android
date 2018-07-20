package com.algolia.instantsearch.voice.ui;

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
    public static final String DEFAULT_TITLE = "You can use voice search to find results";
    public static final String DEFAULT_MESSAGE = "Can we access your device's microphone to enable voice search?";
    public static final String DEFAULT_POSITIVE_BUTTON = "Allow microphone access";
    public static final String DEFAULT_NEGATIVE_BUTTON = "No";

    private String title = DEFAULT_TITLE;
    private String message = DEFAULT_MESSAGE;
    private String positiveButton = DEFAULT_POSITIVE_BUTTON;
    private String negativeButton = DEFAULT_NEGATIVE_BUTTON;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            throw new IllegalStateException("PermissionDialogFragment must be used within an activity.");
        }
        return new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{RECORD_AUDIO}, ID_REQ_VOICE_PERM);
                        dismiss();
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
    }

    //region setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPositiveButton(String positiveButton) {
        this.positiveButton = positiveButton;
    }

    public void setNegativeButton(String negativeButton) {
        this.negativeButton = negativeButton;
    }
    //endregion
}
