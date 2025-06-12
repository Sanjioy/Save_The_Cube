package com.stingach.dm.savethecube.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.main.GameView;

import java.util.Random;

// Класс для одной бомбы (спайка).
public class Spike {

    // Статический массив повёрнутых спрайтов, общий для всех спайков.
    private static Bitmap[] spikeFrames;

    // Текущий кадр анимации.
    public int spikeFrame = 0;

    // Счётчик кадров для плавности анимации.
    public int frameCounter = 0;

    // Координаты спайка.
    public int spikeX;
    public int spikeY;

    // Скорость падения спайка.
    public int spikeVelocity;

    // Статический генератор случайных чисел, общий для всех спайков.
    private static final Random random = new Random();

    // Конструктор, инициализирует позицию и подготавливает спрайты (при первом вызове).
    public Spike(Context context) {
        // Если изображения ещё не загружены — инициализируем их.
        if (spikeFrames == null) {
            initSpikeFrames(context);
        }

        // Устанавливаем стартовую позицию и скорость.
        resetPosition();
    }

    // Метод инициализации повёрнутых спрайтов спайка.
    private void initSpikeFrames(Context context) {
        // Загружаем исходное изображение.
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike0);

        // Создаём массив для хранения 10 повёрнутых кадров.
        spikeFrames = new Bitmap[10];

        // Матрица для поворота изображений.
        Matrix matrix = new Matrix();

        // Генерация 10 кадров с поворотом на 36 градусов.
        for (int i = 0; i < 10; i++) {
            matrix.reset();
            matrix.postRotate(i * 36);
            spikeFrames[i] = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        }
    }

    // Получение текущего изображения спайка.
    public Bitmap getSpike(int spikeFrame) {
        return spikeFrames[spikeFrame];
    }

    // Получение ширины спайка.
    public int getSpikeWidth() {
        return spikeFrames[0].getWidth();
    }

    // Получение высоты спайка.
    public int getSpikeHeight() {
        return spikeFrames[0].getHeight();
    }

    // Сброс позиции и скорости спайка.
    public void resetPosition() {
        // Случайная X координата в пределах ширины экрана.
        spikeX = random.nextInt(GameView.dWidth - getSpikeWidth());

        // Y координата — за экраном, между -200 и -800.
        spikeY = -200 - random.nextInt(600);

        // Случайная скорость падения от 35 до 50.
        spikeVelocity = 35 + random.nextInt(16);
    }
}

