package com.stingach.dm.savethecube.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.data.db.entities.Users;
import com.stingach.dm.savethecube.data.prefs.PrefsManager;
import com.stingach.dm.savethecube.data.db.AppDatabase;
import com.stingach.dm.savethecube.ui.dialog.UserLoginDialog;
import com.stingach.dm.savethecube.ui.dialog.UserSelectionDialog;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Виджеты интерфейса
    private TextView textUsername; // Отображение имени текущего пользователя
    private View btnStart;         // Кнопка "Старт"
    private View btnUsers;         // Кнопка "Пользователи"
    private Toast currentToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ⚠ Очистка базы — только во время разработки!
        /*Executors.newSingleThreadExecutor().execute(() ->
                AppDatabase.getInstance(getApplicationContext()).clearAllTables()
        );*/

        // Получаем ссылки на элементы UI
        btnStart = findViewById(R.id.btnStart);
        btnUsers = findViewById(R.id.btnUsers);
        // Кнопка "Выход"
        View btnExit = findViewById(R.id.btnExit);
        textUsername = findViewById(R.id.textUsername);

        // Скрываем имя пользователя до авторизации
        textUsername.setVisibility(View.GONE);

        // Деактивация кнопок до входа
        setButtonsEnabled(false);

        // Назначение обработчиков кнопок
        btnStart.setOnClickListener(v -> handleStartClick());
        btnUsers.setOnClickListener(v -> handleUsersClick());
        btnExit.setOnClickListener(v -> exitApp());

        // Загружаем пользователя из базы, если есть сохранённый ID
        loadUserFromDatabase();

        // Предотвращаем отключение экрана во время игры
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Метод обработки кнопки "Старт"
    private void handleStartClick() {
        int userId = PrefsManager.getUserId(this); // Получаем user_id из PrefsManager

        // Проверка на авторизацию
        if (userId == -1) {
            showSingleToast("Please register or log in before starting the game.");
            return;
        }

        // Получаем пользователя из базы в отдельном потоке
        new Thread(() -> {
            Users user = AppDatabase.getInstance(getApplicationContext()).usersDao().getUserById(userId);

            runOnUiThread(() -> {
                if (user == null) {
                    // Если пользователь не найден, очищаем сохраненный ID и деактивируем кнопки
                    showSingleToast("Please register or log in before starting the game.");
                    PrefsManager.setUserId(this, -1);
                    setButtonsEnabled(false);
                    textUsername.setVisibility(View.GONE);
                } else {
                    // Запускаем игровой экран
                    GameView gameView = new GameView(this);
                    setContentView(gameView);
                }
            });
        }).start();
    }

    // Метод обработки кнопки "Пользователи"
    private void handleUsersClick() {
        int userId = PrefsManager.getUserId(this);

        // Проверяем, авторизован ли пользователь
        if (userId == -1) {
            showSingleToast("First, register or log in.");
        } else {
            // Показываем список пользователей
            showUsersDialog();
        }
    }

    // Тост, не спамится
    private void showSingleToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }


    // Метод завершения приложения
    private void exitApp() {
        finishAffinity();
        System.exit(0);
    }

    // Метод активации или деактивации кнопок
    private void setButtonsEnabled(boolean hasUser) {
        float alpha = hasUser ? 1f : 0.5f;
        btnStart.setAlpha(alpha);
        btnUsers.setAlpha(alpha);

        // Оставляем кнопки активными всегда, чтобы ловить клики
        btnStart.setEnabled(true);
        btnUsers.setEnabled(true);
    }


    // Загрузка текущего пользователя из базы по сохранённому ID
    private void loadUserFromDatabase() {
        new Thread(() -> {
            int userId = PrefsManager.getUserId(this);

            if (userId != -1) {
                Users user = AppDatabase.getInstance(getApplicationContext()).usersDao().getUserById(userId);

                if (user != null) {
                    runOnUiThread(() -> {
                        textUsername.setText(user.username);
                        textUsername.setVisibility(View.VISIBLE);
                        setButtonsEnabled(true);
                        Toast.makeText(this, "Log in as " + user.username, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Если пользователь не найден, очищаем ID и деактивируем кнопки
                    PrefsManager.setUserId(this, -1);
                    runOnUiThread(() -> {
                        textUsername.setVisibility(View.GONE);
                        setButtonsEnabled(false);
                    });
                }
            } else {
                runOnUiThread(() -> textUsername.setVisibility(View.GONE));
            }
        }).start();
    }

    // Открытие диалога регистрации или входа пользователя
    public void openUserDialog(View view) {
        UserLoginDialog.show(this, user -> {
            textUsername.setText(user.username);
            textUsername.setVisibility(View.VISIBLE);
            setButtonsEnabled(true);
            showSingleToast("Log in as " + user.username);
        });
    }

    // Отображение диалога выбора пользователей
    public void showUsersDialog() {
        UserSelectionDialog.show(this, user -> {
            textUsername.setText(user.username);
            textUsername.setVisibility(View.VISIBLE);
            setButtonsEnabled(true);
            showSingleToast("Log in as " + user.username);
        });
    }
}