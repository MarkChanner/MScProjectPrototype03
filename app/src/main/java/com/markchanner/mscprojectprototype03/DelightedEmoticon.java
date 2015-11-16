package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class DelightedEmoticon extends AbstractEmoticon {

    public static final String EMOTION_TYPE = "DELIGHTED";

    public DelightedEmoticon(int x, int y, int emoWidth, int emoHeight, Bitmap delightedBitmap, int offScreenStartPositionY) {
        super(x, y, emoWidth, emoHeight, delightedBitmap, EMOTION_TYPE, offScreenStartPositionY);
    }
}



