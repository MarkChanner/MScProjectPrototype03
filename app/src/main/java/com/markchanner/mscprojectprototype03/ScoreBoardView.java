package com.markchanner.mscprojectprototype03;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ScoreBoardView extends View {

    public static final int ZERO = 0;

    private Bitmap scoreBitmap;

    public ScoreBoardView(Context context, int viewX, int viewY) {
        super(context);
        prepareCanvas(context, viewX, viewY);
    }

    private void prepareCanvas(Context context, int viewX, int viewY) {
        Paint paint = new Paint();
        scoreBitmap = Bitmap.createBitmap(viewX, viewY, Bitmap.Config.RGB_565);
        Canvas scoreCanvas = new Canvas(scoreBitmap);

        paint.setColor(context.getResources().getColor(R.color.niceyellow));
        scoreCanvas.drawRect(ZERO, ZERO, viewX, viewY, paint);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        scoreCanvas.drawRect(ZERO, ZERO, viewX, viewY, paint);

        paint.setTextSize(80);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        scoreCanvas.drawText("Score", viewX / 2, 100, paint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(scoreBitmap, ZERO, ZERO, null);
    }
}
