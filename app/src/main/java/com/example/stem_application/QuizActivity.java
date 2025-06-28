package com.example.stem_application;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class QuizActivity extends AppCompatActivity {

    private TextView questionText, progressText, feedbackText;
    private ImageView questionImage;
    private Button trueButton, falseButton, nextButton;
    private CardView feedbackCard;

    private int currentQuestionIndex = 0;
    private int score = 0;

    private final Question[] questions = {
            new Question(
                    "Every butterfly has three main parts: a head, a middle (thorax), and a bottom (abdomen).",
                    true,
                    "That's right! All butterflies have three body parts: the head, the thorax, and the abdomen. Great job!",
                    "Oops! Actually, butterflies do have these three parts. The head, the thorax (middle), and the abdomen (bottom). Let’s try another question!",
                    R.drawable.q1
            ),
            new Question(
                    "Butterflies use their antennae (feelers on their head) to smell and find food or a mate.",
                    true,
                    "Excellent! The antennae help butterflies sense smells in the air, so they can find yummy flowers or a butterfly friend.",
                    "Butterfly antennae do help them smell and find food or mates. They’re like tiny, super-smelling straws on their heads.",
                    R.drawable.q2
            ),
            new Question(
                    "Butterflies use their long, straw-like tongue (proboscis) to drink nectar from flowers.",
                    true,
                    "Yes! Butterflies unroll their proboscis (like a tiny straw) to sip yummy flower nectar.",
                    "Oops! Butterflies do use their proboscis to drink nectar. It works like a built-in juice straw!",
                    R.drawable.q3
            ),
            new Question(
                    "Butterfly eyes work exactly like human eyes, with just one lens to see light and dark.",
                    false,
                    "Correct! Butterfly eyes are compound. They have many tiny lenses (not just one) to spot colors, light, and fast movement.",
                    "Oops! Not quite right. Butterfly eyes are not like ours. They’re made of thousands of tiny lenses! This helps them see movement and colors.",
                    R.drawable.q4
            ),
            new Question(
                    "A butterfly's wings are connected to its bottom (abdomen).",
                    false,
                    "You're correct! The wings connect to the middle body part (thorax), not the abdomen. Good spotting!",
                    "Oops! Butterflies actually have their wings attached to their middle part (thorax), not their bottom.",
                    R.drawable.q5
            )
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        progressText = findViewById(R.id.progressText);
        feedbackText = findViewById(R.id.feedbackText);
        questionImage = findViewById(R.id.questionImage);
        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);
        nextButton = findViewById(R.id.nextButton);
        feedbackCard = findViewById(R.id.feedbackCard);

        // Load first question
        loadQuestion(currentQuestionIndex);

        // Set button click listeners
        trueButton.setOnClickListener(v -> checkAnswer(true));
        falseButton.setOnClickListener(v -> checkAnswer(false));
        nextButton.setOnClickListener(v -> nextQuestion());
    }

    private void loadQuestion(int index) {
        if (index < questions.length) {
            Question currentQuestion = questions[index];
            questionText.setText(currentQuestion.getQuestion());
            questionImage.setImageResource(currentQuestion.getImageRes());
            progressText.setText("Question " + (index + 1) + "/" + questions.length);
            feedbackCard.setVisibility(View.GONE);
        } else {
            showResults();
        }
    }

    private void checkAnswer(boolean userAnswer) {
        Question currentQuestion = questions[currentQuestionIndex];
        boolean isCorrect = (userAnswer == currentQuestion.isAnswer());

        if (isCorrect) {
            score++;
            feedbackText.setText(currentQuestion.getCorrectFeedback());
        } else {
            feedbackText.setText(currentQuestion.getIncorrectFeedback());
        }

        // Disable answer buttons
        trueButton.setEnabled(false);
        falseButton.setEnabled(false);

        // Show feedback
        feedbackCard.setVisibility(View.VISIBLE);
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            // Enable buttons for next question
            trueButton.setEnabled(true);
            falseButton.setEnabled(true);
            loadQuestion(currentQuestionIndex);
        } else {
            showResults();
        }
    }

    private void showResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Complete!");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_results, null);
        builder.setView(dialogView);

        TextView resultsText = dialogView.findViewById(R.id.resultsText);
        resultsText.setText("You got " + score + " out of " + questions.length + " correct!");

        ImageView resultImage = dialogView.findViewById(R.id.resultImage);
        if(score == questions.length) {
            resultImage.setImageResource(R.drawable.butterfly_excellent);
            resultsText.append("\n\nYou're a butterfly expert!");
        } else if(score >= questions.length/2) {
            resultImage.setImageResource(R.drawable.butterfly_good);
            resultsText.append("\n\nGreat job! Keep learning!");
        } else {
            resultImage.setImageResource(R.drawable.butterfly_encouragement);
            resultsText.append("\n\nNice try! Butterflies are fascinating to learn about!");
        }

        Button doneButton = dialogView.findViewById(R.id.doneButton);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        doneButton.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
    }

    // Question model class
    private static class Question {
        private final String question;
        private final boolean answer;
        private final String correctFeedback;
        private final String incorrectFeedback;
        private final int imageRes;

        public Question(String question, boolean answer, String correctFeedback,
                        String incorrectFeedback, int imageRes) {
            this.question = question;
            this.answer = answer;
            this.correctFeedback = correctFeedback;
            this.incorrectFeedback = incorrectFeedback;
            this.imageRes = imageRes;
        }

        public String getQuestion() { return question; }
        public boolean isAnswer() { return answer; }
        public String getCorrectFeedback() { return correctFeedback; }
        public String getIncorrectFeedback() { return incorrectFeedback; }
        public int getImageRes() { return imageRes; }
    }
}