// Класс PrefsManager отвечает за работу с локальными настройками через SharedPreferences.
package com.stingach.dm.savethecube.data.prefs;

import android.content.Context;

public class PrefsManager {

    // Имя файла SharedPreferences.
    private static final String PREF_NAME = "my_pref";

    // Ключ для хранения ID пользователя.
    private static final String KEY_USER_ID = "user_id";

    // Получение сохранённого ID пользователя. Возвращает -1, если ID не найден.
    public static int getUserId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_USER_ID, -1);
    }

    // Сохранение ID пользователя в SharedPreferences.
    public static void setUserId(Context context, int userId) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_USER_ID, userId)
                .apply();
    }
}
