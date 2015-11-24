package com.markchanner.mscprojectprototype03;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.IOException;

/**
 * @author Mark Channer for Birkbeck MSc Computer Science project
 */
public class GameActivity extends Activity {

    public static final int X_MAX = 8;
    public static final int Y_MAX = 7;

    private final static String TAG = "GameActivity";
    private LinearLayout screenLayout;
    private MediaPlayer mediaPlayer;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "called onCreate(Bundle)");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        screenLayout = (LinearLayout) findViewById(R.id.gameLayout);

        mediaPlayer = new MediaPlayer();
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor musicDescriptor = assetManager.openFd("shroom_ridge.ogg");  // Temporary music
            mediaPlayer.setDataSource(musicDescriptor.getFileDescriptor(),
                    musicDescriptor.getStartOffset(), musicDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            mediaPlayer = null;
            Log.e(TAG, "IOException in onCreate: " + e);
            e.printStackTrace();
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int sizeX = (size.x - ((screenLayout.getPaddingLeft() * 2) + screenLayout.getPaddingRight()));
        int sizeY = (size.y - (screenLayout.getPaddingTop() + screenLayout.getPaddingBottom()));
        int gameViewX = (int) (sizeX * 0.8);
        int scoreViewX = (int) (sizeX * 0.2);

        ScoreBoardView scoreView = new ScoreBoardView(this, scoreViewX, sizeY / 3);
        LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(scoreViewX, sizeY / 3));
        screenLayout.addView(scoreView, scoreParams);

        int emoWidth = gameViewX / X_MAX;
        int emoHeight = sizeY / Y_MAX;
        Board boardLogic = new BoardImpl(this, scoreView, emoWidth, emoHeight);

        gameView = new GameView(this, boardLogic, gameViewX, sizeY);
        LinearLayout.LayoutParams boardParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(gameViewX, sizeY));
        boardParams.setMargins(screenLayout.getPaddingLeft(), 0, gameViewX, 0);
        screenLayout.addView(gameView, boardParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "called onResume()");
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "called onPause()");
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            if (isFinishing()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        gameView.pause();
    }
}