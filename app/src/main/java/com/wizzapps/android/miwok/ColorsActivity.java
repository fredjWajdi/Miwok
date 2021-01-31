package com.wizzapps.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ColorsActivity extends AppCompatActivity {
    private List<Word> words;
    private MediaPlayer mediaPlayer;
    private final MediaPlayer.OnCompletionListener completionListener = mp -> releaseMediaPlayer();
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        words = new ArrayList<>();
        words.add(new Word("red", "weṭeṭṭi", R.drawable.color_red, R.raw.color_red));
        words.add(new Word("green", "chokokki", R.drawable.color_green, R.raw.color_green));
        words.add(new Word("brown", "ṭakaakki", R.drawable.color_brown, R.raw.color_brown));
        words.add(new Word("gray", "ṭopoppi", R.drawable.color_gray, R.raw.color_gray));
        words.add(new Word("black", "kululli", R.drawable.color_black, R.raw.color_black));
        words.add(new Word("white", "kelelli", R.drawable.color_white, R.raw.color_white));
        words.add(new Word("dusty yellow", "ṭopiisә", R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        words.add(new Word("mustard yellow", "chiwiiṭә", R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));
        WordAdapter itemsAdapter = new WordAdapter(this, words, R.color.category_colors);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            releaseMediaPlayer();
            Word word = words.get(position);
            mediaPlayer = MediaPlayer.create(ColorsActivity.this, word.getmAudioResourceId());
            int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(completionListener);
            } else {
                Toast.makeText(ColorsActivity.this, "Could not play audio", Toast.LENGTH_SHORT);
            }
        });
    }

    private void releaseMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
    }

    @Override
    protected void onStop() {
        releaseMediaPlayer();
        super.onStop();
    }

    private Handler handler = new Handler();
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Permanent loss of audio focus
                // Pause playback immediately
                mediaPlayer.pause();
                // Wait 30 seconds before stopping playback
                handler.postDelayed(releaseMediaPlayerRunnable,
                        TimeUnit.SECONDS.toMillis(30));
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                mediaPlayer.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume, keep playing
                mediaPlayer.setVolume(0.5f, 0.5f);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                mediaPlayer.setVolume(1f, 1f);
                mediaPlayer.start();
            }
        }
    };

    Runnable releaseMediaPlayerRunnable = new Runnable() {
        @Override
        public void run() {
            releaseMediaPlayer();
        }
    };
}