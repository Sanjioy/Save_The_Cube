package com.stingach.dm.savethecube.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.data.db.AppDatabase;
import com.stingach.dm.savethecube.data.db.entities.Users;
import com.stingach.dm.savethecube.data.prefs.PrefsManager;
import com.stingach.dm.savethecube.ui.adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserSelectionDialog {

    public interface OnUserSelectedListener {
        void onUserSelected(Users user);
    }

    public static void show(Context context, OnUserSelectedListener listener) {
        int currentUserId = PrefsManager.getUserId(context);

        if (currentUserId == -1) {
            Toast.makeText(context, "Please register or log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
            List<Users> users = db.usersDao().getAllUsers();
            List<UsersAdapter.UserItem> userItems = new ArrayList<>();

            for (Users user : users) {
                Integer bestScore = db.gameSessionsDao().getMaxScoreForUser(user.id);
                userItems.add(new UsersAdapter.UserItem(user.id, user.username, bestScore != null ? bestScore : 0));
            }

            ((AppCompatActivity) context).runOnUiThread(() -> {
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_users_list, null);
                AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();

                RecyclerView recycler = dialogView.findViewById(R.id.recyclerUsers);
                recycler.setLayoutManager(new LinearLayoutManager(context));

                Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(v -> dialog.dismiss());

                UsersAdapter adapter = new UsersAdapter(userItems, currentUserId, selectedItem -> {
                    for (Users u : users) {
                        if (u.username.equals(selectedItem.username)) {
                            PrefsManager.setUserId(context, u.id);
                            dialog.dismiss();
                            listener.onUserSelected(u);
                            break;
                        }
                    }
                });

                recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                // Scroll to current user
                int scrollTo = -1;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).id == currentUserId) {
                        scrollTo = i;
                        break;
                    }
                }

                if (scrollTo != -1) {
                    final int pos = scrollTo;
                    recycler.post(() -> recycler.scrollToPosition(pos));
                }

                dialog.show();
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            });
        }).start();
    }
}
