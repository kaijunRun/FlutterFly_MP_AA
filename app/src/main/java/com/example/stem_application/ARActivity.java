package com.example.stem_application;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class ARActivity extends AppCompatActivity {
    private ArFragment arFragment;
    private ModelRenderable butterflyRenderable1;
    private ModelRenderable butterflyRenderable2;

    private ModelRenderable butterflyRenderable3;
    private final Random random = new Random();
    private int butterflyCount = 0;
    private static final int MAX_BUTTERFLIES = 5;
    private boolean isAnimationRunning = false;

    private final List<AnimatedButterfly> animatedButterflies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aractivity);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        if (arFragment != null) {
            loadButterflyModels();
        } else {
            Toast.makeText(this, "AR Fragment not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadButterflyModels() {
        // Load first butterfly model
        CompletableFuture<ModelRenderable> butterfly1 = ModelRenderable.builder()
                .setSource(this,
                        RenderableSource.builder()
                                .setSource(this, Uri.parse("models/butterfly1.glb"),
                                        RenderableSource.SourceType.GLB)
                                .setScale(0.5f)
                                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                .build())
                .setRegistryId("butterfly_model_1")
                .build();

        // Load second butterfly model
        CompletableFuture<ModelRenderable> butterfly2 = ModelRenderable.builder()
                .setSource(this,
                        RenderableSource.builder()
                                .setSource(this, Uri.parse("models/butterfly2.glb"),
                                        RenderableSource.SourceType.GLB)
                                .setScale(0.5f)
                                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                .build())
                .setRegistryId("butterfly_model_2")
                .build();

        // Load third butterfly model
        CompletableFuture<ModelRenderable> butterfly4 = ModelRenderable.builder()
                .setSource(this,
                        RenderableSource.builder()
                                .setSource(this, Uri.parse("models/butterfly3.glb"),
                                        RenderableSource.SourceType.GLB)
                                .setScale(0.5f)
                                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                .build())
                .setRegistryId("butterfly_model_3")
                .build();

        CompletableFuture.allOf(butterfly1, butterfly2, butterfly4)
                .thenAccept(ignored -> {
                    butterflyRenderable1 = butterfly1.getNow(null);
                    butterflyRenderable2 = butterfly2.getNow(null);
                    butterflyRenderable3 = butterfly4.getNow(null);
                    Toast.makeText(this, "Butterfly models loaded!", Toast.LENGTH_SHORT).show();
                    setupTapListener();
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Model load error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("ARActivity", "Model load failed", throwable);
                    return null;
                });
    }

    private void setupTapListener() {
        arFragment.setOnTapArPlaneListener((HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
            if (butterflyRenderable1 == null || butterflyRenderable2 == null || butterflyRenderable3 == null) {
                Toast.makeText(this, "Models not ready yet!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (butterflyCount >= MAX_BUTTERFLIES) {
                Toast.makeText(this, "Too many butterflies!", Toast.LENGTH_SHORT).show();
                return;
            }

            butterflyCount++;

            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            TransformableNode butterflyNode = new TransformableNode(arFragment.getTransformationSystem());
            butterflyNode.setParent(anchorNode);

            // Ternary chain for 4 butterfly types
            int butterflyType = random.nextInt(4);
            butterflyNode.setRenderable(
                    butterflyType == 0 ? butterflyRenderable1 :
                            butterflyType == 1 ? butterflyRenderable2 :
                                            butterflyRenderable3
            );

            Vector3 initialPos = new Vector3(
                    (random.nextFloat() - 0.5f) * 0.5f,
                    random.nextFloat() * 0.3f,
                    -0.5f + (random.nextFloat() * -0.5f)
            );
            butterflyNode.setLocalPosition(initialPos);
            butterflyNode.select();

            // Adjust animation parameters based on butterfly type (optional)
            float flutterSpeed = 3f + random.nextFloat() * 3f; // All types same speed (or customize per type)
            float hoverAmplitude = 0.03f + random.nextFloat() * 0.1f;

            animatedButterflies.add(new AnimatedButterfly(butterflyNode, initialPos, flutterSpeed, hoverAmplitude));

            if (!isAnimationRunning) {
                startAnimationLoop();
            }
        });
    }

    private void startAnimationLoop() {
        if (isAnimationRunning) return;

        isAnimationRunning = true;
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                try {
                    updateButterflyAnimations(frameTimeNanos);
                    Choreographer.getInstance().postFrameCallback(this);
                } catch (Exception e) {
                    Log.e("ARActivity", "Animation error", e);
                    isAnimationRunning = false;
                }
            }
        });
    }

    private void updateButterflyAnimations(long frameTimeNanos) {
        float timeSeconds = frameTimeNanos / 1_000_000_000.0f;

        for (AnimatedButterfly butterfly : animatedButterflies) {
            if (butterfly.node == null || butterfly.originalPosition == null) continue;

            // Wing flapping animation (rotation around X-axis)
            float flapAngle = (float) Math.sin(timeSeconds * butterfly.flutterSpeed * 6f) * 45f;
            Quaternion flapRotation = Quaternion.axisAngle(new Vector3(1f, 0f, 0f), flapAngle);

            // Body rotation (slight tilt)
            float bodyAngle = (float) Math.sin(timeSeconds * butterfly.flutterSpeed * 0.5f) * 10f;
            Quaternion bodyRotation = Quaternion.axisAngle(new Vector3(0f, 1f, 0f), bodyAngle);

            // Combine rotations
            Quaternion combinedRotation = Quaternion.multiply(flapRotation, bodyRotation);

            // Floating movement
            float yOffset = (float) Math.sin(timeSeconds * butterfly.flutterSpeed * 2.0f) * butterfly.hoverAmplitude;
            float xOffset = (float) Math.cos(timeSeconds * butterfly.flutterSpeed * 1.5f) * butterfly.hoverAmplitude;

            Vector3 newPosition = new Vector3(
                    butterfly.originalPosition.x + xOffset,
                    butterfly.originalPosition.y + yOffset,
                    butterfly.originalPosition.z
            );

            butterfly.node.setLocalPosition(newPosition);
            butterfly.node.setLocalRotation(combinedRotation);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAnimationRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!animatedButterflies.isEmpty() && !isAnimationRunning) {
            startAnimationLoop();
        }
    }

    private static class AnimatedButterfly {
        final Node node;
        final Vector3 originalPosition;
        final float flutterSpeed;
        final float hoverAmplitude;

        AnimatedButterfly(Node node, Vector3 originalPosition, float flutterSpeed, float hoverAmplitude) {
            this.node = node;
            this.originalPosition = originalPosition;
            this.flutterSpeed = flutterSpeed;
            this.hoverAmplitude = hoverAmplitude;
        }
    }
}