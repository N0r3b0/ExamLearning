package com.example.examlearnapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScoreActivity extends AppCompatActivity {
    private ScoreDbHelper dbHelper;
    private Button backButton, backToGameMode, historyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        dbHelper = new ScoreDbHelper(this);

        backButton = findViewById(R.id.backButton);
        backToGameMode = findViewById(R.id.backToGameMode);
        historyButton = findViewById(R.id.historyButton);
        TextView scoreText = findViewById(R.id.scoreText);
        int score = getIntent().getIntExtra("score", 0);
        scoreText.setText("Twój wynik: " + score);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        backToGameMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameModeActivity.class);
            startActivity(intent);
            finish();
        });
        historyButton.setOnClickListener(v -> showScoreHistory());
    }

    private void showScoreHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Historia wyników");

        // Get scores from database
        Cursor cursor = dbHelper.getAllScores();
        StringBuilder history = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        while (cursor.moveToNext()) {
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDbHelper.COLUMN_SCORE));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDbHelper.COLUMN_TIMESTAMP));

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
                timestamp = dateFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

            history.append("Wynik: ").append(score)
                    .append("\nData: ").append(timestamp)
                    .append("\n\n");
        }
        cursor.close();

        builder.setMessage(history.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}