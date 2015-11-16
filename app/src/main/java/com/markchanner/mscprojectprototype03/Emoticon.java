package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public interface Emoticon {

    void update();

    void setIsPartOfMatch(boolean bool);

    boolean isPartOfMatch();

    void setLowerEmoticon(boolean bool);

    boolean lowerEmoticonActivated();

    void lowerEmoticon();

    void setSwapUp(boolean swapUp);

    boolean swapUpActivated();

    void swapUp();

    void setSwapDown(boolean swapDown);

    boolean swapDownActivated();

    void swapDown();

    void setSwapRight(boolean swapRight);

    boolean swapRightActivated();

    void swapRight();

    void setSwapLeft(boolean swapLeft);

    boolean swapLeftActivated();

    void swapLeft();

    int getArrayX();

    void setArrayX(int x);

    int getArrayY();

    void setArrayY(int y);

    int getScreenPositionX();

    int getScreenPositionY();

    void setScreenPositionY(int screenPositionY);

    Bitmap getBitmap();

    String getType();

}

