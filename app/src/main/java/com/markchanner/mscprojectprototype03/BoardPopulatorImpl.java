package com.markchanner.mscprojectprototype03;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Implementation of the BoardPopulator interface that populates a Board with Emoticons
 * at random. However, as this class is used for a matching game where the objective is
 * to match 3 consecutive Emoticons, it ensures that 3 consecutive pieces would not
 * be formed at the outset.
 *
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class BoardPopulatorImpl implements BoardPopulator {

    public static final int X_MAX = BoardImpl.X_MAX;
    public static final int Y_MAX = BoardImpl.Y_MAX;
    public static final int ROW_START = 0;
    public static final int COLUMN_TOP = 0;

    private Bitmap angryBitmap;
    private Bitmap delightedBitmap;
    private Bitmap embarrassedBitmap;
    private Bitmap surprisedBitmap;
    private Bitmap upsetBitmap;
    private Bitmap emptyBitmap;

    /**
     * Populates the given Board object with emoticons that are allocated at random. If
     * placing the emoticon would result in a board that has 3 consecutive emoticons at
     * the start of the game, another emoticon is chosen until one that does not form a match is
     * found { @inheritDocs }
     */
    @Override
    public void populateBoard(Context context, Board board, int emoticonWidth, int emoticonHeight) {
        createBitmaps(context, emoticonWidth, emoticonHeight);
        Emoticon[][] emoticons = board.getEmoticons();
        Emoticon newEmoticon;
        for (int x = ROW_START; x < X_MAX; x++) {
            int dropGap = Y_MAX * 2;
            for (int y = COLUMN_TOP; y < Y_MAX; y++) {
                do {

                    newEmoticon = generateEmoticon(x, y, emoticonWidth, emoticonHeight, ((y - Y_MAX) - dropGap));

                } while ((y >= 2 &&
                        (newEmoticon.getEmoticonType().equals(emoticons[x][y - 1].getEmoticonType()) &&
                                newEmoticon.getEmoticonType().equals(emoticons[x][y - 2].getEmoticonType()))) ||
                        (x >= 2 &&
                                (newEmoticon.getEmoticonType().equals(emoticons[x - 1][y].getEmoticonType()) &&
                                        newEmoticon.getEmoticonType().equals(emoticons[x - 2][y].getEmoticonType()))));

                dropGap--;
                emoticons[x][y] = newEmoticon;
            }
        }
    }

    /**
     * Returns one of five emoticons that are chosen at random
     *
     * @return a subclass of AbstractEmoticon (AbstractEmoticon implements Emoticon interface)
     */
    @Override
    public Emoticon generateEmoticon(int x, int y, int emoticonWidth, int emoticonHeight, int offScreenStartPositionY) {
        Emoticon emoticon = null;
        Random random = new Random();
        int value = random.nextInt(5);
        switch (value) {
            case 0:
                emoticon = new AngryEmoticon(x, y, emoticonWidth, emoticonHeight, angryBitmap, offScreenStartPositionY);
                break;
            case 1:
                emoticon = new DelightedEmoticon(x, y, emoticonWidth, emoticonHeight, delightedBitmap, offScreenStartPositionY);
                break;
            case 2:
                emoticon = new EmbarrassedEmoticon(x, y, emoticonWidth, emoticonHeight, embarrassedBitmap, offScreenStartPositionY);
                break;
            case 3:
                emoticon = new SurprisedEmoticon(x, y, emoticonWidth, emoticonHeight, surprisedBitmap, offScreenStartPositionY);
                break;
            case 4:
                emoticon = new UpsetEmoticon(x, y, emoticonWidth, emoticonHeight, upsetBitmap, offScreenStartPositionY);
                break;
            default:
                break;
        }
        return emoticon;
    }

    @Override
    public void createBitmaps(Context context, int emoticonWidth, int emoticonHeight) {

        Bitmap temp;
        // Retrieves graphics from drawable, scales down, then assigns to Bitmap object
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_tile);
        emptyBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);
        emptyBitmap.eraseColor(android.graphics.Color.TRANSPARENT);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.angry);
        angryBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.delighted);
        delightedBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.embarrassed);
        embarrassedBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.surprised);
        surprisedBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);

        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.upset);
        upsetBitmap = Bitmap.createScaledBitmap(temp, emoticonWidth, emoticonHeight, false);
    }

    @Override
    public Emoticon getEmptyEmoticon(int x, int y, int emoticonWidth, int emoticonHeight) {
        return new EmptyEmoticon(x, y, emoticonWidth, emoticonHeight, emptyBitmap);
    }
}