package com.stingach.dm.savethecube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.Random;

// Класс для одной бомбы (спайка)
public class Spike {

    // Статический массив повёрнутых спрайтов, общий для всех спайков
    private static Bitmap[] spikeFrames;

    // Текущий кадр анимации
    int spikeFrame = 0;

    // Счётчик кадров для плавности анимации
    public int frameCounter = 0;

    // Координаты спайка
    int spikeX, spikeY;

    // Скорость падения спайка
    int spikeVelocity;

    // Статический генератор случайных чисел, общий для всех спайков
    private static final Random random = new Random();

    // Конструктор, инициализирует позицию и подготавливает спрайты (при первом вызове)
    public Spike(Context context) {
        // Инициализация массива с повёрнутыми изображениями спайка — делаем один раз
        if (spikeFrames == null) {
            initSpikeFrames(context);
        }

        // Устанавливаем стартовую позицию и скорость
        resetPosition();
    }

    // Инициализация массива повёрнутых спрайтов (вызывается один раз)
    private void initSpikeFrames(Context context) {
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike0);
        spikeFrames = new Bitmap[10];
        Matrix matrix = new Matrix();

        for (int i = 0; i < 10; i++) {
            matrix.reset();
            matrix.postRotate(i * 36); // Поворот на 36 градусов
            spikeFrames[i] = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        }
    }

    // Возвращает текущее изображение спайка для отрисовки
    public Bitmap getSpike(int spikeFrame) {
        return spikeFrames[spikeFrame];
    }

    // Ширина спайка (одинакова для всех кадров)
    public int getSpikeWidth() {
        return spikeFrames[0].getWidth();
    }

    // Высота спайка
    public int getSpikeHeight() {
        return spikeFrames[0].getHeight();
    }

    // Сброс позиции и скорости спайка — вызывается при падении или столкновении
    public void resetPosition() {
        spikeX = random.nextInt(GameView.dWidth - getSpikeWidth());

        // Задаём Y за пределами экрана сверху случайно в диапазоне от -200 до -800
        spikeY = -200 - random.nextInt(600);

        // Скорость падения в диапазоне 35-50 пикселей за тик
        spikeVelocity = 35 + random.nextInt(16);
    }
}
