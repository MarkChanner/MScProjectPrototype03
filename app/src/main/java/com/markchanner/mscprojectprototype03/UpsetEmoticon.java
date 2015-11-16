package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class UpsetEmoticon extends AbstractEmoticon {

    public static final String EMOTION_TYPE = "UPSET";

    public UpsetEmoticon(int x, int y, int emoWidth, int emoHeight, Bitmap upsetBitmap, int offScreenStartPositionY) {
        super(x, y, emoWidth, emoHeight, upsetBitmap, EMOTION_TYPE, offScreenStartPositionY);
    }
}



