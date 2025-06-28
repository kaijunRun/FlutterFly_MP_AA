package com.example.stem_application;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LifecycleActivity extends AppCompatActivity {

    private VideoView videoView;
    private SeekBar seekBar;
    private ImageButton playPauseButton, forwardButton, backwardButton;
    private Handler handler = new Handler();
    private Uri videoUri;
    private boolean isUserSeeking = false;

    private EditText noteInput;
    private Button addNoteButton;
    private LinearLayout notesContainer;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LifecycleNotes";
    private static final String NOTES_KEY = "saved_notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize UI elements
        videoView = findViewById(R.id.videoView);
        seekBar = findViewById(R.id.seekBar);
        playPauseButton = findViewById(R.id.playPauseButton);
        forwardButton = findViewById(R.id.forwardButton);
        backwardButton = findViewById(R.id.backwardButton);

        noteInput = findViewById(R.id.noteInput);
        addNoteButton = findViewById(R.id.addNoteButton);
        notesContainer = findViewById(R.id.notesContainer);

        // Load saved notes
        loadSavedNotes();

        // Set video URI from raw resources
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.butterfly_lifecycle);
        videoView.setVideoURI(videoUri);

        // When video is ready
        videoView.setOnPreparedListener(mp -> {
            seekBar.setMax(videoView.getDuration());
            updateSeekBar();
        });

        // When video finishes
        videoView.setOnCompletionListener(mp -> {
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        });

        // Play / Pause toggle
        playPauseButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                videoView.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            }
        });

        // Forward 10 seconds
        forwardButton.setOnClickListener(v -> {
            int newPosition = videoView.getCurrentPosition() + 10000;
            if (newPosition < videoView.getDuration()) {
                videoView.seekTo(newPosition);
            } else {
                videoView.seekTo(videoView.getDuration());
            }
        });

        // Backward 10 seconds
        backwardButton.setOnClickListener(v -> {
            int newPosition = videoView.getCurrentPosition() - 10000;
            if (newPosition > 0) {
                videoView.seekTo(newPosition);
            } else {
                videoView.seekTo(0);
            }
        });

        // SeekBar interaction
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
                isUserSeeking = false;
            }
        });

        addNoteButton.setOnClickListener(v -> {
            String noteText = noteInput.getText().toString().trim();
            if (!noteText.isEmpty()) {
                long timestamp = System.currentTimeMillis(); // Unique identifier
                addNoteToUI(noteText, timestamp);
                saveNote(noteText, timestamp);
                noteInput.setText(""); // clear input
            }
        });
    }

    private void loadSavedNotes() {
        String savedNotes = sharedPreferences.getString(NOTES_KEY, "{}");
        try {
            JSONObject notesJson = new JSONObject(savedNotes);
            Iterator<String> keys = notesJson.keys();

            while (keys.hasNext()) {
                String timestamp = keys.next();
                String noteText = notesJson.getString(timestamp);
                addNoteToUI(noteText, Long.parseLong(timestamp));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveNote(String noteText, long timestamp) {
        try {
            // Get existing notes
            String savedNotes = sharedPreferences.getString(NOTES_KEY, "{}");
            JSONObject notesJson = new JSONObject(savedNotes);

            // Add new note
            notesJson.put(String.valueOf(timestamp), noteText);

            // Save back to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(NOTES_KEY, notesJson.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteNote(long timestamp) {
        try {
            // Get existing notes
            String savedNotes = sharedPreferences.getString(NOTES_KEY, "{}");
            JSONObject notesJson = new JSONObject(savedNotes);

            // Remove the note
            notesJson.remove(String.valueOf(timestamp));

            // Save back to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(NOTES_KEY, notesJson.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addNoteToUI(String noteText, long timestamp) {
        View noteView = getLayoutInflater().inflate(R.layout.item_note, null);

        TextView noteTextView = noteView.findViewById(R.id.noteText);
        Button deleteButton = noteView.findViewById(R.id.deleteNoteButton);

        noteTextView.setText(noteText);
        deleteButton.setOnClickListener(v -> {
            notesContainer.removeView(noteView);
            deleteNote(timestamp);
        });

        notesContainer.addView(noteView);
    }

    // Update SeekBar progress while playing
    private void updateSeekBar() {
        seekBar.setProgress(videoView.getCurrentPosition());

        if (videoView.isPlaying() && !isUserSeeking) {
            handler.postDelayed(this::updateSeekBar, 500);
        }
    }
}