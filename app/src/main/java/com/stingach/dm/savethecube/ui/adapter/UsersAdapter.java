package com.stingach.dm.savethecube.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stingach.dm.savethecube.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    // Класс модели пользователя для адаптера
    public static class UserItem {
        public final int userId;
        public final String username;
        public final int bestScore;

        public UserItem(int userId, String username, int bestScore) {
            this.userId = userId;
            this.username = username;
            this.bestScore = bestScore;
        }
    }


    // Интерфейс для обработки кликов по пользователю
    public interface OnUserClickListener {
        void onUserClick(UserItem user);
    }

    private final List<UserItem> userList;         // Список пользователей для отображения
    private final OnUserClickListener listener;    // Обработчик кликов
    private final int currentUserId;

    // Конструктор адаптера принимает список пользователей и обработчик кликов
    public UsersAdapter(List<UserItem> users, int currentUserId, OnUserClickListener listener) {
        this.userList = users;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание View для элемента списка пользователей
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_users, parent, false);
        return new UserViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserItem user = userList.get(position);

        holder.tvUsername.setText(user.username);
        holder.tvBestScore.setText("Best: " + user.bestScore);
        holder.imgUserIcon.setColorFilter(0xFF4CAF50);

        // 🟩 Выделить текущего пользователя
        if (user.userId == currentUserId) {
            holder.itemView.setBackgroundResource(R.drawable.bg_current_user);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // без фона
        }


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

    // ViewHolder для кеширования View компонентов элемента списка
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
