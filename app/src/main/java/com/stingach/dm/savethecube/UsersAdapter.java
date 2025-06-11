package com.stingach.dm.savethecube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    public static class UserItem {
        public final String username;
        public final int bestScore;

        public UserItem(String username, int bestScore) {
            this.username = username;
            this.bestScore = bestScore;
        }
    }

    public interface OnUserClickListener {
        void onUserClick(UserItem user);
    }

    private final List<UserItem> userList;
    private final OnUserClickListener listener;

    public UsersAdapter(List<UserItem> users, OnUserClickListener listener) {
        this.userList = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserItem user = userList.get(position);
        holder.tvUsername.setText(user.username);
        holder.tvBestScore.setText("Max: " + user.bestScore);
        holder.imgUserIcon.setColorFilter(0xFF4CAF50);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserIcon;
        TextView tvUsername, tvBestScore;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserIcon = itemView.findViewById(R.id.imgUserIcon);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBestScore = itemView.findViewById(R.id.tvBestScore);
        }
    }
}
