package com.example.stem_application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class PictureViewActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private Butterfly butterfly;
    private ImageView butterflyImageView;
    private TextView butterflyTitle;
    private TextView butterflyDescription;
    private SeekBar brightnessSeekBar;
    private SeekBar sizeSeekBar;
    private Bitmap originalBitmap;
    private Bitmap adjustedBitmap;

    // Static OpenCV initialization
    static {
        if (!OpenCVLoader.initDebug()) {
            System.loadLibrary("opencv_java4");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        // Get the selected butterfly
        butterfly = (Butterfly) getIntent().getSerializableExtra("butterfly");

        // Initialize views
        butterflyImageView = findViewById(R.id.butterflyImageView);
        butterflyTitle = findViewById(R.id.butterflyTitle);
        butterflyDescription = findViewById(R.id.butterflyDescription);
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        sizeSeekBar = findViewById(R.id.sizeSeekBar);

        // Set butterfly info
        butterflyTitle.setText(butterfly.getName());
        butterflyDescription.setText(butterfly.getDescription());

        // Load the butterfly image
        originalBitmap = BitmapFactory.decodeResource(getResources(), butterfly.getImageResId());
        adjustedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        butterflyImageView.setImageBitmap(originalBitmap);

        // Set up seek bars
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjustImageBrightness(progress - 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = progress / 100f;
                butterflyImageView.setScaleX(scale);
                butterflyImageView.setScaleY(scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void adjustImageBrightness(int value) {
        if (originalBitmap == null) return;

        try {
            // Convert bitmap to Mat
            Mat srcMat = new Mat();
            Utils.bitmapToMat(originalBitmap, srcMat);

            // Convert to HSV color space
            Mat hsvMat = new Mat();
            Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_RGB2HSV);

            // Split channels
            java.util.List<Mat> channels = new java.util.ArrayList<>(3);
            Core.split(hsvMat, channels);

            // Adjust brightness (V channel)
            Mat vChannel = channels.get(2);
            vChannel.convertTo(vChannel, CvType.CV_32F);
            Core.multiply(vChannel, new Scalar(1 + value/100.0), vChannel);
            vChannel.convertTo(vChannel, CvType.CV_8U);

            // Merge channels back
            Core.merge(channels, hsvMat);

            // Convert back to RGB
            Imgproc.cvtColor(hsvMat, hsvMat, Imgproc.COLOR_HSV2RGB);

            // Convert back to bitmap
            Utils.matToBitmap(hsvMat, adjustedBitmap);
            butterflyImageView.setImageBitmap(adjustedBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to Android's ColorMatrix if OpenCV fails
            ColorMatrix matrix = new ColorMatrix();
            matrix.setScale(1 + value/100f, 1 + value/100f, 1 + value/100f, 1);
            butterflyImageView.setColorFilter(new ColorMatrixColorFilter(matrix));
        }
    }
}