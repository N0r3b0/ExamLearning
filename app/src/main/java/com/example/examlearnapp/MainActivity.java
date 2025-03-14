package com.example.examlearnapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import io.noties.markwon.Markwon;

public class MainActivity extends AppCompatActivity {
    private List<String> questions;
    private List<String> answers;
    private TextView questionText;
    private TextView answerText;
    private Markwon markwon;
    private EditText numberInput;
    private int currentQuestionIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        answerText = findViewById(R.id.answerText);
        markwon = Markwon.create(this);
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
            answerText.setVisibility(View.GONE);
        }
    }

    public static List<String> loadQuestions(Context context, int resourceId) {
        List<String> list = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            // Use a regex to separate answers (e.g., %%%)
            Pattern pattern = Pattern.compile("%%%\\d+%%%");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);

                if (matcher.matches()) {
                    if (sb.length() > 0) {
                        list.add(sb.toString().trim());
                        sb.setLength(0);
                    }
                } else {
                    sb.append(line).append("\n");
                }
            }
            // Add the last answer if exists
            if (sb.length() > 0) {
                list.add(sb.toString().trim());
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
        updateQuestionDisplay();
    }

    private void showAnswer() {
        if (currentQuestionIndex != -1) {
            String html = MarkdownToHtmlConverter.markdownToHtml(answers.get(currentQuestionIndex));
            Spanned spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT);
            answerText.setText(spanned);
            answerText.setVisibility(View.VISIBLE);
        }
    }
    private void startGameMode() {
        Intent intent = new Intent(this, GameModeActivity.class);
        startActivity(intent);
    }

}
