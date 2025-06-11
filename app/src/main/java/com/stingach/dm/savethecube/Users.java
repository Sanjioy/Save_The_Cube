package com.stingach.dm.savethecube;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users")
public class Users {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
}

