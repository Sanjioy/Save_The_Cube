// DAO-интерфейс для взаимодействия с таблицей пользователей.
package com.stingach.dm.savethecube.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.stingach.dm.savethecube.data.db.entities.Users;

import java.util.List;

@Dao
public interface UsersDao {

    // Добавление нового пользователя в базу. Возвращает ID добавленной записи.
    @Insert
    long insert(Users user);

    // Получение пользователя по имени (используется для логина или регистрации).
    @Query("SELECT * FROM Users WHERE username = :username LIMIT 1")
    Users getUserByUsername(String username);

    // Получение пользователя по ID (например, для загрузки текущего профиля).
    @Query("SELECT * FROM Users WHERE id = :userId LIMIT 1")
    Users getUserById(int userId);

    // Получение списка всех зарегистрированных пользователей.
    @Query("SELECT * FROM Users")
    List<Users> getAllUsers();
}
