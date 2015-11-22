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
    private int viewPositionX;
    private int viewPositionY;
    private int pixelMovement;

    volatile boolean lowering;
    volatile boolean swappingUp;
    volatile boolean swappingDown;
    volatile boolean swappingRight;
    volatile boolean swappingLeft;
    volatile boolean isPartOfMatch;

    public AbstractEmoticon(int arrayX, int arrayY, int emoWidth, int emoHeight, Bitmap bitmap, String emoticonType, int offScreenStartPositionY) {
        this.arrayX = arrayX;
        this.arrayY = arrayY;
        this.emoWidth = emoWidth;
        this.emoHeight = emoHeight;
        this.bitmap = bitmap;
        this.emoticonType = emoticonType;
        pixelMovement = 16;
        viewPositionX = (arrayX * emoWidth);
        viewPositionY = (offScreenStartPositionY * emoHeight);
        lowering = true;
    }

    @Override
    public boolean isSwapping() {
        if (swappingUp) {
            return true;
        } else if (swappingDown) {
            return true;
        } else if (swappingLeft) {
            return true;
        } else if (swappingRight) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateSwapping() {
        if (swappingUp) {
            swapUp();
        } else if (swappingDown) {
            swapDown();
        } else if (swappingRight) {
            swapRight();
        } else if (swappingLeft) {
            swapLeft();
        }
    }

    @Override
    public boolean isDropping() {
        return lowering;
    }

    @Override
    public void updateDropping() {
        if (lowering) {
            dropEmoticon();
        }
    }

    @Override
    public void setIsPartOfMatch(boolean bool) {
        isPartOfMatch = bool;
    }

    @Override
    public boolean isPartOfMatch() {
        return isPartOfMatch;
    }

    @Override
    public void setDropping(boolean bool) {
        lowering = bool;
    }

    @Override
    public void dropEmoticon() {
        int newPosition = (arrayY * emoHeight);
        int pixelRate = pixelMovement;
        while (viewPositionY + pixelRate > newPosition) {
            pixelRate /= DIVISOR;
        }
        viewPositionY += pixelRate;
        if (viewPositionY >= newPosition) {
            lowering = false;
        }
    }

    @Override
    public void setSwappingUp(boolean bool) {
        swappingUp = bool;
    }

    @Override
    public void swapUp() {
        int newPosition = emoHeight * arrayY;
        int pixelRate = pixelMovement;
        while (viewPositionY - pixelRate < newPosition) {
            pixelRate /= DIVISOR;
        }
        viewPositionY -= pixelRate;
        if (viewPositionY <= newPosition) {
            swappingUp = false;
        }
    }

    @Override
    public void setSwappingDown(boolean bool) {
        swappingDown = bool;
    }

    @Override
    public void swapDown() {
        int newPosition = emoHeight * arrayY;
        int pixelRate = pixelMovement;
        while (viewPositionY + pixelRate > newPosition) {
            pixelRate /= DIVISOR;
        }
        viewPositionY += pixelRate;
        if (viewPositionY >= newPosition) {
            swappingDown = false;
        }
    }

    @Override
    public void setSwappingRight(boolean bool) {
        swappingRight = bool;
    }

    @Override
    public void swapRight() {
        int newPosition = emoWidth * arrayX;
        int pixelRate = pixelMovement;
        while (viewPositionX + pixelRate > newPosition) {
            pixelRate /= DIVISOR;
        }
        viewPositionX += pixelRate;
        if (viewPositionX >= newPosition) {
            swappingRight = false;
        }
    }

    @Override
    public void setSwappingLeft(boolean bool) {
        swappingLeft = bool;
    }

    @Override
    public void swapLeft() {
        int newPosition = emoWidth * arrayX;
        int pixelRate = pixelMovement;
        while (viewPositionX - pixelRate < newPosition) {
            pixelRate /= DIVISOR;
        }
        viewPositionX -= pixelRate;
        if (viewPositionX <= newPosition) {
            swappingLeft = false;
        }
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
    public int getViewPositionX() {
        return viewPositionX;
    }

    @Override
    public int getViewPositionY() {
        return viewPositionY;
    }

    @Override
    public void setViewPositionY(int viewPositionY) {
        this.viewPositionY = viewPositionY;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String getEmoticonType() {
        return emoticonType;
    }

    public void setPixelMovement(int pixelMovement) {
        this.pixelMovement = pixelMovement;
    }
}
