package com.stingach.dm.savethecube.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.stingach.dm.savethecube.R;

// Класс для анимации взрыва.
public class Explosion {

    // Массив с кадрами взрыва.
    Bitmap[] exposion = new Bitmap[4];

    // Индекс текущего кадра.
    public int explosionFrame = 0;

    // Позиция взрыва.
    public int explosionX;
    public int explosionY;

    // Конструктор.
    public Explosion(Context context) {
        // Загружаем кадры взрыва из ресурсов.
        exposion[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode3);
        exposion[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode1);
        exposion[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode2);
        exposion[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode0);
    }

    // Получение изображения по текущему кадру.
    public Bitmap getExplosion(int explosionFrame){
        return exposion[explosionFrame];
    }
}

