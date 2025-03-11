package com.example.examlearnapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    private List<String> questions;
    private List<String> answers;
    private TextView questionText;
    private TextView answerText;
    private EditText numberInput;
    private int currentQuestionIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        answerText = findViewById(R.id.answerText);
        numberInput = findViewById(R.id.numberInput);
        Button randomButton = findViewById(R.id.randomButton);
        Button showAnswerButton = findViewById(R.id.showAnswerButton);
        Button gameModeButton = findViewById(R.id.gameModeButton);
        Button previousButton = findViewById(R.id.previousButton);
        Button showQuestionButton = findViewById(R.id.showQuestionButton);
        Button nextButton = findViewById(R.id.nextButton);

        // Load questions and answers
        questions = loadQuestions(this, R.raw.questions);
        answers = loadQuestions(this, R.raw.answers);

        // Button click listeners
        randomButton.setOnClickListener(v -> showRandomQuestion());
        showAnswerButton.setOnClickListener(v -> showAnswer());
        gameModeButton.setOnClickListener(v -> startGameMode());

        // New buttons
        previousButton.setOnClickListener(v -> showPreviousQuestion());
        nextButton.setOnClickListener(v -> showNextQuestion());
        showQuestionButton.setOnClickListener(v -> showQuestion());
    }

    private void showQuestion() {
        String input = numberInput.getText().toString().trim();
        if (!input.isEmpty()) {
            try {
                int number = Integer.parseInt(input);
                if (number >= 1 && number <= questions.size()) {
                    currentQuestionIndex = number - 1;
                    numberInput.getText().clear();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            currentQuestionIndex = (currentQuestionIndex + 1) % questions.size();
        }
        updateQuestionDisplay();
    }

    private void showPreviousQuestion() {
        currentQuestionIndex = (currentQuestionIndex - 1 + questions.size()) % questions.size();
        updateQuestionDisplay();
    }

    private void showNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questions.size();
        updateQuestionDisplay();
    }

    private void updateQuestionDisplay() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            questionText.setText(questions.get(currentQuestionIndex));
            answerText.setVisibility(View.INVISIBLE);
        }
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

    private void showRandomQuestion() {
        Random random = new Random();
        currentQuestionIndex = random.nextInt(questions.size());
        questionText.setText(questions.get(currentQuestionIndex));
        answerText.setVisibility(View.INVISIBLE);
    }

    private void showAnswer() {
        if (currentQuestionIndex != -1) {
            answerText.setText(answers.get(currentQuestionIndex));
            answerText.setVisibility(View.VISIBLE);
        }
    }

    private void chooseQuestion() {
        String input = numberInput.getText().toString();
        if (!input.isEmpty()) {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < questions.size()) {
                currentQuestionIndex = index;
                questionText.setText(questions.get(index));
            }
            answerText.setVisibility(View.INVISIBLE);
        }
    }

    private void startGameMode() {
        Intent intent = new Intent(this, GameModeActivity.class);
        startActivity(intent);
    }
}
