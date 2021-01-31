package com.wizzapps.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FamilyActivity extends AppCompatActivity {
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
        words.add(new Word("father", "әpә", R.drawable.family_father, R.raw.family_father));
        words.add(new Word("mother", "әṭa", R.drawable.family_mother, R.raw.family_mother));
        words.add(new Word("son", "angsi", R.drawable.family_son, R.raw.family_son));
        words.add(new Word("daughter", "tune", R.drawable.family_daughter, R.raw.family_daughter));
        words.add(new Word("older brother", "taachi", R.drawable.family_older_brother, R.raw.family_older_brother));
        words.add(new Word("younger brother", "chalitti", R.drawable.family_younger_brother, R.raw.family_younger_brother));
        words.add(new Word("older sister", "teṭe", R.drawable.family_older_sister, R.raw.family_older_sister));
        words.add(new Word("younger sister", "kolliti", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        words.add(new Word("grandmother", "ama", R.drawable.family_grandmother, R.raw.family_grandmother));
        words.add(new Word("grandfather", "paapa", R.drawable.family_grandfather, R.raw.family_grandfather));
        WordAdapter itemsAdapter = new WordAdapter(this, words, R.color.category_family);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            releaseMediaPlayer();
            Word word = words.get(position);
            mediaPlayer = MediaPlayer.create(FamilyActivity.this, word.getmAudioResourceId());
            int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(completionListener);
            } else {
                Toast.makeText(FamilyActivity.this, "Could not play audio", Toast.LENGTH_SHORT);
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