package com.stingach.dm.savethecube;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

import com.stingach.dm.savethecube.Users;

@Entity(foreignKeys = @ForeignKey(
        entity = Users.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = CASCADE
))
public class GameSessions {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String dateTime;
    public int score;
    public int bestScore;
}
