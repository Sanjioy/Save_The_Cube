// Класс GameView отвечает за отрисовку и логику игры Save The Cube.
package com.stingach.dm.savethecube;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameView extends View {
    // Список бонусов в виде сердец.
    ArrayList<HeartBonus> heartBonuses;

    // Следующее количество очков, при котором появится бонус-сердце.
    int nextHeartBonusPoints = 200;
    final int HEART_BONUS_INTERVAL_MIN = 200;
    final int HEART_BONUS_INTERVAL_MAX = 300;

    // Флаги и счетчики для эффекта получения урона.
    boolean isCubeHit = false;
    int hitEffectCounter = 0;
    final int HIT_EFFECT_DURATION = 10;

    // Флаги и счетчики для эффекта получения сердца.
    boolean isCubeGotHeart = false;
    int heartEffectCounter = 0;
    final int HEART_EFFECT_DURATION = 15;

    // Изображения.
    Bitmap background, ground, cube;
    Bitmap lifeHeart;
    Rect rectBackground, rectGround;

    // Контекст приложения и обработчик.
    Context context;
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;

    // Параметры текста и жизней.
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    float TEXT_SIZE = 120;

    // Счет очков и количество жизней.
    int points = 0;
    int life = 3;

    // Размеры экрана.
    static int dWidth, dHeight;

    // Генератор случайных чисел.
    Random random;

    // Положение куба.
    float cubeX, cubeY;
    float oldX;
    float oldCubeX;

    // Списки шипов и взрывов.
    ArrayList<Spike> spikes;
    ArrayList<Explosion> explosions;

    // Paint для прозрачности бонусов.
    Paint alphaPaint = new Paint();

    // Конструктор GameView инициализирует все игровые ресурсы и переменные.
    public GameView(Context context) {
        super(context);
        this.context = context;

        // Загрузка ресурсов.
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        cube = BitmapFactory.decodeResource(getResources(), R.drawable.cube);
        lifeHeart = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        lifeHeart = Bitmap.createScaledBitmap(lifeHeart, 120, 120, false);

        // Получение размеров экрана.
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;

        // Создание прямоугольников для фона и земли.
        rectBackground = new Rect(0, 0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);

        handler = new Handler();
        runnable = this::invalidate; // Обновление экрана.

        // Настройка шрифта и цвета текста.
        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.hackney_block_regular));

        // Настройка кисти для здоровья.
        healthPaint.setColor(Color.GREEN);

        random = new Random();

        // Инициализация позиции куба.
        cubeX = (float) dWidth / 2 - (float) cube.getWidth() / 2;
        cubeY = dHeight - ground.getHeight() - cube.getHeight();

        // Инициализация списков объектов.
        spikes = new ArrayList<>();
        explosions = new ArrayList<>();
        heartBonuses = new ArrayList<>();

        // Создание начальных шипов.
        for (int i = 0; i < 3; i++) {
            spikes.add(new Spike(context));
        }
    }

    // Метод отрисовки всех игровых элементов.
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Отрисовка фона и земли.
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);

        @SuppressLint("DrawAllocation") Paint cubePaint = new Paint();
        float drawX = cubeX;
        float drawY = cubeY;
        float drawWidth = cube.getWidth();
        float drawHeight = cube.getHeight();

        // Эффект урона для куба.
        if (isCubeHit) {
            hitEffectCounter--;
            int alpha = (hitEffectCounter % 2 == 0) ? 255 : 100;
            cubePaint.setAlpha(alpha);

            float scale = 0.8f;
            drawWidth = cube.getWidth() * scale;
            drawHeight = cube.getHeight() * scale;
            drawX = cubeX + (cube.getWidth() - drawWidth) / 2;
            drawY = cubeY + (cube.getHeight() - drawHeight) / 2;

            if (hitEffectCounter <= 0) {
                isCubeHit = false;
                cubePaint.setAlpha(255);
            }
        }

        // Эффект получения сердца для куба.
        if (isCubeGotHeart) {
            heartEffectCounter--;
            if (heartEffectCounter <= 0) {
                isCubeGotHeart = false;
            }

            double phase = (double) heartEffectCounter / HEART_EFFECT_DURATION * Math.PI * 4;
            float scale = 1.0f + 0.15f * (float) Math.sin(phase);
            drawWidth = cube.getWidth() * scale;
            drawHeight = cube.getHeight() * scale;
            drawX = cubeX + (cube.getWidth() - drawWidth) / 2;
            drawY = cubeY + (cube.getHeight() - drawHeight) / 2;
        }

        // Отрисовка куба.
        @SuppressLint("DrawAllocation") Rect destRect = new Rect(
                (int) drawX,
                (int) drawY,
                (int) (drawX + drawWidth),
                (int) (drawY + drawHeight)
        );
        canvas.drawBitmap(cube, null, destRect, cubePaint);

        // Эффект наложения при получении сердца.
        if (isCubeGotHeart) {
            double phase = (double) heartEffectCounter / HEART_EFFECT_DURATION * Math.PI * 4;
            int overlayAlpha = 100 + (int)(155 * (0.5 + 0.5 * Math.sin(phase)));

            @SuppressLint("DrawAllocation") Paint overlayPaint = new Paint();
            overlayPaint.setColor(Color.GREEN);
            overlayPaint.setAlpha(overlayAlpha);
            overlayPaint.setStyle(Paint.Style.FILL);

            canvas.drawRect(destRect, overlayPaint);
        }

        // Обновление и отрисовка шипов.
        for (Spike spike : spikes) {
            canvas.drawBitmap(spike.getSpike(spike.spikeFrame), spike.spikeX, spike.spikeY, null);
            spike.frameCounter++;
            if (spike.frameCounter >= 3) {
                spike.spikeFrame = (spike.spikeFrame + 1) % 10;
                spike.frameCounter = 0;
            }
            spike.spikeY += (int) (spike.spikeVelocity + ((float) points / 100));
            if (spike.spikeY + spike.getSpikeHeight() >= dHeight - ground.getHeight()) {
                points += 10;
                @SuppressLint("DrawAllocation") Explosion explosion = new Explosion(context);
                explosion.explosionX = spike.spikeX;
                explosion.explosionY = spike.spikeY;
                explosions.add(explosion);
                spike.resetPosition();
            }
        }

        // Проверка столкновения шипов с кубом.
        for (Spike spike : spikes) {
            if (spike.spikeX + spike.getSpikeWidth() >= cubeX
                    && spike.spikeX <= cubeX + cube.getWidth()
                    && spike.spikeY + spike.getSpikeWidth() >= cubeY
                    && spike.spikeY + spike.getSpikeWidth() <= cubeY + cube.getHeight()) {
                isCubeHit = true;
                hitEffectCounter = HIT_EFFECT_DURATION;
                life--;

                @SuppressLint("DrawAllocation") Explosion explosion = new Explosion(context);
                explosion.explosionX = spike.spikeX;
                explosion.explosionY = spike.spikeY;
                explosions.add(explosion);
                spike.resetPosition();

                if (life == 0) {
                    @SuppressLint("DrawAllocation") Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }

        // Обработка взрывов.
        Iterator<Explosion> explosionIterator = explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            canvas.drawBitmap(explosion.getExplosion(explosion.explosionFrame), explosion.explosionX,
                    explosion.explosionY, null);
            explosion.explosionFrame++;
            if (explosion.explosionFrame > 3) {
                explosionIterator.remove();
            }
        }

        // Отрисовка сердец, отображающих количество жизней.
        for (int i = 0; i < life; i++) {
            float x = dWidth - (i + 1) * (lifeHeart.getWidth() + 10) - 20;
            float y = 20;
            canvas.drawBitmap(lifeHeart, x, y, null);
        }

        // Отрисовка очков.
        canvas.drawText(String.valueOf(points), 20, TEXT_SIZE, textPaint);

        // Добавление бонусов-сердец при достижении нужного количества очков.
        if (points >= nextHeartBonusPoints) {
            heartBonuses.add(new HeartBonus(context));
            nextHeartBonusPoints += HEART_BONUS_INTERVAL_MIN + random.nextInt(HEART_BONUS_INTERVAL_MAX - HEART_BONUS_INTERVAL_MIN + 1);
        }

        // Обновление и отрисовка бонусов-сердец.
        Iterator<HeartBonus> heartIterator = heartBonuses.iterator();
        while (heartIterator.hasNext()) {
            HeartBonus hb = heartIterator.next();
            hb.y += hb.velocity + ((float) points / 100);

            hb.updateAlpha();
            alphaPaint.setAlpha(hb.alpha);
            canvas.drawBitmap(hb.bitmap, hb.x, hb.y, alphaPaint);

            if (hb.x + hb.getWidth() >= cubeX && hb.x <= cubeX + cube.getWidth()
                    && hb.y + hb.getHeight() >= cubeY && hb.y <= cubeY + cube.getHeight()) {
                if (life < 3) {
                    life++;
                }
                isCubeGotHeart = true;
                heartEffectCounter = HEART_EFFECT_DURATION;
                heartIterator.remove();
                continue;
            }

            if (hb.y + hb.getHeight() >= dHeight - ground.getHeight()) {
                heartIterator.remove();
            }
        }

        // Отложенный вызов перерисовки экрана.
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    // Обработка касания экрана.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
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
                cubeX = Math.max(0, Math.min(newCubeX, dWidth - cube.getWidth()));
            }
        }
        return true;
    }

    // Метод для поддержки Accessibility.
    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
