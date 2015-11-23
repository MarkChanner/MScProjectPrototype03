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

    private final static String TAG = "GameActivity";
    private MediaPlayer mediaPlayer;
    private GameView gameBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "called onCreate(Bundle)");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

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
            Log.d(TAG, "IOException in onCreate: " + e);
            e.printStackTrace();
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        LinearLayout layout = (LinearLayout) findViewById(R.id.gameLayout);
        int leftPad = layout.getPaddingLeft();
        int rightPad = layout.getPaddingRight();
        int topPad = layout.getPaddingTop();
        int bottomPad = layout.getPaddingBottom();
        int gameboardLeftMargin = leftPad;

        int sizeX = (size.x - (leftPad + rightPad + gameboardLeftMargin));
        int sizeY = (size.y - (topPad + bottomPad));

        int boardX = (int) (sizeX * 0.8);
        int boardY = sizeY;
        int scoreX = (int) (sizeX * 0.2);
        int scoreY = boardY;

        ScoreBoardView scoreBoardView = new ScoreBoardView(this, scoreX, scoreY);
        LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(scoreX, scoreY));
        layout.addView(scoreBoardView, scoreParams);

        gameBoardView = new GameView(this, boardX, boardY);
        LinearLayout.LayoutParams boardParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(boardX, boardY));
        boardParams.setMargins(gameboardLeftMargin, 0, boardX, 0);
        layout.addView(gameBoardView, boardParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "called onResume()");
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        gameBoardView.resume();
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
        gameBoardView.pause();
    }
}