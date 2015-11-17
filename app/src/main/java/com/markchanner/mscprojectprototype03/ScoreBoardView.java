package com.markchanner.mscprojectprototype03;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ScoreBoardView extends View {

    public static final int ZERO = 0;
    private Bitmap scoreBoardBitmap;

    public ScoreBoardView(Context context, int viewX, int viewY) {
        super(context);
        prepareCanvas(context, viewX, viewY);
    }

    private void prepareCanvas(Context context, int viewX, int viewY) {
        Paint borderColour = new Paint();
        borderColour.setColor(context.getResources().getColor(R.color.scoreboard));

        Paint gridLineColour = new Paint();
        gridLineColour.setStyle(Paint.Style.STROKE);
        gridLineColour.setStrokeWidth(5f);
        gridLineColour.setColor(Color.BLACK);

        scoreBoardBitmap = Bitmap.createBitmap(viewX, viewY, Bitmap.Config.RGB_565);
        Canvas scoreCanvas = new Canvas(scoreBoardBitmap);
        scoreCanvas.drawRect(ZERO, ZERO, viewX, viewY, borderColour);
        scoreCanvas.drawRect(ZERO, ZERO, viewX, viewY, gridLineColour);

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(scoreBoardBitmap, ZERO, ZERO, null);

    }
}
