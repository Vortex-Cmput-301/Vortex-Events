package com.example.vortex_events;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * DialogHelper class for managing dialog operations
 * Handles delete profile confirmation and countdown dialogs
 */
public class DialogHelper {

    private Context context;
    private DatabaseWorker databaseWorker;

    /**
     * Constructor for DialogHelper
     * @param context Application context
     */
    public DialogHelper(Context context) {
        this.context = context;
        this.databaseWorker = new DatabaseWorker(FirebaseFirestore.getInstance());
    }

    /**
     * Show delete confirmation dialog with input validation
     * @param currentUser The current user to delete
     * @param onDeleteSuccess Callback when delete is successful
     */
    public void showDeleteConfirmationDialog(RegisteredUser currentUser, Runnable onDeleteSuccess) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_delete_profile, null);
            builder.setView(dialogView);

            TextInputEditText etConfirmation = dialogView.findViewById(R.id.et_confirmation);
            MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
            MaterialButton btnConfirm = dialogView.findViewById(R.id.btn_confirm);

            AlertDialog dialog = builder.create();
            dialog.setCancelable(true);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            etConfirmation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String input = s.toString().trim();
                    btnConfirm.setEnabled(input.equals("DELETE"));
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            btnCancel.setOnClickListener(v -> dialog.dismiss());
            btnConfirm.setOnClickListener(v -> {
                if (etConfirmation.getText().toString().trim().equals("DELETE")) {
                    deleteUserProfile(currentUser, onDeleteSuccess);
                    dialog.dismiss();
                }
            });

            dialog.show();
            etConfirmation.requestFocus();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }

        } catch (Exception e) {
            Log.e("DialogHelper", "Error showing dialog: " + e.getMessage());
            Toast.makeText(context, "Error showing dialog", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Delete user profile from database
     * @param currentUser The user to delete
     * @param onSuccess Callback when delete is successful
     */
    private void deleteUserProfile(RegisteredUser currentUser, Runnable onSuccess) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseWorker.deleteUser(currentUser)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        onSuccess.run();
                    } else {
                        String errorMessage = "Delete failed: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Show exit countdown dialog
     * @param countdownSeconds Number of seconds for countdown
     * @param onCountdownComplete Callback when countdown completes
     */
    public void showExitCountdown(int countdownSeconds, Runnable onCountdownComplete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Profile Deleted")
                .setMessage("Application will exit in " + countdownSeconds + " seconds...")
                .setCancelable(false);

        AlertDialog countdownDialog = builder.create();
        countdownDialog.show();

        // Start countdown
        new android.os.Handler().postDelayed(() -> {
            countdownDialog.dismiss();
            onCountdownComplete.run();
        }, countdownSeconds * 1000L);
    }
    /**
     * Show a simple confirmation dialog with message and two buttons.
     * This dialog does not require any text input.
     *
     * @param title       dialog title
     * @param message     dialog message
     * @param onConfirmed callback when user clicks confirm
     */
    public void showSimpleConfirmationDialog(String title, String message, Runnable onConfirmed) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(true)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        dialog.dismiss();
                        if (onConfirmed != null) {
                            onConfirmed.run();
                        }
                    });

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            }
            dialog.show();
        } catch (Exception e) {
            Log.e("DialogHelper", "Error showing simple confirmation dialog: " + e.getMessage());
            Toast.makeText(context, "Error showing dialog", Toast.LENGTH_SHORT).show();
        }
    }
}
