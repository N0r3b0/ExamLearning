package com.example.examlearnapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        TextView scoreText = findViewById(R.id.scoreText);
        int score = getIntent().getIntExtra("score", 0);
        scoreText.setText("Twój wynik: " + score);
    }
}