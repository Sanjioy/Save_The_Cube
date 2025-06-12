package com.stingach.dm.savethecube.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.main.GameView;

import java.util.Random;

// Класс бонуса жизни.
public class HeartBonus {

    // Битмап с изображением бонуса (с тенью).
    public Bitmap bitmap;

    // Позиция бонуса.
    public float x;
    public float y;

    // Скорость падения.
    public float velocity;

    // Прозрачность для мигания.
    public int alpha = 255;

    // Флаг направления мигания.
    boolean fadingOut = true;

    // Конструктор.
    public HeartBonus(Context context) {
        // Загружаем изображение сердца.
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);

        // Временный объект спайка для получения размеров.
        Spike tempSpike = new Spike(context);

        // Устанавливаем целевую ширину и высоту по спайку.
        int targetWidth = tempSpike.getSpikeWidth();
        int targetHeight = tempSpike.getSpikeHeight();

        // Масштабируем сердце до нужного размера.
        original = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, false);

        // Добавляем тень.
        bitmap = addShadow(original);

        // Устанавливаем случайную X координату.
        int maxX = GameView.dWidth - bitmap.getWidth();
        Random random = new Random();
        x = (maxX > 0) ? random.nextInt(maxX) : 0;

        // Стартовая Y позиция — выше экрана.
        y = -bitmap.getHeight();

        // Скорость падения как у спайка.
        velocity = tempSpike.spikeVelocity;
    }

    // Метод для добавления тени.
    private Bitmap addShadow(Bitmap original) {
        // Радиус тени.
        int radius = 8;

        // Создаём новое изображение с запасом под тень.
        Bitmap shadowBitmap = Bitmap.createBitmap(original.getWidth() + radius * 2, original.getHeight() + radius * 2, Bitmap.Config.ARGB_8888);

        // Подготавливаем канвас и кисть.
        Canvas canvas = new Canvas(shadowBitmap);
        Paint shadowPaint = new Paint();
        shadowPaint.setShadowLayer(radius, 0, 0, Color.argb(100, 0, 0, 0));

        // Рисуем оригинал с тенью.
        canvas.drawBitmap(original, radius, radius, shadowPaint);
        return shadowBitmap;
    }

    // Обновление прозрачности для эффекта мигания.
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

    // Получение ширины бонуса.
    public int getWidth() {
        return bitmap.getWidth();
    }

    // Получение высоты бонуса.
    public int getHeight() {
        return bitmap.getHeight();
    }
}

