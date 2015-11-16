package com.markchanner.mscprojectprototype03;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class SoundManager {

    private SoundPool soundPool;

    private int invalidMoveID = -1;
    private int matchFoundID = -1;
    private int angryID = -1;
    private int delightedID = -1;
    private int embarrassedID = -1;
    private int surprisedID = -1;
    private int upsetID = -1;

    public void loadSound(Context context) {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Second parameter specifies priority of sound effect
            descriptor = assetManager.openFd("match_found.ogg");
            matchFoundID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("swap_back.ogg");
            invalidMoveID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("angry.ogg");
            angryID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("delighted.ogg");
            delightedID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("embarrassed.ogg");
            embarrassedID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("surprised.ogg");
            surprisedID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("upset.ogg");
            upsetID = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("Error", "sound file failed to load!");
        }
    }

    public void playSound(String sound) {
        switch (sound) {
            case "INVALID_MOVE":
                soundPool.play(invalidMoveID, 1, 1, 0, 0, 1);
                break;

            case "MATCH_FOUND":
                soundPool.play(matchFoundID, 1, 1, 0, 0, 1);
                break;

            case "ANGRY":
                soundPool.play(angryID, 1, 1, 0, 0, 1);
                break;

            case "DELIGHTED":
                soundPool.play(delightedID, 1, 1, 0, 0, 1);
                break;

            case "EMBARRASSED":
                soundPool.play(embarrassedID, 1, 1, 0, 0, 1);
                break;

            case "SURPRISED":
                soundPool.play(surprisedID, 1, 1, 0, 0, 1);
                break;

            case "UPSET":
                soundPool.play(upsetID, 1, 1, 0, 0, 1);
                break;
        }
    }
}
