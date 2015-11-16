package com.markchanner.mscprojectprototype03;

import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class GameView extends SurfaceView implements Runnable {

    public static final int X_MAX = BoardImpl.X_MAX;
    public static final int Y_MAX = BoardImpl.Y_MAX;
    public static final int ONE_MILLISECOND = 1;
    public static final int ZERO = 0;

    private final Rect highlightSelectionRect = new Rect();
    private final Rect highlightMatchRect = new Rect();
    private SurfaceHolder surfaceHolder;
    private Paint backgroundColour;
    private Paint gridLineColour;
    private Paint selectionFill;

    private Context context;
    private int emoWidth;
    private int emoHeight;
    private Board board;
    private Selection selections;
    private Thread gameViewThread = null;

    volatile boolean running = false;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        this.context = context;
        surfaceHolder = getHolder();
        emoWidth = screenX / X_MAX;
        emoHeight = screenY / Y_MAX;
        startGame();
    }

    private void startGame() {
        backgroundColour = new Paint();
        backgroundColour.setColor(Color.parseColor("#7EC0EE"));

        gridLineColour = new Paint();
        gridLineColour.setStyle(Paint.Style.STROKE);
        gridLineColour.setStrokeWidth(2f);
        gridLineColour.setColor(Color.BLACK);

        selectionFill = new Paint();
        selectionFill.setColor(Color.parseColor("#fff2a8"));

        board = new BoardImpl(context, emoWidth, emoHeight);
        selections = new SelectionImpl();
    }

    public void resume() {
        running = true;
        gameViewThread = new Thread(this);
        gameViewThread.start();
    }

    @Override
    public void run() {
        Canvas canvas;
        while (running) {
            if (surfaceHolder.getSurface().isValid()) {
                canvas = surfaceHolder.lockCanvas();
                synchronized (this) {
                    board.updateEmoticonMovements();
                    drawIt(canvas);
                    control();
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void drawIt(Canvas canvas) {
        // Draw the background
        canvas.drawRect(ZERO, ZERO, getWidth(), getHeight(), backgroundColour);

        // Highlight an emoticon if Emoticon's isPartOfMatch boolean is true
        canvas.drawRect(highlightSelectionRect, selectionFill);
        for (int i = 0; i < X_MAX; i++) {
            // Vertical grid lines
            canvas.drawLine(i * emoWidth, ZERO, i * emoWidth, getHeight(), gridLineColour);
        }
        for (int i = 0; i < Y_MAX; i++) {
            // Horizontal grid lines
            canvas.drawLine(ZERO, i * emoHeight, getWidth(), i * emoHeight, gridLineColour);
        }

        Emoticon[][] emoticons = board.getEmoticons();
        for (int y = Y_MAX - 1; y >= 0; y--) {
            for (int x = 0; x < X_MAX; x++) {
                Emoticon e = emoticons[x][y];

                // if isPartOfMatch is true, highlight that emoticon's background
                if (e.isPartOfMatch()) {
                    int emoX = e.getScreenPositionX();
                    int emoY = e.getScreenPositionY();
                    highlightMatchRect.set(emoX, emoY, (emoX + emoWidth), (emoY + emoHeight));
                    canvas.drawRect(highlightMatchRect, selectionFill);
                    canvas.drawRect(highlightMatchRect, gridLineColour);
                }
                canvas.drawBitmap(e.getBitmap(), e.getScreenPositionX(), e.getScreenPositionY(), null);
            }
        }
    }

    public void control() {
        try {
            Thread.sleep(ONE_MILLISECOND);
        } catch (InterruptedException e) {

        }
    }

    public void control(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {

        }
    }

    public void pause() {
        running = false;
        while (true) {
            try {
                gameViewThread.join();
                return;
            } catch (InterruptedException e) {
                // try again
            }
        }
    }

    protected void highlightSelection(int x, int y) {
        highlightSelectionRect.set(x * emoWidth, y * emoHeight, (x * emoWidth) + emoWidth, (y * emoHeight) + emoHeight);
    }

    protected void unHighlightSelection() {
        highlightSelectionRect.setEmpty();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int screenX = (int) event.getX();
        int screenY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                storeSelection(screenX / emoWidth, screenY / emoHeight);
                break;
        }
        return true;
    }

    private void storeSelection(int x, int y) {
        highlightSelection(x, y);
        if (!(selections.selection01Made())) {
            selections.setSelection01(x, y);
        } else {
            selections.setSelection02(x, y);
            checkSelections(x, y);
        }
    }

    private void checkSelections(int x, int y) {
        unHighlightSelection();
        if (!(selections.sameSelectionMadeTwice())) {
            if (selections.adjacentSelections()) {
                board.processSelections(this, selections);
            } else {
                highlightSelection(x, y);
                selections.setSelection02ToSelection01();
            }
        } else {
            selections.resetUserSelections();
        }
    }
}