package com.stingach.dm.savethecube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// Класс для взрыва
public class Explosion {
    // Массив битов для анимации взрыва
    Bitmap exposion[] = new Bitmap[4];
    // Индекс текущего кадра анимации взрыва
    int explosionFrame = 0;
    // Позиция X и Y взрыва на экране
    int explosionX, explosionY;

    public Explosion(Context context) {
        // Загрузка текстур взрыва из ресурсов
        exposion[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode3);
        exposion[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode1);
        exposion[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode2);
        exposion[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode0);
    }

    // Получение взрыва по индексу кадра
    public Bitmap getExplosion(int explosionFrame){
        return exposion[explosionFrame];
    }
}
