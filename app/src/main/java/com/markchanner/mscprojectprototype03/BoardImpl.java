package com.markchanner.mscprojectprototype03;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class BoardImpl implements Board {

    private static final String TAG = "BoardImpl";

    public static final int X = 0;
    public static final int Y = 1;
    public static final int X_MAX = 8;
    public static final int Y_MAX = 7;
    public static final int ROW_START = 0;
    public static final int COLUMN_TOP = 0;
    public static final int COLUMN_BOTTOM = (Y_MAX - 1);
    public static final String EMPTY = "EMPTY";
    public static final String INVALID_MOVE = "INVALID_MOVE";
    public static final int ONE_SECOND = 1000;

    private volatile boolean animatingSwap = false;
    private volatile boolean animatingDrop = false;
    private final Object swapObj = new Object();
    private final Object dropObj = new Object();

    private SoundManager soundManager;
    private int emoticonWidth;
    private int emoticonHeight;
    private Emoticon[][] emoticons;
    private BoardPopulator populator;

    public BoardImpl(Context context, int emoticonWidth, int emoticonHeight) {
        soundManager = new SoundManager();
        soundManager.loadSound(context);
        this.emoticonWidth = emoticonWidth;
        this.emoticonHeight = emoticonHeight;
        emoticons = new AbstractEmoticon[X_MAX][Y_MAX];
        populator = new BoardPopulatorImpl();
        populator.populateBoard(context, this, emoticonWidth, emoticonHeight);
    }

    public void updateSwaps() {
        Log.d(TAG, "FROM RUN(): in updateSwaps()");
        boolean emoticonsSwapping = false;
        for (int y = COLUMN_BOTTOM; y >= COLUMN_TOP; y--) {
            for (int x = ROW_START; x < X_MAX; x++) {
                if (emoticons[x][y].isSwapping()) {
                    emoticonsSwapping = true;
                    emoticons[x][y].updateSwapping();
                }
            }
        }
        if (!emoticonsSwapping) {
            synchronized (swapObj) {
                if (animatingSwap) {
                    animatingSwap = false;
                    swapObj.notifyAll();
                }
            }
        }
    }

    public void updateDrops() {
        Log.d(TAG, "FROM RUN(): in updateDrops");
        boolean emoticonsLowering = false;
        for (int y = COLUMN_BOTTOM; y >= COLUMN_TOP; y--) {
            for (int x = ROW_START; x < X_MAX; x++) {
                if (emoticons[x][y].isLowering()) {
                    emoticonsLowering = true;
                    emoticons[x][y].updateLowering();
                }
            }
        }
        if (!emoticonsLowering) {
            synchronized (dropObj) {
                if (animatingDrop) {
                    animatingDrop = false;
                    dropObj.notifyAll();
                }
            }
        }
    }

    @Override
    public void processSelections(GameView view, Selection selections) {
        Log.d(TAG, "in processSelections(GameView, Selection)");
        int[] sel1 = selections.getSelection01();
        int[] sel2 = selections.getSelection02();
        swapSelectedEmoticons(sel1, sel2);

        ArrayList<LinkedList<Emoticon>> matchingX = findMatchingColumns();
        ArrayList<LinkedList<Emoticon>> matchingY = findMatchingRows();

        if (matchesFound(matchingX, matchingY)) {
            modifyBoard(view, matchingX, matchingY);
        } else {
            soundManager.playSound(INVALID_MOVE);
            swapBack(sel1, sel2);
        }
        selections.resetUserSelections();
    }

    public void swapSelectedEmoticons(int[] sel1, int[] sel2) {
        Log.d(TAG, "in swapSelectedEmoticons(int[] int[])");
        int emo01X = emoticons[sel1[X]][sel1[Y]].getArrayX();
        int emo01Y = emoticons[sel1[X]][sel1[Y]].getArrayY();
        Emoticon newEmoticon2 = emoticons[sel1[X]][sel1[Y]];

        int emo02X = emoticons[sel2[X]][sel2[Y]].getArrayX();
        int emo02Y = emoticons[sel2[X]][sel2[Y]].getArrayY();

        emoticons[sel1[X]][sel1[Y]] = emoticons[sel2[X]][sel2[Y]];
        emoticons[sel1[X]][sel1[Y]].setArrayX(emo01X);
        emoticons[sel1[X]][sel1[Y]].setArrayY(emo01Y);

        emoticons[sel2[X]][sel2[Y]] = newEmoticon2;
        emoticons[sel2[X]][sel2[Y]].setArrayX(emo02X);
        emoticons[sel2[X]][sel2[Y]].setArrayY(emo02Y);

        // values now swapped, now need to move screen position to reflect
        Emoticon e1 = emoticons[sel1[X]][sel1[Y]];
        Emoticon e2 = emoticons[sel2[X]][sel2[Y]];

        if (e1.getArrayX() == e2.getArrayX()) {
            if (e1.getArrayY() < e2.getArrayY()) {
                e1.setSwappingUp(true);
                e2.setSwappingDown(true);
            } else {
                e2.setSwappingUp(true);
                e1.setSwappingDown(true);
            }
        } else if (e1.getArrayY() == e2.getArrayY()) {
            if (e1.getArrayX() < e2.getArrayX()) {
                e1.setSwappingLeft(true);
                e2.setSwappingRight(true);
            } else {
                e2.setSwappingLeft(true);
                e1.setSwappingRight(true);
            }
        }
        lockSwap();
    }

    private void lockSwap() {
        Log.d(TAG, "in lockSwap()");
        synchronized (swapObj) {
            animatingSwap = true;
            while (animatingSwap) {
                try {
                    swapObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void swapBack(int[] sel1, int[] sel2) {
        Log.d(TAG, "in swapBack(int[] int[])");
        swapSelectedEmoticons(sel1, sel2);
    }

    public ArrayList<LinkedList<Emoticon>> findMatchingColumns() {
        Log.d(TAG, "in findMatchingColumns()");
        LinkedList<Emoticon> consecutiveEmoticons = new LinkedList<>();
        ArrayList<LinkedList<Emoticon>> bigList = new ArrayList<>();
        Emoticon emoticon;
        for (int x = ROW_START; x < X_MAX; x++) {
            consecutiveEmoticons.add(emoticons[x][COLUMN_BOTTOM]);

            for (int y = (COLUMN_BOTTOM - 1); y >= COLUMN_TOP; y--) {
                emoticon = emoticons[x][y];
                if (!emoticon.getEmoticonType().equals(consecutiveEmoticons.getLast().getEmoticonType())) {
                    examineList(consecutiveEmoticons, bigList);
                    consecutiveEmoticons = new LinkedList<>();
                }
                consecutiveEmoticons.add(emoticon);
                if (y == COLUMN_TOP) {
                    examineList(consecutiveEmoticons, bigList);
                    consecutiveEmoticons = new LinkedList<>();
                }
            }
        }
        return bigList;
    }

    public ArrayList<LinkedList<Emoticon>> findMatchingRows() {
        Log.d(TAG, "in findMatchingRows()");
        LinkedList<Emoticon> consecutiveEmoticons = new LinkedList<>();
        ArrayList<LinkedList<Emoticon>> bigList = new ArrayList<>();
        Emoticon emoticon;
        for (int y = COLUMN_BOTTOM; y >= COLUMN_TOP; y--) {
            consecutiveEmoticons.add(emoticons[ROW_START][y]);

            for (int x = (ROW_START + 1); x < X_MAX; x++) {
                emoticon = emoticons[x][y];
                if (!(emoticon.getEmoticonType().equals(consecutiveEmoticons.getLast().getEmoticonType()))) {
                    examineList(consecutiveEmoticons, bigList);
                    consecutiveEmoticons = new LinkedList<>();
                }
                consecutiveEmoticons.add(emoticon);
                if (x == (X_MAX - 1)) {
                    examineList(consecutiveEmoticons, bigList);
                    consecutiveEmoticons = new LinkedList<>();
                }
            }
        }
        return bigList;
    }

    private void examineList(LinkedList<Emoticon> consecutiveEmotions, ArrayList<LinkedList<Emoticon>> bigList) {
        if ((consecutiveEmotions.size() >= 3) && (allSameType(consecutiveEmotions))) {
            bigList.add(consecutiveEmotions);
        }
    }

    private boolean allSameType(LinkedList<Emoticon> consecutiveEmoticons) {
        String previousEmoticon = consecutiveEmoticons.getFirst().getEmoticonType();
        String nextEmoticon;
        for (int i = 1; i < consecutiveEmoticons.size(); i++) {
            nextEmoticon = consecutiveEmoticons.get(i).getEmoticonType();
            if (nextEmoticon.equals(EMPTY) || (!(nextEmoticon.equals(previousEmoticon)))) {
                return false;
            } else {
                previousEmoticon = nextEmoticon;
            }
        }
        return true;
    }

    private boolean matchesFound(ArrayList<LinkedList<Emoticon>> matchingX, ArrayList<LinkedList<Emoticon>> matchingY) {
        Log.d(TAG, "in matchesFound method");
        return (!(matchingX.isEmpty() && matchingY.isEmpty()));
    }

    public void modifyBoard(GameView view, ArrayList<LinkedList<Emoticon>> matchingX, ArrayList<LinkedList<Emoticon>> matchingY) {
        Log.d(TAG, "in modifyBoard method");
        do {
            Log.d(TAG, "entered do/while loop in modifyBoard method");
            highlightMatches(matchingX);
            highlightMatches(matchingY);
            playAudio(view, matchingX, matchingY);
            removeFromBoard(matchingX);
            removeFromBoard(matchingY);
            lowerEmoticons();
            matchingX = findMatchingColumns();
            matchingY = findMatchingRows();
        } while (matchesFound(matchingX, matchingY));
    }

    public void playAudio(GameView view, ArrayList<LinkedList<Emoticon>> matchingX, ArrayList<LinkedList<Emoticon>> matchingY) {
        Log.d(TAG, "in PlayAudio method");
        if (!(matchingX.isEmpty())) {
            String matchingTypeX = matchingX.get(0).getFirst().getEmoticonType();
            soundManager.playSound(matchingTypeX);
        } else if (!(matchingY.isEmpty())) {
            String matchingTypeY = matchingY.get(0).getFirst().getEmoticonType();
            soundManager.playSound(matchingTypeY);
        }
        view.control(ONE_SECOND);
    }

    private void highlightMatches(ArrayList<LinkedList<Emoticon>> matches) {
        for (List<Emoticon> removeList : matches) {
            for (Emoticon e : removeList) {
                e.setIsPartOfMatch(true);
            }
        }
    }

    public void removeFromBoard(ArrayList<LinkedList<Emoticon>> matches) {
        for (List<Emoticon> removeList : matches) {
            for (Emoticon e : removeList) {
                int x = e.getArrayX();
                int y = e.getArrayY();
                if (!(emoticons[x][y].getEmoticonType().equals(EMPTY))) {
                    emoticons[x][y] = populator.getEmptyEmoticon(x, y, emoticonWidth, emoticonHeight);
                }
            }
        }
    }

    public void lowerEmoticons() {
        Log.d(TAG, "in lowerEmoticons()");
        int offScreenStartPosition;
        int runnerY;

        for (int x = ROW_START; x < X_MAX; x++) {

            offScreenStartPosition = -1;

            for (int y = COLUMN_BOTTOM; y >= COLUMN_TOP; y--) {

                if (emoticons[x][y].getEmoticonType().equals(EMPTY)) {
                    runnerY = y;
                    while ((runnerY >= COLUMN_TOP) && (emoticons[x][runnerY].getEmoticonType().equals(EMPTY))) {
                        runnerY--;
                    }
                    if (runnerY >= COLUMN_TOP) {
                        int tempY = emoticons[x][y].getArrayY();
                        emoticons[x][y] = emoticons[x][runnerY];
                        emoticons[x][y].setArrayY(tempY);
                        emoticons[x][y].setLowering(true);
                        emoticons[x][runnerY] = populator.getEmptyEmoticon(x, runnerY, emoticonWidth, emoticonHeight);
                    } else {
                        emoticons[x][y] = populator.generateEmoticon(x, y, emoticonWidth, emoticonHeight, offScreenStartPosition);
                        offScreenStartPosition--;
                    }
                }
            }
        }
        lockDrop();
    }

    private void lockDrop() {
        Log.d(TAG, "in lockDrop()");
        synchronized (dropObj) {
            animatingDrop = true;
            while (animatingDrop) {
                try {
                    dropObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Emoticon[][] getEmoticons() {
        if (emoticons == null) {
            throw new NullPointerException();
        } else {
            return emoticons;
        }
    }
}
