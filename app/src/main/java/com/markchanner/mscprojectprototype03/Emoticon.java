package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public interface Emoticon {

    boolean isSwapping();

    void updateSwapping();

    boolean isDropping();

    void updateDropping();

    void setIsPartOfMatch(boolean bool);

    boolean isPartOfMatch();

    void setDropping(boolean bool);

    void dropEmoticon();

    void setSwappingUp(boolean swapUp);

    void swapUp();

    void setSwappingDown(boolean swapDown);

    void swapDown();

    void setSwappingRight(boolean swapRight);

    void swapRight();

    void setSwappingLeft(boolean swapLeft);

    void swapLeft();

    int getArrayX();

    void setArrayX(int x);

    int getArrayY();

    void setArrayY(int y);

    int getScreenPositionX();

    int getScreenPositionY();

    void setScreenPositionY(int screenPositionY);

    Bitmap getBitmap();

    String getEmoticonType();

}

