package com.example.examlearnapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameModeActivity extends AppCompatActivity {
    private List<String> gameQuestions;
    private List<String> answers;
    private TextView gameQuestionText;
    private TextView answerText;
    private Button yesButton, noButton, showAnswerButton;
    private int score = 0;
    private int questionCount = 0;
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        gameQuestionText = findViewById(R.id.gameQuestionText);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        answerText = findViewById(R.id.answerText);

        gameQuestions = loadQuestions(this, R.raw.questions);
        answers = loadQuestions(this, R.raw.answers);
        Collections.shuffle(gameQuestions);
        totalQuestions = 3;

        showNextQuestion();

        yesButton.setOnClickListener(v -> {
            score++;
            showNextQuestion();
        });

        noButton.setOnClickListener(v -> {
            score--;
            showNextQuestion();
        });

        showAnswerButton.setOnClickListener(v -> showAnswer());
    }

    private void showNextQuestion() {
        if (questionCount < totalQuestions) {
            gameQuestionText.setText(gameQuestions.get(questionCount));
            answerText.setVisibility(View.GONE);
            questionCount++;
        } else {
            endGame();
        }
    }

    private void showAnswer() {
        if (questionCount > 0 && questionCount <= answers.size()) {
            answerText.setText(answers.get(questionCount - 1));
            answerText.setVisibility(View.VISIBLE);
        }
    }

    private void endGame() {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    public static List<String> loadQuestions(android.content.Context context, int resourceId) {
        List<String> list = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}