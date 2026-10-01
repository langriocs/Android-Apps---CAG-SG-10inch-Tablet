package com.avl.cag10inchApp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.avl.cag10inchApp.R;

public class CustomAlertDialog extends DialogFragment {

    View dialog;
    private TextView dialogTitle, dialogMsg;
    private String title, msg;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialog = inflater.inflate(R.layout.custom_alert_dialog, container, false);

        dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogMsg =  dialog.findViewById(R.id.dialog_msg);
        Button dialogOk =  dialog.findViewById(R.id.dialog_ok);
        dialogOk.setOnClickListener(view -> {
            this.dismiss();
            if (getActivity() != null) {
                getActivity().finishAffinity();
            }
        });

        dialogTitle.setText(this.title);
        dialogMsg.setText(this.msg);

        this.setCancelable(false);

        return dialog;
    }

    public void setTitle(String msg) {
        this.title = msg;
    }
    public void setMessage(String msg) {
        this.msg = msg;
    }
}
