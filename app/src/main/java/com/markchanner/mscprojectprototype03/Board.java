package com.markchanner.mscprojectprototype03;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public interface Board {

    void updateEmoticons();

    void processSelections(GameView view, Selection selections);

    Emoticon[][] getEmoticons();

}
