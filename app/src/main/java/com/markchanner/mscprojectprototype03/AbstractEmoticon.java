package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public abstract class AbstractEmoticon implements Emoticon {

    public static final int DIVISOR = 2;

    private int arrayX;
    private int arrayY;
    private int emoWidth;
    private int emoHeight;
    private Bitmap bitmap;
    private String emoticonType;

    private int screenPositionX;
    private int screenPositionY;
    private int pixelMovement; // consider making this a constructor argument
    volatile boolean lowerEmoticon;
    volatile boolean swapUp;
    volatile boolean swapDown;
    volatile boolean swapRight;
    volatile boolean swapLeft;
    volatile boolean isPartOfMatch;

    public AbstractEmoticon(int arrayX, int arrayY, int emoWidth, int emoHeight, Bitmap bitmap, String emoticonType, int offScreenStartPositionY) {
        this.arrayX = arrayX;
        this.arrayY = arrayY;
        this.emoWidth = emoWidth;
        this.emoHeight = emoHeight;
        this.bitmap = bitmap;
        this.emoticonType = emoticonType;

        pixelMovement = 32;
        screenPositionX = (arrayX * emoWidth);
        screenPositionY = (offScreenStartPositionY * emoHeight);
        lowerEmoticon = true;
    }

    @Override
    public void update() {
        if (lowerEmoticonActivated()) {
            lowerEmoticon();
        } else if (swapUpActivated()) {
            swapUp();
        } else if (swapDownActivated()) {
            swapDown();
        } else if (swapRightActivated()) {
            swapRight();
        } else if (swapLeftActivated()) {
            swapLeft();
        }
    }

    @Override
    public void setIsPartOfMatch(boolean bool) {
        isPartOfMatch = true;
    }

    @Override
    public boolean isPartOfMatch() {
        return isPartOfMatch;
    }

    @Override
    public void setLowerEmoticon(boolean bool) {
        lowerEmoticon = bool;
    }

    @Override
    public boolean lowerEmoticonActivated() {
        return lowerEmoticon;
    }

    @Override
    public void lowerEmoticon() {
        int newPosition = (arrayY * emoHeight);
        int pixelRate = pixelMovement;
        while (screenPositionY + pixelRate > newPosition) {
            pixelRate /= DIVISOR;
        }
        screenPositionY += pixelRate;
        if (screenPositionY >= newPosition) {
            lowerEmoticon = false;
        }
    }

    @Override
    public void setSwapUp(boolean swapUp) {
        this.swapUp = swapUp;
    }

    @Override
    public boolean swapUpActivated() {
        return swapUp;
    }

    @Override
    public void swapUp() {
        int newPosition = emoHeight * arrayY;
        int pixelRate = pixelMovement;
        while (screenPositionY - pixelRate < newPosition) {
            pixelRate /= DIVISOR;
        }
        screenPositionY -= pixelRate;
        if (screenPositionY <= newPosition) swapUp = false;
    }

    @Override
    public void setSwapDown(boolean swapDown) {
        this.swapDown = swapDown;
    }

    @Override
    public boolean swapDownActivated() {
        return swapDown;
    }

    @Override
    public void swapDown() {
        int newPosition = emoHeight * arrayY;
        int pixelRate = pixelMovement;
        while (screenPositionY + pixelRate > newPosition) {
            pixelRate /= DIVISOR;
        }
        screenPositionY += pixelRate;
        if (screenPositionY >= newPosition) swapDown = false;
    }

    @Override
    public void setSwapRight(boolean swapRight) {
        this.swapRight = swapRight;
    }

    @Override
    public boolean swapRightActivated() {
        return swapRight;
    }

    @Override
    public void swapRight() {
        int newPosition = emoWidth * arrayX;
        int pixelRate = pixelMovement;
        while (screenPositionX + pixelRate > newPosition) {
            pixelRate /= DIVISOR;
        }
        screenPositionX += pixelRate;
        if (screenPositionX >= newPosition) swapRight = false;
    }

    @Override
    public void setSwapLeft(boolean swapLeft) {
        this.swapLeft = swapLeft;
    }

    @Override
    public boolean swapLeftActivated() {
        return swapLeft;
    }

    @Override
    public void swapLeft() {
        int newPosition = emoWidth * arrayX;
        int pixelRate = pixelMovement;
        while (screenPositionX - pixelRate < newPosition) {
            pixelRate /= DIVISOR;
        }
        screenPositionX -= pixelRate;
        if (screenPositionX <= newPosition) swapLeft = false;
    }

    @Override
    public int getArrayX() {
        return arrayX;
    }

    @Override
    public void setArrayX(int arrayX) {
        this.arrayX = arrayX;
    }

    @Override
    public int getArrayY() {
        return arrayY;
    }

    @Override
    public void setArrayY(int arrayY) {
        this.arrayY = arrayY;
    }

    @Override
    public int getScreenPositionX() {
        return screenPositionX;
    }

    @Override
    public int getScreenPositionY() {
        return screenPositionY;
    }

    @Override
    public void setScreenPositionY(int screenPositionY) {
        this.screenPositionY = screenPositionY;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String getType() {
        return emoticonType;
    }
}
