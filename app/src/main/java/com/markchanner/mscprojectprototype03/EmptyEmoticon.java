package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class EmptyEmoticon extends AbstractEmoticon {

    public static final String EMOTION_TYPE = "EMPTY";

    public EmptyEmoticon(int x, int y, int emoWidth, int emoHeight, Bitmap emptyBitmap) {
        super(x, y, emoWidth, emoHeight, emptyBitmap, EMOTION_TYPE, y);
        super.lowering = false;
    }
}



