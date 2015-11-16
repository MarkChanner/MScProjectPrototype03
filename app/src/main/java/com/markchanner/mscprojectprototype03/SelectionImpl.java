package com.markchanner.mscprojectprototype03;

public class SelectionImpl implements Selection {

    /**
     * @author Mark Channer for Birkbeck MSc Computer Science project
     */
    public static final int X = 0;
    public static final int Y = 1;
    private int[] selection01 = new int[2];
    private int[] selection02 = new int[2];
    private boolean selection01Made;

    public SelectionImpl() {
        resetUserSelections();
    }
    @Override
    public void resetUserSelections() {
        selection01[X] = -1;
        selection01[Y] = -1;
        selection01Made = false;

        selection02[X] = -1;
        selection02[Y] = -1;
    }

    @Override
    public boolean selection01Made() {
        return selection01Made;
    }

    @Override
    public int[] getSelection01() {
        return selection01;
    }

    @Override
    public void setSelection01(int x, int y) {
        selection01[X] = x;
        selection01[Y] = y;
        selection01Made = true;
    }

    @Override
    public int[] getSelection02() {
        return selection02;
    }

    @Override
    public void setSelection02(int x, int y) {
        selection02[X] = x;
        selection02[Y] = y;
    }

    @Override
    public boolean sameSelectionMadeTwice() {
        return (selection01[X] == selection02[X] && selection01[Y] == selection02[Y]);
    }

    @Override
    public boolean adjacentSelections() {
        if ((selection01[X] == selection02[X]) &&
                (selection01[Y] == (selection02[Y] + 1) || selection01[Y] == (selection02[Y] - 1))) {
            return true;
        } else if ((selection01[Y] == selection02[Y]) &&
                (selection01[X] == (selection02[X] + 1) || selection01[X] == (selection02[X] - 1))) {
            return true;
        }
        return false;
    }

    @Override
    public void setSelection02ToSelection01() {
        selection01[X] = selection02[X];
        selection01[Y] = selection02[Y];

        selection02[X] = -1;
        selection02[Y] = -1;
    }
}