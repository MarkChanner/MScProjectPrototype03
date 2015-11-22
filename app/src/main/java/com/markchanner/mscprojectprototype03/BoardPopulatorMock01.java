package com.markchanner.mscprojectprototype03;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Populates the board Mock Emoticons that are all given a unique value for the type. This
 * is so that no matches are considered to be available on the board to begin with, which
 * enables testing of the methods in BoardImpl which check if matches are still available
 * on the board. Specific Emoticons can also be constructed at specific board locations to
 * test that the pre-swap and post-swap Emoticon positions are correct.
 *
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class BoardPopulatorMock01 implements BoardPopulator {

    public static final int X_MAX = BoardImpl.X_MAX;
    public static final int Y_MAX = BoardImpl.Y_MAX;
    public static final int ROW_START = 0;
    public static final int COLUMN_TOP = 0;

    private Bitmap mockBitmap;
    private Bitmap emptyBitmap;
    private Bitmap angryBitmap;
    private Bitmap delightedBitmap;
    private Bitmap surprisedBitmap;
    private Bitmap upsetBitmap;

    private int mockTypeID = 0;

    @Override
    public void createBitmaps(Context context, int emoticonWidth, int emoticonHeight) {

        Bitmap temp;
        // Retrieves graphics from drawable, scales down, then assigns to Bitmap object
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.mock);
        mockBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_tile);
        emptyBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);
        emptyBitmap.eraseColor(android.graphics.Color.TRANSPARENT);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.angry);
        angryBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.delighted);
        delightedBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.surprised);
        surprisedBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.upset);
        upsetBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);
    }

    @Override
    public void populateBoard(Context context, Board board, int emoticonWidth, int emoticonHeight) {
        createBitmaps(context, emoticonWidth, emoticonHeight);
        Emoticon[][] emoticons = board.getEmoticons();
        Emoticon mockEmoticon;
        for (int x = ROW_START; x < X_MAX; x++) {
            for (int y = COLUMN_TOP; y < Y_MAX; y++) {

                mockEmoticon = generateEmoticon(x, y, emoticonWidth, emoticonHeight, y);
                emoticons[x][y] = mockEmoticon;
            }
        }

        constructEmoticonsAtSpecificLocations(emoticons, emoticonWidth, emoticonHeight);

    }

    @Override
    public Emoticon generateEmoticon(int x, int y, int emoticonWidth, int emoticonHeight, int offScreenStartPositionY) {
        mockTypeID++;
        return new MockEmoticon(x, y, emoticonWidth, emoticonHeight, mockBitmap, ("" + mockTypeID));
    }

    private void constructEmoticonsAtSpecificLocations(Emoticon[][] emoticons, int emoticonWidth, int emoticonHeight) {
        // Populates board so that a horizontal match of Delighted
        // Emoticons and a vertical match of Surprised Emoticons
        // can occur when Emoticons at (0,3) and (0,4) are selected
        emoticons[0][1] = new DelightedEmoticon(0, 1, emoticonWidth, emoticonHeight, delightedBitmap, 1);
        emoticons[0][2] = new DelightedEmoticon(0, 2, emoticonWidth, emoticonHeight, delightedBitmap, 2);
        emoticons[0][3] = new SurprisedEmoticon(0, 3, emoticonWidth, emoticonHeight, surprisedBitmap, 3);
        emoticons[0][4] = new DelightedEmoticon(0, 4, emoticonWidth, emoticonHeight, delightedBitmap, 4);
        emoticons[1][4] = new SurprisedEmoticon(1, 4, emoticonWidth, emoticonHeight, surprisedBitmap, 4);
        emoticons[2][4] = new SurprisedEmoticon(2, 4, emoticonWidth, emoticonHeight, surprisedBitmap, 4);

        // Populates board so that a horizontal match of Angry
        // Emoticons and a vertical match of Upset Emoticons
        // can occur when Emoticons at (3,3) and (3,4) are selected
        emoticons[3][3] = new UpsetEmoticon(3, 3, emoticonWidth, emoticonHeight, upsetBitmap, 3);
        emoticons[4][3] = new AngryEmoticon(4, 3, emoticonWidth, emoticonHeight, angryBitmap, 3);
        emoticons[5][3] = new UpsetEmoticon(5, 3, emoticonWidth, emoticonHeight, upsetBitmap, 3);
        emoticons[6][3] = new UpsetEmoticon(6, 3, emoticonWidth, emoticonHeight, upsetBitmap, 3);
        emoticons[4][4] = new UpsetEmoticon(4, 4, emoticonWidth, emoticonHeight, upsetBitmap, 4);
        emoticons[4][5] = new UpsetEmoticon(4, 5, emoticonWidth, emoticonHeight, upsetBitmap, 5);
    }

    @Override
    public Emoticon getEmptyEmoticon(int x, int y, int emoticonWidth, int emoticonHeight) {
        return new EmptyEmoticon(x, y, emoticonWidth, emoticonHeight, emptyBitmap);
    }
}