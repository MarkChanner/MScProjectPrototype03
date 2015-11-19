package com.markchanner.mscprojectprototype03;

public final class Monitor {

    private final Object swapsObj = new Object();
    private volatile boolean swapsLocked = true;

    private final Object dropsObj = new Object();
    private volatile boolean dropsLocked = true;

    public void waitSwaps() {
        synchronized (swapsObj) {
            while (swapsLocked) {
                try {
                    swapsObj.wait();
                } catch (InterruptedException e) {

                }
            }
            swapsLocked = true;
        }
    }

    public void notifySwaps() {
        synchronized (swapsObj) {
            swapsLocked = false;
            swapsObj.notify();
        }
    }

    public void waitDrops() {
        synchronized (dropsObj) {
            while (dropsLocked) {
                try {
                    dropsObj.wait();
                } catch (InterruptedException e) {

                }
            }
            dropsLocked = true;
        }
    }

    public void notifyDrops() {
        synchronized (dropsObj) {
            dropsLocked = false;
            dropsObj.notify();
        }
    }
}
