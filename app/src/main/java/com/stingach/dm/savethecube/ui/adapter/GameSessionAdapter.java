package com.stingach.dm.savethecube.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.data.db.entities.GameSessions;

import java.util.List;

// Адаптер для отображения списка игровых сессий.
public class GameSessionAdapter extends RecyclerView.Adapter<GameSessionAdapter.ViewHolder> {

    private final List<GameSessions> sessions;

    public GameSessionAdapter(List<GameSessions> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаем View для элемента списка.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_session, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Привязываем данные сессии к элементу интерфейса.
        GameSessions session = sessions.get(position);
        holder.tvScore.setText("Score: " + session.score);
        holder.tvBestScore.setText("Best: " + session.bestScore);
        holder.tvDateTime.setText(session.dateTime);

        // Показываем иконку, если счет равен рекордному.
        holder.imgBestIcon.setVisibility(session.score == session.bestScore ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    // ViewHolder содержит элементы интерфейса одного элемента списка.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvScore, tvBestScore, tvDateTime;
        ImageView imgBestIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvBestScore = itemView.findViewById(R.id.tvBestScore);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            imgBestIcon = itemView.findViewById(R.id.img_best_icon);
        }
    }
}
