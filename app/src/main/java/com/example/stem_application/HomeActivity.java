package com.example.stem_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the card views
        CardView learningCard = findViewById(R.id.learningCard);
        CardView testCard = findViewById(R.id.testCard);
        CardView galleryCard = findViewById(R.id.galleryCard);
        Button arBtn = findViewById(R.id.btnTryAR);

        // Set click listeners for each card
        learningCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Learning Activity
                Intent intent = new Intent(HomeActivity.this, LearningSelectionActivity.class);
                startActivity(intent);
            }
        });

        testCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Test Activity
                Intent intent = new Intent(HomeActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        galleryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Gallery Selection Activity
                Intent intent = new Intent(HomeActivity.this, GallerySelectionActivity.class);
                startActivity(intent);
            }
        });

        arBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AR Activity
                Intent intent = new Intent(HomeActivity.this, ARActivity.class);
                startActivity(intent);
            }
        });

    }
}