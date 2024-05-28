package com.stingach.dm.savethecube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

// Класс для бомбы
public class Spike {
    // Массив битов для анимации бомбы
    Bitmap spike[] = new Bitmap[3];
    // Индекс текущего кадра анимации бомбы
    int spikeFrame = 0;
    // Позиция X и Y спика на экране
    int spikeX, spikeY;
    // Скорость движения бомбы
    int spikeVelocity;
    // Генератор случайных чисел для позиционирования и скорости бомбы
    Random random;

    public Spike(Context context) {
        // Загрузка текстур бомб из ресурсов
        spike[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike0);
        spike[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike1);
        spike[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike2);
        // Инициализация генератора случайных чисел
        random = new Random();
        // Перезапуск позиции бомбы
        resetPosition();
    }

    // Получение бомбы по индексу кадра
    public Bitmap getSpike(int spikeFrame) {
        return spike[spikeFrame];
    }

    // Получение ширины бомбы
    public int getSpikeWidth() {
        return spike[0].getWidth();
    }

    // Получение высоты бомбы
    public int getSpikeHeight() {
        return spike[0].getHeight();
    }

    // Перезапуск позиции бомбы
    public void resetPosition() {
        // Расположение бомбы в пределах экрана с заданной скоростью
        spikeX = random.nextInt(GameView.dWidth - getSpikeWidth());
        spikeY = -200 + random.nextInt(600) * -1;
        spikeVelocity = 35 + random.nextInt(16);
    }
}
