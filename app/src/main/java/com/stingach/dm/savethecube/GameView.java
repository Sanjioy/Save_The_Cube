package com.stingach.dm.savethecube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Random;

// Класс представления игрового поля
public class GameView extends View {
    // Изображения для фона, земли и куба
    Bitmap background, ground, cube;
    // Области для рисования фонового изображения и земли
    Rect rectBackground, rectGround;
    // Контекст и обработчик событий
    Context context;
    Handler handler;
    // Время обновления экрана
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    // Рисовщики для текста и здоровья
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    // Размер текста
    float TEXT_SIZE = 120;
    // Текущие очки и жизни
    int points = 0;
    int life = 3;
    // Ширина и высота экрана
    static int dWidth, dHeight;
    // Генератор случайных чисел
    Random random;
    // Позиция куба
    float cubeX, cubeY;
    float oldX;
    float oldCubeX;
    // Списки спиков и взрывов
    ArrayList<Spike> spikes;
    ArrayList<Explosion> explosions;

    // Конструктор класса GameView
    public GameView(Context context) {
        super(context);
        this.context = context;
        // Загрузка изображений
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        cube = BitmapFactory.decodeResource(getResources(), R.drawable.cube);
        // Получение размеров экрана
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        // Настройка областей для рисования
        rectBackground = new Rect(0, 0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);
        // Инициализация обработчика и рутинного обновления
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        // Настройка рисовщиков
        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.hackney_block_regular));
        healthPaint.setColor(Color.GREEN);
        // Инициализация генератора случайных чисел и позиций
        random = new Random();
        cubeX = dWidth / 2 - cube.getWidth() / 2;
        cubeY = dHeight - ground.getHeight() - cube.getHeight();
        // Создание списков спиков и взрывов
        spikes = new ArrayList<>();
        explosions = new ArrayList<>();
        // Добавление трех спиков
        for (int i = 0; i < 3; i++) {
            Spike spike = new Spike(context);
            spikes.add(spike);
        }
    }

    // Отрисовка элементов на экране
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Рисование фона и земли
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        // Рисование куба
        canvas.drawBitmap(cube, cubeX, cubeY, null);
        // Отрисовка бомб
        for (int i = 0; i < spikes.size(); i++) {
            canvas.drawBitmap(spikes.get(i).getSpike(spikes.get(i).spikeFrame), spikes.get(i).spikeX, spikes.get(i).spikeY,null);
            spikes.get(i).spikeFrame++;
            if (spikes.get(i).spikeFrame > 2) {
                spikes.get(i).spikeFrame = 0;
            }
            spikes.get(i).spikeY +=  spikes.get(i).spikeVelocity;
            if (spikes.get(i).spikeY + spikes.get(i).getSpikeHeight() >= dHeight - ground.getHeight()) {
                points += 10;
                Explosion explosion = new Explosion(context);
                explosion.explosionX = spikes.get(i).spikeX;
                explosion.explosionY = spikes.get(i).spikeY;
                explosions.add(explosion);
                spikes.get(i).resetPosition();
            }
        }
        // Проверка столкновений бомб с кубом
        for (int i = 0; i < spikes.size(); i++) {
            if (spikes.get(i).spikeX + spikes.get(i).getSpikeWidth() >= cubeX
                    && spikes.get(i).spikeX <= cubeX + cube.getWidth()
                    && spikes.get(i).spikeY + spikes.get(i).getSpikeWidth() >= cubeY
                    && spikes.get(i).spikeY + spikes.get(i).getSpikeWidth() <= cubeY + cube.getHeight()) {
                life--;
                spikes.get(i).resetPosition();
                if (life == 0) {
                    Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }
        // Отрисовка взрывов
        for (int i = 0; i < explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).explosionX,
                    explosions.get(i).explosionY, null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 3) {
                explosions.remove(i);
            }
        }
        // Отрисовка полосы здоровья
        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth-200, 30, dWidth-200+60*life, 80, healthPaint);
        // Отрисовка счета
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        // Запуск следующего кадра через заданный промежуток времени
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    // Обработка касаний экрана
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= cubeY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.getX();
                oldCubeX = cubeX;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                float newCubeX = oldCubeX - shift;
                if (newCubeX <= 0)
                    cubeX = 0;
                else if (newCubeX >= dWidth - cube.getWidth())
                    cubeX = dWidth - cube.getWidth();
                else
                    cubeX = newCubeX;
            }
        }
        return true;
    }
}
