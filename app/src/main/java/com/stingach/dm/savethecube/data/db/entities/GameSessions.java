package com.stingach.dm.savethecube.data.db.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Сущность базы данных, представляющая игровую сессию.
@Entity(indices = {@Index(value = {"userId"})},
        foreignKeys = @ForeignKey(entity = Users.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE))
public class GameSessions {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String dateTime;
    public int score;
    public int bestScore;
}

