package com.stingach.dm.savethecube;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Класс, отвечающий за экран окончания игры
public class GameOver extends AppCompatActivity {

    // UI элементы
    private TextView tvPoints;      // Текущее количество очков
    private TextView tvHighest;     // Лучший результат
    private ImageView ivNewHighest; // Иконка "Новый рекорд!"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        // Привязка UI элементов
        tvPoints = findViewById(R.id.tvPoints);
        tvHighest = findViewById(R.id.tvHighest);
        ivNewHighest = findViewById(R.id.ivNewHighest);

        // Получаем очки из Intent с защитой от null
        Bundle extras = getIntent().getExtras();
        int points = (extras != null) ? extras.getInt("points", 0) : 0;
        tvPoints.setText(String.valueOf(points));

        // Получаем userId из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) return; // Если пользователь не найден — выходим

        // Работа с базой данных в фоне
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());

            // Получение текущего лучшего результата
            Integer bestSoFar = db.gameSessionsDao().getMaxScoreForUser(userId);
            if (bestSoFar == null) bestSoFar = 0;

            // Определяем, является ли это новым рекордом
            boolean isNewRecord = points > bestSoFar;
            int bestScore = Math.max(points, bestSoFar);

            // Создаем и сохраняем сессию
            GameSessions session = new GameSessions();
            session.userId = userId;
            session.score = points;
            session.bestScore = bestScore;
            session.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            db.gameSessionsDao().insert(session);

            // Обновляем UI на главном потоке
            runOnUiThread(() -> {
                tvHighest.setText(String.valueOf(bestScore));
                if (isNewRecord) {
                    ivNewHighest.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    // Кнопка "Играть снова"
    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, GameActivity.class);
        startActivity(intent);
        finish();
    }

    // Кнопка "Выход"
    public void exit(View view) {
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Кнопка "История"
    public void openHistory(View view) {
        Intent intent = new Intent(this, GameHistoryActivity.class);
        startActivity(intent);
    }
}
