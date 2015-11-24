package com.markchanner.mscprojectprototype03;

import android.graphics.Bitmap;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class MockEmoticon extends AbstractEmoticon {

    public MockEmoticon(int x, int y, int emoWidth, int emoHeight, Bitmap mockBitmap, String emoticonType) {
        super(x, y, emoWidth, emoHeight, mockBitmap, emoticonType, y);
        super.dropping = false;
    }
}



