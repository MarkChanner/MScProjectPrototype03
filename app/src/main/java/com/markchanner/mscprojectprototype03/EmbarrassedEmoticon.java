package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class EmbarrassedEmoticon extends AbstractEmoticon {

    public static final String EMOTION_TYPE = "EMBARRASSED";

    public EmbarrassedEmoticon(int x, int y, int emoWidth, int emoHeight, Bitmap embarrassedBitmap, int offScreenStartPositionY) {
        super(x, y, emoWidth, emoHeight, embarrassedBitmap, EMOTION_TYPE, offScreenStartPositionY);
    }
}



