package com.stingach.dm.savethecube.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.stingach.dm.savethecube.data.db.entities.GameSessions;

import java.util.List;

// DAO-интерфейс для взаимодействия с таблицей игровых сессий.
@Dao
public interface GameSessionsDao {

    // Добавление новой сессии.
    @Insert
    void insert(GameSessions session);

    // Получение списка всех сессий пользователя.
    @Query("SELECT * FROM GameSessions WHERE userId = :userId ORDER BY dateTime DESC")
    List<GameSessions> getSessionsForUser(int userId);

    // Получение наибольшего результата пользователя.
    @Query("SELECT MAX(score) FROM GameSessions WHERE userId = :userId")
    Integer getMaxScoreForUser(int userId);
}

