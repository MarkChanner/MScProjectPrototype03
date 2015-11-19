package com.markchanner.mscprojectprototype03;

public final class Monitor {

    private final Object obj = new Object();
    private boolean locked = true;

    public void doWait() {
        synchronized (obj) {
            while (locked) {
                try {
                    obj.wait();
                } catch (InterruptedException e) {

                }
            }
            locked = true;
        }
    }

    public void doNotify() {
        synchronized (obj) {
            locked = false;
            obj.notify();
        }
    }
}
