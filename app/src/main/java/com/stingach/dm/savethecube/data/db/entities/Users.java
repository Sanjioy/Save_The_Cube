// Сущность базы данных, представляющая пользователя.
package com.stingach.dm.savethecube.data.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users")
public class Users {

    // Уникальный идентификатор пользователя.
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    // Имя пользователя, должно быть уникальным.
    @ColumnInfo(name = "username")
    public String username;
}
