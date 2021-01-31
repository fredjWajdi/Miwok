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

public class NumbersActivity extends AppCompatActivity {

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
        words.add(new Word("One", "lutti", R.drawable.number_one, R.raw.number_one));
        words.add(new Word("Two", "otiiko", R.drawable.number_two, R.raw.number_two));
        words.add(new Word("Three", "tolookosu", R.drawable.number_three, R.raw.number_three));
        words.add(new Word("Four", "oyyisa", R.drawable.number_four, R.raw.number_four));
        words.add(new Word("Five", "massokka", R.drawable.number_five, R.raw.number_five));
        words.add(new Word("Six", "temmokka", R.drawable.number_six, R.raw.number_six));
        words.add(new Word("Seven", "kenekaku", R.drawable.number_seven, R.raw.number_seven));
        words.add(new Word("Eight", "kawinta", R.drawable.number_eight, R.raw.number_eight));
        words.add(new Word("Nine", "wo’e", R.drawable.number_nine, R.raw.number_nine));
        words.add(new Word("Ten", "na’aacha", R.drawable.number_ten, R.raw.number_ten));
        WordAdapter itemsAdapter = new WordAdapter(this, words, R.color.category_numbers);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            releaseMediaPlayer();
            Word word = words.get(position);
            mediaPlayer = MediaPlayer.create(NumbersActivity.this, word.getmAudioResourceId());
            int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(completionListener);
            } else {
                Toast.makeText(NumbersActivity.this, "Could not play audio", Toast.LENGTH_SHORT);
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
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Pause playback
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
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