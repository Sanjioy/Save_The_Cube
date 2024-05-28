package com.stingach.dm.savethecube;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

// Основная активность приложения
public class MainActivity extends AppCompatActivity {

    // Создание интерфейса приложения и установка флага для предотвращения выключения экрана
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Установка макета главной активности
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Флаг для поддержания включенного состояния экрана
    }

    // Метод для запуска игры
    public void startGame(View view) {
        GameView gameView = new GameView(this); // Создание объекта GameView
        setContentView(gameView); // Замена макета на GameView
    }
}
