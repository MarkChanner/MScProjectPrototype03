package com.markchanner.mscprojectprototype03;

import android.util.Log;

public final class Monitor {

    private static final String TAG = "Monitor";
    private final Object swapsObj = new Object();
    private volatile boolean swapsLocked = true;
    private final Object dropsObj = new Object();
    private volatile boolean dropsLocked = true;

    public void waitSwaps() {
        Log.d(TAG, "in waitSwaps()");
        synchronized (swapsObj) {
            while (swapsLocked) {
                try {
                    swapsObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            swapsLocked = true;
        }
    }

    public void notifySwaps() {
        Log.d(TAG, "in notifySwaps()");
        synchronized (swapsObj) {
            swapsLocked = false;
            swapsObj.notify();
        }
    }

    public void waitDrops() {
        Log.d(TAG, "in waitDrops()");
        synchronized (dropsObj) {
            while (dropsLocked) {
                try {
                    dropsObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dropsLocked = true;
        }
    }

    public void notifyDrops() {
        Log.d(TAG, "in notifyDrops()");
        synchronized (dropsObj) {
            dropsLocked = false;
            dropsObj.notify();
        }
    }
}
