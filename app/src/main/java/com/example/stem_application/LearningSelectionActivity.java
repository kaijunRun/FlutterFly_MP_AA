package com.example.stem_application;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class LearningSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_selection);

        // Set up butterfly animation
        ImageView butterflyImage = findViewById(R.id.butterflyImage);
        animateButterfly(butterflyImage);

        // Set click listeners for active cards
        CardView cardLifecycle = findViewById(R.id.cardLifecycle);
        CardView cardSpecies = findViewById(R.id.cardSpecies);

        cardLifecycle.setOnClickListener(v -> {
            Intent intent = new Intent(LearningSelectionActivity.this, LifecycleActivity.class);
            startActivity(intent);
        });

        cardSpecies.setOnClickListener(v -> {
            Intent intent = new Intent(LearningSelectionActivity.this, AnatomyActivity.class);
            startActivity(intent);
        });
    }

    private void animateButterfly(ImageView butterfly) {
        // Create flutter animation
        ObjectAnimator flutterAnim = ObjectAnimator.ofFloat(butterfly, "translationY", 0f, -20f, 0f);
        flutterAnim.setDuration(1000);
        flutterAnim.setRepeatCount(ValueAnimator.INFINITE);
        flutterAnim.setRepeatMode(ValueAnimator.REVERSE);
        flutterAnim.start();

        // Add rotation for more realistic effect
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(butterfly, "rotation", -10f, 10f);
        rotateAnim.setDuration(1500);
        rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnim.setRepeatMode(ValueAnimator.REVERSE);
        rotateAnim.start();
    }
}