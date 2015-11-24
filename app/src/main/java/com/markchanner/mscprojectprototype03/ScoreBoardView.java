package com.markchanner.mscprojectprototype03;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ScoreBoardView extends SurfaceView implements Runnable {

    public static final int ZERO = 0;
    private SurfaceHolder holder;
    private Bitmap scoreBitmap;
    private Paint paint;
    private int score = 0;
    private int pointsX;
    private int pointsY;

    private Thread runner = null;
    volatile boolean running = false;
    private final Object lock = new Object();
    volatile boolean needsUpdating = false;

    public ScoreBoardView(Context context, int viewX, int viewY) {
        super(context);
        holder = getHolder();
        prepareCanvas(context, viewX, viewY);
    }

    private void prepareCanvas(Context context, int viewX, int viewY) {
        scoreBitmap = Bitmap.createBitmap(viewX, viewY, Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(scoreBitmap);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(context, R.color.niceyellow));
        tempCanvas.drawRect(ZERO, ZERO, viewX, viewY, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.BLACK);
        tempCanvas.drawRect(ZERO, ZERO, viewX, viewY, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        float scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, context.getResources().getDisplayMetrics());
        paint.setTextSize(scale);
        tempCanvas.drawText("SCORE", (viewX / 2), (viewY / 10), paint);

        scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 50, context.getResources().getDisplayMetrics());
        paint.setTextSize(scale);
        pointsX = (viewX / 2);
        pointsY = (viewY / 3);
    }

    public void resume() {
        running = true;
        runner = new Thread(this);
        runner.start();
    }

    public void pause() {
        running = false;
        while (true) {
            try {
                runner.join();
                return;
            } catch (InterruptedException e) {
                // try again
            }
        }
    }

    @Override
    public void run() {
        Canvas canvas;
        while (running) {
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                drawIt(canvas);
                holder.unlockCanvasAndPost(canvas);
                synchronized (lock) {
                    while (!needsUpdating) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void drawIt(Canvas canvas) {
        canvas.drawBitmap(scoreBitmap, ZERO, ZERO, null);
        canvas.drawText("" + score, pointsX, pointsY, paint);
    }

    protected void incrementScore(int points) {
        synchronized (lock) {
            score += points;
            needsUpdating = true;
            lock.notifyAll();
        }
    }
}
