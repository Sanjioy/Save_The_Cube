package com.stingach.dm.savethecube;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UsersDao {
    @Insert
    long insert(Users user);

    @Query("SELECT * FROM Users WHERE username = :username LIMIT 1")
    Users getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    Users getUserById(int userId);

    // Получение всех пользователей из таблицы users
    @Query("SELECT * FROM users")
    List<Users> getAllUsers();
}
