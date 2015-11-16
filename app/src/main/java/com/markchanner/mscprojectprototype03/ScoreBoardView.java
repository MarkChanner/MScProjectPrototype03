package com.markchanner.mscprojectprototype03;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ScoreBoardView extends View {

    public static final int X_MAX = BoardImpl.X_MAX;
    public static final int Y_MAX = BoardImpl.Y_MAX;
    public static final int ZERO = 0;

    private int screenX;
    private int screenY;
    private int emoX;
    private int emoY;
    private Paint backgroundColour;
    private Paint gridLineColour;

    public ScoreBoardView(Context context, int screenX, int screenY) {
        super(context);
        this.screenX = screenX;
        this.screenY = screenY;
        this.emoX = (screenX / X_MAX);
        this.emoY = (screenY / Y_MAX);
        startGame(context);
    }

    private void startGame(Context context) {
        backgroundColour = new Paint();
        gridLineColour = new Paint();

        backgroundColour.setColor(context.getResources().getColor(R.color.scoreboard));
        gridLineColour.setStyle(Paint.Style.STROKE);
        gridLineColour.setStrokeWidth(5f);
        gridLineColour.setColor(Color.BLACK);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawRect(ZERO, ZERO, getWidth(), getHeight(), backgroundColour);
        canvas.drawRect(ZERO, ZERO, getWidth(), getHeight(), gridLineColour);

    }
}
