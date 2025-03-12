package com.example.examlearnapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.Markwon;

public class GameModeActivity extends AppCompatActivity {
    private ScoreDbHelper dbHelper;
    private List<String> allQuestions;
    private List<String> allAnswers;
    private List<Integer> questionIndices = new ArrayList<>();
    private Markwon markwon;
    private TextView gameQuestionText;
    private TextView scoreDisplay;
    private TextView answerText;
    private EditText rangeInput;
    private Button yesButton, noButton, showAnswerButton, historyButton;
    private LinearLayout setupSection;
    private int score = 0;
    private int currentQuestion = 0;
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        dbHelper = new ScoreDbHelper(this);

        // Initialize views
        markwon = Markwon.create(this);
        gameQuestionText = findViewById(R.id.gameQuestionText);
        answerText = findViewById(R.id.answerText);
        rangeInput = findViewById(R.id.rangeInput);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        Button startQuizButton = findViewById(R.id.startQuizButton);
        historyButton = findViewById(R.id.historyButton);
        scoreDisplay = findViewById(R.id.scoreDisplay);
        setupSection = findViewById(R.id.setupSection);

        // hide game section
        noButton.setVisibility(View.GONE);
        yesButton.setVisibility(View.GONE);
        showAnswerButton.setVisibility(View.GONE);
        gameQuestionText.setVisibility(View.GONE);

        // Load all questions and answers
        allQuestions = loadQuestions(this, R.raw.questions);
        allAnswers = loadQuestions(this, R.raw.answers);

        // Set up button click listeners
        yesButton.setOnClickListener(v -> handleAnswer(true));
        noButton.setOnClickListener(v -> handleAnswer(false));
        showAnswerButton.setOnClickListener(v -> showAnswer());
        startQuizButton.setOnClickListener(v -> setupQuiz());
        historyButton.setOnClickListener(v -> showScoreHistory());
    }

    private void setupQuiz() {
        // Parse and validate range input
        String range = rangeInput.getText().toString().trim();
        List<Integer> validIndices = new ArrayList<>();

        try {
            if (!range.isEmpty()) {
                String[] parts = range.split(",");
                if (parts.length != 2) throw new Exception();

                int start = Integer.parseInt(parts[0].trim()) - 1;
                int end = Integer.parseInt(parts[1].trim()) - 1;

                start = Math.max(0, start);
                end = Math.min(allQuestions.size() - 1, end);

                if (start > end) {
                    Toast.makeText(this, "Nieprawidłowy zakres", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = start; i <= end; i++) {
                    validIndices.add(i);
                }
            } else {
                // Use all questions if no range specified
                for (int i = 0; i < allQuestions.size(); i++) {
                    validIndices.add(i);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Format: np. 10,20", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validIndices.isEmpty()) {
            Toast.makeText(this, "Brak pytań w zakresie", Toast.LENGTH_SHORT).show();
            return;
        }

        // Shuffle indices and initialize quiz
        questionIndices.clear();
        questionIndices.addAll(validIndices);
        Collections.shuffle(questionIndices);

        totalQuestions = questionIndices.size();
        currentQuestion = 0;
        score = 0;

        setupSection.setVisibility(View.GONE);
        historyButton.setVisibility(View.GONE);
        // show game section
        scoreDisplay.setVisibility(View.VISIBLE);
        noButton.setVisibility(View.VISIBLE);
        yesButton.setVisibility(View.VISIBLE);
        showAnswerButton.setVisibility(View.VISIBLE);
        gameQuestionText.setVisibility(View.VISIBLE);

        updateScoreDisplay();

        showNextQuestion();
    }

    private void handleAnswer(boolean known) {
        if (known) {
            score++;
        } else {
//            score = Math.max(0, score - 1);
        }
        updateScoreDisplay();
        showNextQuestion();
    }

    private void updateScoreDisplay() {
        scoreDisplay.setText("Punkty: " + score);
    }

    private void showNextQuestion() {
        if (currentQuestion < totalQuestions) {
            int actualIndex = questionIndices.get(currentQuestion);
            gameQuestionText.setText(allQuestions.get(actualIndex));
            answerText.setVisibility(View.GONE);
            currentQuestion++;
        } else {
            endGame();
        }
    }

    private void showAnswer() {
        if (currentQuestion > 0 && currentQuestion <= totalQuestions) {
            int actualIndex = questionIndices.get(currentQuestion - 1);
            String html = MarkdownToHtmlConverter.markdownToHtml(allAnswers.get(actualIndex));
            Spanned spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT);
            answerText.setText(spanned);
            answerText.setVisibility(View.VISIBLE);
        }
    }

    private void endGame() {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("score", score);
        dbHelper.addScore(score);
        startActivity(intent);
        finish();
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
            if (sb.length() > 0) {
                list.add(sb.toString().trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
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