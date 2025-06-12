// Класс, отвечающий за экран окончания игры.
package com.stingach.dm.savethecube.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.data.db.AppDatabase;
import com.stingach.dm.savethecube.data.db.entities.GameSessions;
import com.stingach.dm.savethecube.data.prefs.PrefsManager;
import com.stingach.dm.savethecube.ui.activity.GameActivity;
import com.stingach.dm.savethecube.ui.activity.GameHistoryActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameOver extends AppCompatActivity {

    // Текстовое поле с очками.
    private TextView tvPoints;

    // Текстовое поле с лучшим результатом.
    private TextView tvHighest;

    // Иконка "Новый рекорд!".
    private ImageView ivNewHighest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        // Инициализация элементов интерфейса.
        initUI();

        // Получение количества очков из Intent.
        int points = getIntent().getIntExtra("points", 0);
        tvPoints.setText(String.valueOf(points));

        // Получение ID пользователя.
        int userId = PrefsManager.getUserId(this);
        if (userId == -1) {
            finish(); // Завершение активности, если пользователь не найден.
            return;
        }

        // Сохранение игровой сессии и обновление интерфейса.
        saveGameSession(userId, points);
    }

    // Метод инициализации UI-элементов.
    private void initUI() {
        tvPoints = findViewById(R.id.tvPoints);
        tvHighest = findViewById(R.id.tvHighest);
        ivNewHighest = findViewById(R.id.ivNewHighest);
    }

    // Метод сохранения игровой сессии в базу данных.
    private void saveGameSession(int userId, int points) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());

            // Получение текущего лучшего результата.
            Integer bestSoFarObj = db.gameSessionsDao().getMaxScoreForUser(userId);
            int bestSoFar = bestSoFarObj != null ? bestSoFarObj : 0;

            // Проверка, является ли текущий результат новым рекордом.
            boolean isNewRecord = points > bestSoFar;
            int bestScore = Math.max(points, bestSoFar);

            // Создание и сохранение игровой сессии.
            GameSessions session = new GameSessions();
            session.userId = userId;
            session.score = points;
            session.bestScore = bestScore;
            session.dateTime = getFormattedCurrentDateTime();

            db.gameSessionsDao().insert(session);

            // Обновление UI на главном потоке.
            runOnUiThread(() -> updateUI(bestScore, isNewRecord));
        }).start();
    }

    // Метод форматирования текущей даты и времени.
    private String getFormattedCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // Метод обновления элементов интерфейса.
    private void updateUI(int bestScore, boolean isNewRecord) {
        tvHighest.setText(String.valueOf(bestScore));
        if (isNewRecord) {
            ivNewHighest.setVisibility(View.VISIBLE);
        }
    }

    // Обработка кнопки "Играть снова".
    public void restart(View view) {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    // Обработка кнопки "Выход".
    public void exit(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Обработка кнопки "История".
    public void openHistory(View view) {
        startActivity(new Intent(this, GameHistoryActivity.class));
    }
}
