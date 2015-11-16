package com.markchanner.mscprojectprototype03;

import android.content.Context;

/**
 * Implementations of this interface are used to populateBoard the Board with emoticons.
 * For testing purposes, an implementation can populateBoard the board with a set layout,
 * rather than at random, which would make testing difficult.
 *
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public interface BoardPopulator {

    /**
     * Populates the given Board with emoticons
     */
    void populateBoard(Context context, Board board, int emoticonWidth, int emoticonHeight);

    Emoticon generateEmoticon(int x, int y, int emoWidth, int emoHeight, int offScreenStartPositionY);

    void createBitmaps(Context context, int emoWidth, int emoHeight);

    Emoticon getEmptyEmoticon(int x, int y, int emoticonWidth, int emoticonHeight);

}
