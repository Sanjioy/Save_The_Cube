// Класс AppDatabase отвечает за инициализацию и доступ к базе данных через Room.
package com.stingach.dm.savethecube.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.stingach.dm.savethecube.data.db.dao.GameSessionsDao;
import com.stingach.dm.savethecube.data.db.dao.UsersDao;
import com.stingach.dm.savethecube.data.db.entities.GameSessions;
import com.stingach.dm.savethecube.data.db.entities.Users;

@Database(entities = {Users.class, GameSessions.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Singleton экземпляр базы данных.
    private static volatile AppDatabase INSTANCE;

    // Абстрактный метод для доступа к DAO пользователей.
    public abstract UsersDao usersDao();

    // Абстрактный метод для доступа к DAO игровых сессий.
    public abstract GameSessionsDao gameSessionsDao();

    // Метод для получения Singleton экземпляра базы данных.
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "save_the_cube_db"
                            )
                            .fallbackToDestructiveMigration() // Удаляет и пересоздаёт БД при несовпадении версии.
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
