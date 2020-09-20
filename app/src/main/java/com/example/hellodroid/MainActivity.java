package com.example.hellodroid;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // handler & timer
    private Handler handler = new Handler(Looper.myLooper());
    private Timer timer = new Timer();

    // status
    private boolean actionFlag = false;
    private boolean startFlag = false;

    private TextView scoreLabel;
    private TextView startLabel;
    ImageView box;
    ImageView black;
    ImageView red;
    ImageView orange;

    // サイズ
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    // 位置
    private float boxY;
    private float orangeX;
    private float orangeY;
    private float redX;
    private float redY;
    private float blackX;
    private float blackY;

    // speed
    private int boxSpeed;
    private int orangeSpeed;
    private int redSpeed;
    private int blackSpeed;


    // Score
    private int score = 0;

    // Sound
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        red = findViewById(R.id.red);
        black = findViewById(R.id.black);
        orange = findViewById(R.id.orange);

        // Screen Size

        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        screenHeight = size.x;
        screenWidth = size.y;
//        DisplayMetrics dm = new DisplayMetrics();
//        screenHeight = dm.heightPixels;
//        screenWidth = dm.widthPixels;
        boxSpeed = Math.round(screenHeight / 60f);
        orangeSpeed = Math.round(screenHeight / 60f);
        redSpeed = Math.round(screenHeight / 36f);
        blackSpeed = Math.round(screenHeight / 45f);


        red.setX(-80.0f);
        red.setY(-80.0f);
        black.setY(-80.0f);
        black.setX(-80.0f);
        orange.setY(-80.0f);
        orange.setX(-80.0f);

        boxY = 500.0f;

        scoreLabel.setText("Score : 0");
    }

    public void changePos() {
        hitCheck();

        // Orange
        orangeX -= orangeSpeed;
        if (orangeX < 0) {
            orangeX = screenWidth + 20;
            orangeY = (float) Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setY(orangeY);
        orange.setX(orangeX);

        // black
        blackX -= blackSpeed;
        if (blackX < 0) {
            blackX = screenWidth + 10;
            blackY = (float) Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setY(blackY);
        black.setX(blackX);
        // red
        redX -= redSpeed;
        if (redX < 0) {
            redX = screenWidth + 5000;
            redY = (float) Math.floor(Math.random() * (frameHeight - red.getHeight()));
        }
        red.setX(redX);
        red.setY(redY);

        // box
        if (actionFlag) {
            boxY -= boxSpeed;
        } else {
            boxY += boxSpeed;
        }

        if (boxY < 0) {
            boxY = 0;
        }
        if (boxY > frameHeight - boxSize) {
            boxY = frameHeight - boxSize;
        }

        box.setY(boxY);

        scoreLabel.setText("Score : " + score);
    }

    @Override
    public void onBackPressed() {
    }

    private void hitCheck() {
        // orange
        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        if (hitStatus(orangeCenterX, orangeCenterY)) {
            orangeX = -10.0f;
            score += 10;
            soundPlayer.playHitSound();
        }
        // red
        float redCenterX = redX + red.getWidth() / 2;
        float redCenterY = redY + red.getHeight() / 2;

        if (hitStatus(redCenterX, redCenterY)) {
            redX = -10.0f;
            score += 30;
            soundPlayer.playHitSound();
        }
        // Black
        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;

        if (hitStatus(blackCenterX, blackCenterY)) {
            soundPlayer.playOverSound();
            // Game Over!
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            // 結果画面へ
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("Score", score);
            startActivity(intent);
        }
    }

    public boolean hitStatus(float centerX, float centerY) {
        return (0 <= centerX && centerX <= boxSize &&
                boxY <= centerY && centerY <= boxY + boxSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!startFlag) {
            startFlag = true;

            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            boxY = box.getY();
            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);
        } else {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                actionFlag = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                actionFlag = false;
            }
        }

        return true;
    }
}
