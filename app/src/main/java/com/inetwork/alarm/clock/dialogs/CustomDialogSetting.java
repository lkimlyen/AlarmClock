package com.inetwork.alarm.clock.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.inetwork.alarm.clock.R;


/**
 * Created by macbook on 5/26/18.
 */

public class CustomDialogSetting extends DialogFragment {
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private OnDuplicateClickListener onDuplicateClickListener;
    private OnReviewClickListener onReviewClickListener;

    public void setListener(OnEditClickListener onEditClickListener,
                            OnDeleteClickListener onDeleteClickListener,
                            OnDuplicateClickListener onDuplicateClickListener,
                            OnReviewClickListener onReviewClickListener) {
        this.onEditClickListener = onEditClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onDuplicateClickListener = onDuplicateClickListener;
        this.onReviewClickListener = onReviewClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_setting);

        dialog.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onEditClickListener.onEditClick();
            }
        });

        dialog.findViewById(R.id.tv_duplicate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onDuplicateClickListener.onDuplicateClick();

            }
        });
        dialog.findViewById(R.id.tv_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onReviewClickListener.onReviewClick();

            }
        });
        dialog.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onDeleteClickListener.onDeleteClick();

            }
        });

        return dialog;
    }

    public interface OnEditClickListener {
        void onEditClick();
    }

    public interface OnDuplicateClickListener {

        void onDuplicateClick();

    }

    public interface OnReviewClickListener {

        void onReviewClick();

    }

    public interface OnDeleteClickListener {
        void onDeleteClick();
    }

}