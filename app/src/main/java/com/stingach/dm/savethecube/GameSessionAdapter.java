package com.stingach.dm.savethecube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameSessionAdapter extends RecyclerView.Adapter<GameSessionAdapter.ViewHolder> {

    private List<GameSessions> sessions;

    public GameSessionAdapter(List<GameSessions> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public GameSessionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_session, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GameSessionAdapter.ViewHolder holder, int position) {
        GameSessions session = sessions.get(position);
        holder.tvScore.setText("Очки: " + session.score);
        holder.tvBestScore.setText("Лучший: " + session.bestScore);
        holder.tvDateTime.setText(session.dateTime);

        // Если текущий счет равен лучшему, показываем иконку
        if (session.score == session.bestScore) {
            holder.imgBestIcon.setVisibility(View.VISIBLE);
        } else {
            holder.imgBestIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

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
