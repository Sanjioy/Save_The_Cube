package com.stingach.dm.savethecube;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Виджеты интерфейса
    private TextView textUsername; // Отображение имени текущего пользователя
    private View btnStart;         // Кнопка "Старт"
    private View btnUsers;         // Кнопка "Пользователи"
    private View btnExit;

    // Основной метод создания активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getApplicationContext().deleteDatabase("save_the_cube_db"); // Очистка базы (только для отладки)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Установка макета экрана

        // Получаем ссылки на элементы UI
        btnStart = findViewById(R.id.btnStart);
        btnUsers = findViewById(R.id.btnUsers);
        btnExit = findViewById(R.id.btnExit);

        textUsername = findViewById(R.id.textUsername);
        textUsername.setVisibility(View.GONE); // Скрываем имя до загрузки пользователя

        // Делаем кнопки неактивными до регистрации
        btnStart.setAlpha(0.5f);
        btnUsers.setAlpha(0.5f);

        // Обработка нажатия кнопки "Старт"
        btnStart.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            if (userId == -1) {
                Toast.makeText(MainActivity.this, "Please register or log in before starting the game.", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    Users user = db.usersDao().getUserById(userId);

                    runOnUiThread(() -> {
                        if (user == null) {
                            Toast.makeText(MainActivity.this, "The user was not found. Please log in again.", Toast.LENGTH_SHORT).show();
                            prefs.edit().remove("user_id").apply();
                            btnStart.setAlpha(0.5f);
                            btnUsers.setAlpha(0.5f);
                            textUsername.setVisibility(View.GONE);
                        } else {
                            GameView gameView = new GameView(MainActivity.this);
                            setContentView(gameView); // Переход на игровой экран
                        }
                    });
                }).start();
            }
        });

        // Обработка нажатия кнопки "Пользователи"
        btnUsers.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            if (userId == -1) {
                Toast.makeText(MainActivity.this, "First, register or log in.", Toast.LENGTH_SHORT).show();
            } else {
                showUsersDialog(); // Показать диалог выбора пользователей
            }
        });

        btnExit.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        loadUserFromDatabase(); // Проверка текущего пользователя при старте
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Блокировка выключения экрана
    }

    // Метод загрузки пользователя из базы
    private void loadUserFromDatabase() {
        new Thread(() -> {
            SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            if (userId != -1) {
                Users user = AppDatabase.getInstance(getApplicationContext()).usersDao().getUserById(userId);
                if (user != null) {
                    runOnUiThread(() -> {
                        textUsername.setText(user.username);
                        textUsername.setVisibility(View.VISIBLE);
                        btnStart.setAlpha(1f);
                        btnUsers.setAlpha(1f);
                        Toast.makeText(this, "Log in as " + user.username, Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                runOnUiThread(() -> textUsername.setVisibility(View.GONE));
            }
        }).start();
    }

    // Диалог регистрации или входа пользователя
    public void openUserDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        EditText usernameEdit = dialogView.findViewById(R.id.editUsername);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnContinue = dialogView.findViewById(R.id.btnContinue);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnContinue.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString().trim();

            if (!username.isEmpty()) {
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    Users existingUser = db.usersDao().getUserByUsername(username);

                    Users user;
                    if (existingUser != null) {
                        user = existingUser; // Вход
                    } else {
                        user = new Users();  // Регистрация
                        user.username = username;
                        long userId = db.usersDao().insert(user);
                        user.id = (int) userId;
                    }

                    SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
                    prefs.edit().putInt("user_id", user.id).apply();

                    runOnUiThread(() -> {
                        textUsername.setText(user.username);
                        textUsername.setVisibility(View.VISIBLE);
                        btnStart.setAlpha(1f);
                        btnUsers.setAlpha(1f);
                        Toast.makeText(this, "Log in as " + user.username, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }).start();
            } else {
                Toast.makeText(this, "Enter the user's name", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    public void showUsersDialog() {
        SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
        int currentUserId = prefs.getInt("user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Please register or log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<Users> users = db.usersDao().getAllUsers();

            // Подготовим список UserItem с именами и лучшими счётами
            List<UsersAdapter.UserItem> userItems = new ArrayList<>();
            for (Users user : users) {
                Integer bestScore = db.gameSessionsDao().getMaxScoreForUser(user.id);
                int score = (bestScore != null) ? bestScore : 0;
                userItems.add(new UsersAdapter.UserItem(user.username, score));
            }

            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_users_list, null);
                builder.setView(dialogView);

                RecyclerView recyclerUsers = dialogView.findViewById(R.id.recyclerUsers);
                recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

                Button btnCancel = dialogView.findViewById(R.id.btnCancel);

                // Создаём AlertDialog и сохраняем ссылку
                AlertDialog alertDialog = builder.create();

                btnCancel.setOnClickListener(v -> alertDialog.dismiss());

                // Адаптер и логика выбора пользователя
                UsersAdapter adapter = new UsersAdapter(userItems, userItem -> {
                    for (Users u : users) {
                        if (u.username.equals(userItem.username)) {
                            prefs.edit().putInt("user_id", u.id).apply();
                            textUsername.setText(u.username);
                            textUsername.setVisibility(View.VISIBLE);
                            btnStart.setAlpha(1f);
                            btnUsers.setAlpha(1f);
                            Toast.makeText(this, "Log in as " + u.username, Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            break;
                        }
                    }
                });
                recyclerUsers.setAdapter(adapter);

                alertDialog.show();
            });
        }).start();
    }

    // Метод для вывода всех пользователей и их игровых сессий в лог
    /*private void printAllUsersFromDatabase() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<Users> users = db.usersDao().getAllUsers();

            for (Users user : users) {
                Log.d("USER_LOG", "ID: " + user.id + ", Username: " + user.username);
                List<GameSessions> sessions = db.gameSessionsDao().getSessionsForUser(user.id);
                for (GameSessions session : sessions) {
                    Log.d("SESSION_LOG", "UserID: " + session.userId + ", DateTime: " + session.dateTime + ", Score: " + session.score + ", BestScore: " + session.bestScore);
                }
            }
        }).start();
    }*/

    // Вызов логирования пользователей через XML (например, кнопкой)
    /*public void onShowUsersClicked(View view) {
        printAllUsersFromDatabase();
    }*/

    // Метод запуска игры (не используется напрямую)
    /*public void startGame(View view) {
        SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Пожалуйста, введите имя пользователя перед началом игры", Toast.LENGTH_SHORT).show();
            return;
        }

        GameView gameView = new GameView(this);
        setContentView(gameView);
    }*/
}