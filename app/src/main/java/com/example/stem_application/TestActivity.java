package com.example.stem_application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class TestActivity extends AppCompatActivity {

    private int correctCount = 0;
    private final int TOTAL_STAGES = 4;
    private Button nextButton;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        nextButton = findViewById(R.id.nextButton);

        // Disable the button initially
        nextButton.setEnabled(false);

        // Navigate to QuizActivity when the next button is clicked
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, QuizActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set drag listeners for draggable images
        setDragListeners();

        // Reset all targets
        resetAllTargets();
    }


    private void setDragListeners() {
        findViewById(R.id.eggImage).setOnLongClickListener(new MyLongClickListener());
        findViewById(R.id.caterpillarImage).setOnLongClickListener(new MyLongClickListener());
        findViewById(R.id.pupaImage).setOnLongClickListener(new MyLongClickListener());
        findViewById(R.id.butterflyImage).setOnLongClickListener(new MyLongClickListener());

        findViewById(R.id.target1).setOnDragListener(new MyDragListener(1));
        findViewById(R.id.target2).setOnDragListener(new MyDragListener(2));
        findViewById(R.id.target3).setOnDragListener(new MyDragListener(3));
        findViewById(R.id.target4).setOnDragListener(new MyDragListener(4));
    }

    private void resetAllTargets() {
        FrameLayout target1 = findViewById(R.id.target1);
        FrameLayout target2 = findViewById(R.id.target2);
        FrameLayout target3 = findViewById(R.id.target3);
        FrameLayout target4 = findViewById(R.id.target4);

        target1.removeAllViews();
        target2.removeAllViews();
        target3.removeAllViews();
        target4.removeAllViews();

        target1.setTag(null);
        target2.setTag(null);
        target3.setTag(null);
        target4.setTag(null);

        correctCount = 0;
        nextButton.setEnabled(false);
    }

    class MyLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(null, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    class MyDragListener implements View.OnDragListener {
        private final int expectedStage;
        private View draggedView;

        MyDragListener(int expectedStage) {
            this.expectedStage = expectedStage;
        }

        @Override
        public boolean onDrag(View targetView, DragEvent event) {
            FrameLayout target = (FrameLayout) targetView;

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    draggedView = (View) event.getLocalState();
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    target.setBackgroundResource(R.drawable.target_slot_circle_highlight);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    target.setBackgroundResource(R.drawable.target_slot_circle);
                    return true;

                case DragEvent.ACTION_DROP:
                    if (draggedView == null) return false;

                    // Remove any existing view in target
                    target.removeAllViews();

                    // Create new ImageView
                    ImageView draggedImageView = (ImageView) draggedView;
                    ImageView newImage = new ImageView(TestActivity.this);
                    newImage.setImageDrawable(draggedImageView.getDrawable());
                    newImage.setTag(draggedImageView.getTag());

                    // Set layout to fill the target
                    newImage.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    ));

                    target.addView(newImage);

                    // Check correctness
                    int draggedStage = Integer.parseInt(draggedImageView.getTag().toString());
                    if (draggedStage == expectedStage) {
                        correctCount++;
                        target.setTag("correct");
                    } else {
                        convertToGrayscale(newImage);
                        target.setTag("incorrect");
                        Toast.makeText(TestActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                    }

                    // Update next button
                    nextButton.setEnabled(correctCount == TOTAL_STAGES);

                    // Show original view
                    draggedView.setVisibility(View.VISIBLE);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    target.setBackgroundResource(R.drawable.target_slot_circle);
                    if (draggedView != null && !event.getResult()) {
                        draggedView.setVisibility(View.VISIBLE);
                    }
                    return true;
            }
            return false;
        }
    }

    private void convertToGrayscale(ImageView imageView) {
        try {
            Drawable drawable = imageView.getDrawable();
            Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();

            Mat srcMat = new Mat();
            Mat grayMat = new Mat();
            Utils.bitmapToMat(originalBitmap, srcMat);
            Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_RGB2GRAY);
            Imgproc.cvtColor(grayMat, srcMat, Imgproc.COLOR_GRAY2RGB);

            Bitmap grayBitmap = Bitmap.createBitmap(srcMat.cols(), srcMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(srcMat, grayBitmap);
            imageView.setImageBitmap(grayBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback if OpenCV fails
            imageView.setColorFilter(Color.argb(150, 150, 150, 150));
        }
    }
}