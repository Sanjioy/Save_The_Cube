package com.stingach.dm.savethecube;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerSessions;
    GameSessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        recyclerSessions = findViewById(R.id.recyclerSessions);
        recyclerSessions.setLayoutManager(new LinearLayoutManager(this));

        // Загружаем сессии пользователя из базы
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());

            // Получаем userId из SharedPreferences
            int userId = getSharedPreferences("my_pref", MODE_PRIVATE).getInt("user_id", -1);

            if (userId != -1) {
                List<GameSessions> sessions = db.gameSessionsDao().getSessionsForUser(userId);

                runOnUiThread(() -> {
                    adapter = new GameSessionAdapter(sessions);
                    recyclerSessions.setAdapter(adapter);
                });
            }
        }).start();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}
