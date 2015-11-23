package com.markchanner.mscprojectprototype03;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public interface Board {

    void reset();

    void updateSwaps();

    void updateDrops();

    void processSelections(GameView view, Selection selections);

    Emoticon[][] getEmoticons();

}