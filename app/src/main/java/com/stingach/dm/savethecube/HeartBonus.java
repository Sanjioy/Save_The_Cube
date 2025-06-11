package com.stingach.dm.savethecube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

// Класс бонуса жизни.
public class HeartBonus {
    Bitmap bitmap;
    float x, y;
    float velocity;
    int alpha = 255; // Прозрачность для мигания.
    boolean fadingOut = true; // Направление изменения прозрачности.

    public HeartBonus(Context context) {
        // Загружаем изображение и создаём тень.
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        Spike tempSpike = new Spike(context);

        int targetWidth = tempSpike.getSpikeWidth();
        int targetHeight = tempSpike.getSpikeHeight();

        // Масштабируем изображение.
        original = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, false);

        // Создаём изображение с тенью.
        bitmap = addShadow(original);

        int maxX = GameView.dWidth - bitmap.getWidth();
        Random random = new Random();
        x = (maxX > 0) ? random.nextInt(maxX) : 0;

        // Стартуем чуть выше экрана.
        y = -bitmap.getHeight();

        velocity = tempSpike.spikeVelocity;
    }

    // Добавляет лёгкую тень по краям.
    private Bitmap addShadow(Bitmap original) {
        int radius = 8;
        Bitmap shadowBitmap = Bitmap.createBitmap(original.getWidth() + radius * 2, original.getHeight() + radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(shadowBitmap);
        Paint shadowPaint = new Paint();
        shadowPaint.setShadowLayer(radius, 0, 0, Color.argb(100, 0, 0, 0));
        canvas.drawBitmap(original, radius, radius, shadowPaint);
        return shadowBitmap;
    }

    // Обновление прозрачности (для мигания).
    public void updateAlpha() {
        if (fadingOut) {
            alpha -= 10;
            if (alpha <= 100) {
                alpha = 100;
                fadingOut = false;
            }
        } else {
            alpha += 10;
            if (alpha >= 255) {
                alpha = 255;
                fadingOut = true;
            }
        }
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }
}
