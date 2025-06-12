// Класс GameView отвечает за отрисовку и логику игры Save The Cube.
package com.stingach.dm.savethecube.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.view.*;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.stingach.dm.savethecube.utils.Explosion;
import com.stingach.dm.savethecube.utils.HeartBonus;
import com.stingach.dm.savethecube.R;
import com.stingach.dm.savethecube.utils.Spike;

import java.util.*;

public class GameView extends View {
    // Используем final для константных значений.
    private static final int HEART_BONUS_INTERVAL_MIN = 200;
    // Максимальный интервал между бонусами-сердцами.
    private static final int HEART_BONUS_INTERVAL_MAX = 300;
    // Длительность эффекта удара.
    private static final int HIT_EFFECT_DURATION = 10;
    // Длительность эффекта получения сердца.
    private static final int HEART_EFFECT_DURATION = 15;
    // Интервал между кадрами отрисовки.
    private static final long UPDATE_MILLIS = 30;

    // Контекст приложения.
    private final Context context;
    // Обработчик для обновления экрана.
    private final Handler handler = new Handler();
    // Задача, вызывающая перерисовку экрана.
    private final Runnable runnable = this::invalidate;

    // Изображения и рендеринг.
    private final Bitmap background;
    private final Bitmap ground;
    private final Bitmap cube;
    private final Bitmap lifeHeart;
    private final Rect rectBackground;
    private final Rect rectGround;
    private final Paint textPaint = new Paint();
    private final Paint alphaPaint = new Paint();

    // Экран и позиционирование.
    public static int dWidth, dHeight;
    private float cubeX;
    private final float cubeY;
    private float oldX, oldCubeX;

    // Игровое состояние.
    private int points = 0;
    private int life = 3;
    private boolean isCubeHit = false;
    private boolean isCubeGotHeart = false;
    private int hitEffectCounter = 0;
    private int heartEffectCounter = 0;
    private int nextHeartBonusPoints = 200;

    // Генератор случайных чисел.
    private final Random random = new Random();

    // Объекты игры.
    private final ArrayList<Spike> spikes = new ArrayList<>();
    private final ArrayList<Explosion> explosions = new ArrayList<>();
    private final ArrayList<HeartBonus> heartBonuses = new ArrayList<>();

    // Конструктор инициализирует все ресурсы.
    public GameView(Context context) {
        super(context);
        this.context = context;

        // Загружаем фоновое изображение.
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_1);
        // Загружаем изображение земли.
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        // Загружаем изображение куба.
        cube = BitmapFactory.decodeResource(getResources(), R.drawable.cube);
        // Загружаем и масштабируем иконку сердца.
        Bitmap rawHeart = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        lifeHeart = Bitmap.createScaledBitmap(rawHeart, 120, 120, false);

        // Получаем размеры экрана.
        Point size = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(size);
        dWidth = size.x;
        dHeight = size.y;

        // Определяем прямоугольники для отрисовки фона и земли.
        rectBackground = new Rect(0, 0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);

        // Настраиваем стиль текста очков.
        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(120);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.hackney_block_regular));

        // Устанавливаем начальную позицию куба.
        cubeX = dWidth / 2f - cube.getWidth() / 2f;
        cubeY = dHeight - ground.getHeight() - cube.getHeight();

        // Добавляем стартовые шипы.
        for (int i = 0; i < 3; i++) {
            spikes.add(new Spike(context));
        }
    }

    // Метод отрисовки игрового экрана.
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Отрисовка фона и земли.
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);

        // Подготовка к отрисовке куба с эффектами.
        @SuppressLint("DrawAllocation") Paint cubePaint = new Paint();
        float drawX = cubeX, drawY = cubeY;
        float drawWidth = cube.getWidth(), drawHeight = cube.getHeight();

        // Применение эффекта урона.
        if (isCubeHit) {
            hitEffectCounter--;
            cubePaint.setAlpha((hitEffectCounter % 2 == 0) ? 255 : 100);
            float scale = 0.8f;
            drawWidth *= scale;
            drawHeight *= scale;
            drawX += (cube.getWidth() - drawWidth) / 2f;
            drawY += (cube.getHeight() - drawHeight) / 2f;

            if (hitEffectCounter <= 0) {
                isCubeHit = false;
            }
        }

        // Применение эффекта получения сердца.
        if (isCubeGotHeart) {
            heartEffectCounter--;
            double phase = (double) heartEffectCounter / HEART_EFFECT_DURATION * Math.PI * 4;
            float scale = 1.0f + 0.15f * (float) Math.sin(phase);
            drawWidth = cube.getWidth() * scale;
            drawHeight = cube.getHeight() * scale;
            drawX = cubeX + (cube.getWidth() - drawWidth) / 2f;
            drawY = cubeY + (cube.getHeight() - drawHeight) / 2f;

            if (heartEffectCounter <= 0) {
                isCubeGotHeart = false;
            }
        }

        // Отрисовка куба.
        @SuppressLint("DrawAllocation") Rect destRect = new Rect((int) drawX, (int) drawY, (int) (drawX + drawWidth), (int) (drawY + drawHeight));
        canvas.drawBitmap(cube, null, destRect, cubePaint);

        // Эффект подсветки при получении сердца.
        if (isCubeGotHeart) {
            @SuppressLint("DrawAllocation") Paint overlayPaint = new Paint();
            double phase = (double) heartEffectCounter / HEART_EFFECT_DURATION * Math.PI * 4;
            overlayPaint.setColor(Color.GREEN);
            overlayPaint.setAlpha(100 + (int) (155 * (0.5 + 0.5 * Math.sin(phase))));
            overlayPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(destRect, overlayPaint);
        }

        // Обработка шипов.
        for (Spike spike : spikes) {
            canvas.drawBitmap(spike.getSpike(spike.spikeFrame), spike.spikeX, spike.spikeY, null);
            spike.frameCounter++;
            if (spike.frameCounter >= 3) {
                spike.spikeFrame = (spike.spikeFrame + 1) % 10;
                spike.frameCounter = 0;
            }
            spike.spikeY += (int) (spike.spikeVelocity + (points / 100f));

            if (spike.spikeY + spike.getSpikeHeight() >= dHeight - ground.getHeight()) {
                points += 10;
                explosions.add(new Explosion(context));
                explosions.get(explosions.size() - 1).explosionX = spike.spikeX;
                explosions.get(explosions.size() - 1).explosionY = spike.spikeY;
                spike.resetPosition();
            }
        }

        // Обработка столкновений.
        for (Spike spike : spikes) {
            if (spike.spikeX + spike.getSpikeWidth() >= cubeX && spike.spikeX <= cubeX + cube.getWidth()
                    && spike.spikeY + spike.getSpikeHeight() >= cubeY && spike.spikeY <= cubeY + cube.getHeight()) {
                isCubeHit = true;
                hitEffectCounter = HIT_EFFECT_DURATION;
                life--;
                explosions.add(new Explosion(context));
                explosions.get(explosions.size() - 1).explosionX = spike.spikeX;
                explosions.get(explosions.size() - 1).explosionY = spike.spikeY;
                spike.resetPosition();
                if (life == 0) {
                    // Запускаем экран окончания игры при потере всех жизней.
                    Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    return;
                }
            }
        }

        // Отрисовка и обновление взрывов.
        Iterator<Explosion> explosionIterator = explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            canvas.drawBitmap(explosion.getExplosion(explosion.explosionFrame), explosion.explosionX, explosion.explosionY, null);
            explosion.explosionFrame++;
            if (explosion.explosionFrame > 3) {
                explosionIterator.remove();
            }
        }

        // Отрисовка иконок жизни.
        for (int i = 0; i < life; i++) {
            float x = dWidth - (i + 1) * (lifeHeart.getWidth() + 10) - 20;
            canvas.drawBitmap(lifeHeart, x, 20, null);
        }

        // Отображение очков.
        canvas.drawText(String.valueOf(points), 20, 120, textPaint);

        // Добавление бонусов-сердец.
        if (points >= nextHeartBonusPoints) {
            heartBonuses.add(new HeartBonus(context));
            nextHeartBonusPoints += HEART_BONUS_INTERVAL_MIN + random.nextInt(HEART_BONUS_INTERVAL_MAX - HEART_BONUS_INTERVAL_MIN + 1);
        }

        // Отрисовка и обработка бонусов-сердец.
        Iterator<HeartBonus> heartIterator = heartBonuses.iterator();
        while (heartIterator.hasNext()) {
            HeartBonus hb = heartIterator.next();
            hb.y += hb.velocity + (points / 100f);
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

        // Отложенный вызов обновления.
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    // Обработка касаний игрока.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float touchX = event.getX();
        if (event.getY() >= cubeY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = touchX;
                oldCubeX = cubeX;
            } else if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                cubeX = Math.max(0, Math.min(oldCubeX - shift, dWidth - cube.getWidth()));
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