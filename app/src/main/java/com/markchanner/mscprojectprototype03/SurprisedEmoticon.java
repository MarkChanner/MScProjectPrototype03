package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class SurprisedEmoticon extends AbstractEmoticon {

    public static final String EMOTION_TYPE = "SURPRISED";

    public SurprisedEmoticon(int x, int y, int emoWidth, int emoHeight, Bitmap surprisedBitmap, int offScreenStartPositionY) {
        super(x, y, emoWidth, emoHeight, surprisedBitmap, EMOTION_TYPE, offScreenStartPositionY);
    }
}



