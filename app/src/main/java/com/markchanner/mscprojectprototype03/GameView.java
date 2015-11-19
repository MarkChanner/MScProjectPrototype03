package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;
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
    public static final int ZERO = 0;

    private final Rect highlightSelectionRect = new Rect();
    private final Rect highlightMatchRect = new Rect();
    private SurfaceHolder surfaceHolder;
    private Paint gameBoardColour;
    private Paint gridLineColour;
    private Paint selectionFill;
    private Bitmap gridBitmap;

    private int emoWidth;
    private int emoHeight;
    private Board board;
    private Selection selections;
    private Thread gameViewThread = null;
    private final Monitor monitor = new Monitor();
    private Context context;

    volatile boolean running = false;

    /**
     * Try to a consistent Bitmap
     * configuration throughout (like RGB_565)
     */
    public GameView(Context context, int viewX, int viewY) {
        super(context);
        surfaceHolder = getHolder();
        emoWidth = viewX / X_MAX;
        emoHeight = viewY / Y_MAX;
        prepareCanvas(context, viewX, viewY);
        startGame(context);
    }

    private void prepareCanvas(Context context, int screenX, int screenY) {
        gameBoardColour = new Paint();
        selectionFill = new Paint();
        gridLineColour = new Paint();

        gameBoardColour.setColor(context.getResources().getColor(R.color.gameboard));
        selectionFill.setColor(context.getResources().getColor(R.color.highlightbackground));
        gridLineColour.setColor(Color.BLACK);
        gridLineColour.setStyle(Paint.Style.STROKE);
        gridLineColour.setStrokeWidth(5f);

        gridBitmap = Bitmap.createBitmap(screenX, screenY, Bitmap.Config.RGB_565);
        Canvas gridCanvas = new Canvas(gridBitmap);
        gridCanvas.drawRect(ZERO, ZERO, screenX, screenY, gameBoardColour);
        gridCanvas.drawRect(ZERO, ZERO, screenX, screenY, gridLineColour);
        for (int i = 0; i < X_MAX; i++) {
            gridCanvas.drawLine(i * emoWidth, ZERO, i * emoWidth, screenY, gridLineColour); // Vertical
        }
        for (int i = 0; i < Y_MAX; i++) {
            gridCanvas.drawLine(ZERO, i * emoHeight, screenX, i * emoHeight, gridLineColour); // Horizontal
        }
        gridCanvas.drawBitmap(gridBitmap, ZERO, ZERO, null);
    }

    private void startGame(Context context) {
        board = new BoardImpl(context, monitor, emoWidth, emoHeight);
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
                board.updateSwaps();
                board.updateDrops();
                drawIt(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void drawIt(Canvas canvas) {
        canvas.drawBitmap(gridBitmap, ZERO, ZERO, null); // Draws background
        // Highlight the background of a selected Emoticon
        canvas.drawRect(highlightSelectionRect, selectionFill);
        canvas.drawRect(highlightSelectionRect, gridLineColour);

        Emoticon[][] emoticons = board.getEmoticons();
        for (int y = Y_MAX - 1; y >= 0; y--) {
            for (int x = 0; x < X_MAX; x++) {
                Emoticon e = emoticons[x][y];

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