package com.stingach.dm.savethecube;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Класс для экрана окончания игры
public class GameOver extends AppCompatActivity {

    // UI элементы
    TextView tvPoints;
    TextView tvHighest;
    ImageView ivNewHighest;

    // Представление предпочтений для сохранения рекорда
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        // Инициализация UI элементов
        tvPoints = findViewById(R.id.tvPoints);
        tvHighest = findViewById(R.id.tvHighest);
        ivNewHighest = findViewById(R.id.ivNewHighest);

        // Получение количества очков из переданных данных
        int points = getIntent().getExtras().getInt("points");

        // Отображение количества очков
        tvPoints.setText("" + points);

        // Инициализация предпочтений
        sharedPreferences = getSharedPreferences("my_pref", 0);

        // Получение текущего рекорда
        int highest = sharedPreferences.getInt("highest", 0);

        // Проверка, превышены ли текущие очки рекорд
        if (points > highest) {
            // Если да, то показываем иконку нового рекорда
            ivNewHighest.setVisibility(View.VISIBLE);
            // Обновляем рекорд
            highest = points;
            // Сохраняем обновленный рекорд
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highest", highest);
            editor.commit();
        }

        // Отображение рекорда
        tvHighest.setText("" + highest);
    }

    // Метод для перезапуска игры
    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Метод для выхода из игры
    public void exit(View view) {
        finish();
    }
}
