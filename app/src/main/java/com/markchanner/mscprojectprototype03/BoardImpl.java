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
    private final Object swapLock = new Object();
    private final Object dropLock = new Object();

    private Context context;
    private SoundManager soundManager;
    private int emoticonWidth;
    private int emoticonHeight;
    private Emoticon[][] emoticons;
    private BoardPopulator populator;

    public BoardImpl(Context context, int emoticonWidth, int emoticonHeight) {
        this.context = context;
        this.emoticonWidth = emoticonWidth;
        this.emoticonHeight = emoticonHeight;
        this.soundManager = new SoundManager();
        this.soundManager.loadSound(context);
        this.emoticons = new AbstractEmoticon[X_MAX][Y_MAX];
        this.populator = new BoardPopulatorMock01();
        this.populator.populateBoard(context, this, emoticonWidth, emoticonHeight);
    }

    @Override
    public void reset() {
        populator.populateBoard(context, this, emoticonWidth, emoticonHeight);
    }

    @Override
    public void updateSwaps() {
        //Log.d(TAG, "FROM RUN(): in updateSwaps()");
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
            synchronized (swapLock) {
                if (animatingSwap) {
                    animatingSwap = false;
                    swapLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void updateDrops() {
        //Log.d(TAG, "FROM RUN(): in updateDrops");
        boolean emoticonsDropping = false;
        for (int y = COLUMN_BOTTOM; y >= COLUMN_TOP; y--) {
            for (int x = ROW_START; x < X_MAX; x++) {
                if (emoticons[x][y].isDropping()) {
                    emoticonsDropping = true;
                    emoticons[x][y].updateDropping();
                }
            }
        }
        if (!emoticonsDropping) {
            synchronized (dropLock) {
                if (animatingDrop) {
                    animatingDrop = false;
                    dropLock.notifyAll();
                }
            }
        }
    }

    @Override
    public void processSelections(GameView view, Selection selections) {
        //Log.d(TAG, "in processSelections(GameView, Selection)");
        int[] sel1 = selections.getSelection01();
        int[] sel2 = selections.getSelection02();
        swapSelectedEmoticons(sel1, sel2);

        ArrayList<LinkedList<Emoticon>> matchingX = findVerticalMatches();
        ArrayList<LinkedList<Emoticon>> matchingY = findHorizontalMatches();

        if (matchesFound(matchingX, matchingY)) {
            modifyBoard(view, matchingX, matchingY);
        } else {
            soundManager.playSound(INVALID_MOVE);
            swapBack(sel1, sel2);
        }
        selections.resetUserSelections();
    }

    public void swapSelectedEmoticons(int[] sel1, int[] sel2) {
        //Log.d(TAG, "in swapSelectedEmoticons(int[] int[])");
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
        waitForSwapAnimationToFinish();
        //Log.d(TAG, "Returned from waitForSwapAnimationToFinish()");
    }

    private void waitForSwapAnimationToFinish() {
        //Log.d(TAG, "in waitForSwapAnimationToFinish()");
        synchronized (swapLock) {
            animatingSwap = true;
            while (animatingSwap) {
                try {
                    swapLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void swapBack(int[] sel1, int[] sel2) {
        Log.d(TAG, "in swapBack(int[] int[])");
        swapSelectedEmoticons(sel1, sel2);
    }

    private ArrayList<LinkedList<Emoticon>> findVerticalMatches() {
        //Log.d(TAG, "in findVerticalMatches()");
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

    private ArrayList<LinkedList<Emoticon>> findHorizontalMatches() {
        //Log.d(TAG, "in findHorizontalMatches()");
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
        // Log.d(TAG, "in matchesFound method");
        return (!(matchingX.isEmpty() && matchingY.isEmpty()));
    }

    private void modifyBoard(GameView view, ArrayList<LinkedList<Emoticon>> matchingX, ArrayList<LinkedList<Emoticon>> matchingY) {
        do {
            Log.d(TAG, "entered do/while loop in modifyBoard method");
            highlightMatches(matchingX);
            highlightMatches(matchingY);
            playAudio(view, matchingX, matchingY);
            removeFromBoard(matchingX);
            removeFromBoard(matchingY);
            dropEmoticons();
            matchingX = findVerticalMatches();
            matchingY = findHorizontalMatches();
        } while (matchesFound(matchingX, matchingY));

        if (!matchAvailable()) {
            Log.d(TAG, "NO MATCHES AVAILABLE - END GAME CONDITION ENTERED");
            setToDrop();
            dropEmoticons();
            view.setGameEnded(true);
        }
    }

    private void setToDrop() {
        for (int y = COLUMN_BOTTOM; y >= COLUMN_TOP; y--) {
            for (int x = ROW_START; x < X_MAX; x++) {
                emoticons[x][y].setArrayY(Y_MAX);
                emoticons[x][y].setPixelMovement(32);
                emoticons[x][y].setDropping(true);
            }
        }
    }

    private void playAudio(GameView view, ArrayList<LinkedList<Emoticon>> matchingX, ArrayList<LinkedList<Emoticon>> matchingY) {
        //Log.d(TAG, "in PlayAudio method");
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

    private void removeFromBoard(ArrayList<LinkedList<Emoticon>> matches) {
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

    private void dropEmoticons() {
        //Log.d(TAG, "in dropEmoticons()");
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
                        emoticons[x][y].setDropping(true);
                        emoticons[x][runnerY] = populator.getEmptyEmoticon(x, runnerY, emoticonWidth, emoticonHeight);
                    } else {
                        emoticons[x][y] = populator.generateEmoticon(x, y, emoticonWidth, emoticonHeight, offScreenStartPosition);
                        offScreenStartPosition--;
                    }
                }
            }
        }
        waitForDropAnimationToComplete();
        //Log.d(TAG, "Returned from waitForDropAnimationToComplete()");
    }

    private void waitForDropAnimationToComplete() {
        //Log.d(TAG, "in waitForDropAnimationToComplete()");
        synchronized (dropLock) {
            animatingDrop = true;
            while (animatingDrop) {
                try {
                    dropLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean matchAvailable() {
        return (verticalMatchAvailable() || horizontalMatchAvailable());
    }

    private boolean verticalMatchAvailable() {
        Log.d(TAG, "in verticalMatchAvailable()");
        String type;
        for (int x = ROW_START; x < X_MAX; x++) {
            for (int y = COLUMN_BOTTOM; y >= COLUMN_TOP; y--) {

                type = emoticons[x][y].getEmoticonType();

                if ((y - 1 >= COLUMN_TOP &&
                        emoticons[x][y - 1].getEmoticonType().equals(type) &&
                        verticalA(type, x, y)) ||
                        (y - 2 >= COLUMN_TOP &&
                                emoticons[x][y - 2].getEmoticonType().equals(type) &&
                                verticalB(type, x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verticalA(String type, int x, int y) {
        Log.d(TAG, "in verticalA");
        return ((y - 2 >= COLUMN_TOP && verticalAboveA(type, x, y)) ||
                (y + 1 <= COLUMN_BOTTOM && verticalBelowA(type, x, y)));
    }

    /**
     * The condition that (y - 2) must be higher than
     * COLUMN_TOP was  checked in the calling method
     */
    private boolean verticalAboveA(String type, int x, int y) {
        Log.d(TAG, "in verticalAboveA");
        return ((y - 3 >= COLUMN_TOP && emoticons[x][y - 3].getEmoticonType().equals(type)) ||
                (x - 1 >= ROW_START && emoticons[x - 1][y - 2].getEmoticonType().equals(type)) ||
                (x + 1 < X_MAX && emoticons[x + 1][y - 2].getEmoticonType().equals(type)));
    }

    /**
     * The condition that (y + 1) must be less than
     * COLUMN_BOTTOM was checked in the calling method
     */
    private boolean verticalBelowA(String type, int x, int y) {
        Log.d(TAG, "in verticalBelowA");
        return ((y + 2 <= COLUMN_BOTTOM && emoticons[x][y + 2].getEmoticonType().equals(type)) ||
                (x - 1 >= ROW_START && emoticons[x - 1][y + 1].getEmoticonType().equals(type)) ||
                (x + 1 < X_MAX && emoticons[x + 1][y + 1].getEmoticonType().equals(type)));
    }

    private boolean verticalB(String type, int x, int y) {
        Log.d(TAG, "in verticalB");
        return ((x - 1 >= ROW_START && emoticons[x - 1][y - 1].getEmoticonType().equals(type)) ||
                (x + 1 < X_MAX && emoticons[x + 1][y - 1].getEmoticonType().equals(type)));
    }

    private boolean horizontalMatchAvailable() {
        Log.d(TAG, "in horizontalMatchAvailable()");
        String type;
        for (int y = COLUMN_BOTTOM; y >= COLUMN_TOP; y--) {
            for (int x = ROW_START; x < X_MAX; x++) {

                type = emoticons[x][y].getEmoticonType();

                if ((x + 1 < X_MAX &&
                        emoticons[x + 1][y].getEmoticonType().equals(type) &&
                        horizontalA(type, x, y)) ||
                        (x + 2 < X_MAX && emoticons[x + 2][y].getEmoticonType().equals(type) &&
                                horizontalB(type, x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean horizontalA(String type, int x, int y) {
        Log.d(TAG, "in horizontalA()");
        return ((x + 2 < X_MAX && horizontalRightA(type, x, y)) ||
                (x - 1 >= ROW_START && horizontalLeftA(type, x, y)));
    }

    /**
     * The condition that (x + 2) must be above
     * below X_MAX was checked in the calling method
     */
    private boolean horizontalRightA(String type, int x, int y) {
        Log.d(TAG, "in horizontalRightA()");
        return ((x + 3 < X_MAX && emoticons[x + 3][y].getEmoticonType().equals(type)) ||
                (y - 1 >= COLUMN_TOP && emoticons[x + 2][y - 1].getEmoticonType().equals(type)) ||
                (y + 1 <= COLUMN_BOTTOM && emoticons[x + 2][y + 1].getEmoticonType().equals(type)));
    }

    /**
     * The condition that (x - 1) must be above equal to or
     * above  ROW_START was checked in the calling method
     */
    private boolean horizontalLeftA(String type, int x, int y) {
        Log.d(TAG, "in horizontalLeftA()");
        return ((x - 2 >= ROW_START && emoticons[x - 2][y].getEmoticonType().equals(type)) ||
                (y - 1 >= COLUMN_TOP && emoticons[x - 1][y - 1].getEmoticonType().equals(type)) ||
                (y + 1 <= COLUMN_BOTTOM && emoticons[x - 1][y + 1].getEmoticonType().equals(type)));
    }

    private boolean horizontalB(String type, int x, int y) {
        Log.d(TAG, "in horizontalB()");
        return ((y - 1 >= COLUMN_TOP && emoticons[x + 1][y - 1].getEmoticonType().equals(type)) ||
                (y + 1 <= COLUMN_BOTTOM && emoticons[x + 1][y + 1].getEmoticonType().equals(type)));
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
