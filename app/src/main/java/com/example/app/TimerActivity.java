package com.example.app;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startButton;
    private Button pauseButton;
    private Button stopButton;
    private TimePicker timePicker;
    private CountDownTimer countDownTimer;
    private long timeInMillis = 0;
    private long timeRemaining = 0;
    private boolean isTimerRunning = false;
    private Button mainMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timePicker = findViewById(R.id.timePicker);
        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        timePicker.setIs24HourView(true);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTimerRunning) {
                    timeInMillis = getTimeInMillisFromTimePicker();
                    startTimer(timeRemaining > 0 ? timeRemaining : timeInMillis);
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
            }
        });

        mainMenuButton = findViewById(R.id.mainMenuButton);

        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private long getTimeInMillisFromTimePicker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            return (hour * 3600 + minute * 60) * 1000;
        } else {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            return (hour * 3600 + minute * 60) * 1000;
        }
    }

    private void startTimer(long millisInFuture) {
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timeRemaining = 0;
                updateTimerText();
            }
        }.start();
        isTimerRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        timeInMillis = 0;
        timeRemaining = 0;
        updateTimerText();
    }

    private void updateTimerText() {
        String timeText = formatTime(timeRemaining);
        timerTextView.setText(timeText);
    }

    private String formatTime(long timeInMillis) {
        int hours = (int) (timeInMillis / 3600000);
        int minutes = (int) ((timeInMillis % 3600000) / 60000);
        int seconds = (int) ((timeInMillis % 60000) / 1000);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
