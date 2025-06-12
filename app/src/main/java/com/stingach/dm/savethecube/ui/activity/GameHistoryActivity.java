package com.stingach.dm.savethecube.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stingach.dm.savethecube.ui.adapter.GameSessionAdapter;
import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.data.db.AppDatabase;
import com.stingach.dm.savethecube.data.db.entities.GameSessions;

import java.util.List;

// GameHistoryActivity отображает историю игровых сессий.
public class GameHistoryActivity extends AppCompatActivity {

    // Элементы интерфейса.
    private RecyclerView recyclerSessions;
    private GameSessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        // Инициализация RecyclerView.
        recyclerSessions = findViewById(R.id.recyclerSessions);
        recyclerSessions.setLayoutManager(new LinearLayoutManager(this));

        // Кнопка возврата на предыдущий экран.
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Загружаем игровые сессии пользователя в фоновом потоке.
        loadGameSessions();
    }

    // Метод загрузки данных из базы.
    private void loadGameSessions() {
        new Thread(() -> {
            // Получение базы данных.
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());

            // Получение userId из SharedPreferences.
            int userId = getSharedPreferences("my_pref", MODE_PRIVATE).getInt("user_id", -1);

            if (userId != -1) {
                // Получение сессий пользователя.
                List<GameSessions> sessions = db.gameSessionsDao().getSessionsForUser(userId);

                // Обновление UI из главного потока.
                runOnUiThread(() -> {
                    adapter = new GameSessionAdapter(sessions);
                    recyclerSessions.setAdapter(adapter);
                });
            }
        }).start();
    }
}
