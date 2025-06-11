package com.stingach.dm.savethecube;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface GameSessionsDao {

    @Insert
    long insert(GameSessions session);

    @Query("SELECT * FROM GameSessions WHERE userId = :userId ORDER BY dateTime DESC")
    List<GameSessions> getSessionsForUser(int userId);

    @Query("SELECT MAX(score) FROM GameSessions WHERE userId = :userId")
    Integer getMaxScoreForUser(int userId);
}
