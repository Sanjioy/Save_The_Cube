package com.stingach.dm.savethecube.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.data.db.AppDatabase;
import com.stingach.dm.savethecube.data.db.entities.Users;
import com.stingach.dm.savethecube.data.prefs.PrefsManager;

public class UserLoginDialog {

    public interface OnUserLoginListener {
        void onUserLoggedIn(Users user);
    }

    public static void show(Context context, OnUserLoginListener listener) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_user, null);
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        EditText usernameEdit = dialogView.findViewById(R.id.editUsername);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnContinue = dialogView.findViewById(R.id.btnContinue);

        usernameEdit.addTextChangedListener(new TextWatcher() {
            private long lastToastTime = 0;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 20) {
                    usernameEdit.setText(s.subSequence(0, 20));
                    usernameEdit.setSelection(20);
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastToastTime > 2000) {
                        Toast.makeText(context, "Username can't be longer than 20 characters", Toast.LENGTH_SHORT).show();
                        lastToastTime = currentTime;
                    }
                }
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnContinue.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(context, "Enter the user's name", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
                Users user = db.usersDao().getUserByUsername(username);

                if (user == null) {
                    user = new Users();
                    user.username = username;
                    long userId = db.usersDao().insert(user);
                    user.id = (int) userId;
                }

                Users finalUser = user;
                PrefsManager.setUserId(context, user.id);

                ((AppCompatActivity) context).runOnUiThread(() -> {
                    dialog.dismiss();
                    listener.onUserLoggedIn(finalUser);
                });
            }).start();
        });

        dialog.show();
    }
}
