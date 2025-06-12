package com.stingach.dm.savethecube.ui.activity;

import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import com.stingach.dm.savethecube.main.GameView;

// GameActivity отвечает за запуск игрового процесса.
public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Устанавливаем представление игры.
        setContentView(new GameView(this));

        // Предотвращаем отключение экрана во время игры.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}

