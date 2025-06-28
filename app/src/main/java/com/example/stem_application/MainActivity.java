package com.example.stem_application;

import static android.view.animation.AnimationUtils.loadAnimation;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView name, slogan;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.imageView);
        name = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);
        button = findViewById(R.id.button);

        image.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);
        button.setAnimation(bottomAnim);

        // Set click listener for the button
        button.setOnClickListener(v -> {
            // Create an intent to start the HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);

            // Start the new activity with a smooth transition
            startActivity(intent);

            // Add a fade in/out transition between activities
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            // Finish the current activity if you don't want users to go back
             finish();
        });

    }
}
