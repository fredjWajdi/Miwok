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

public class PhrasesActivity extends AppCompatActivity {
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
        words.add(new Word("Where are you going?", "minto wuksus", R.raw.phrase_where_are_you_going));
        words.add(new Word("What is your name?", "tinnә oyaase'nә", R.raw.phrase_what_is_your_name));
        words.add(new Word("My name is...", "oyaaset...", R.raw.phrase_my_name_is));
        words.add(new Word("How are you feeling?", "michәksәs?", R.raw.phrase_how_are_you_feeling));
        words.add(new Word("I’m feeling good.", "kuchi achit", R.raw.phrase_im_feeling_good));
        words.add(new Word("Are you coming?", "әәnәs'aa?", R.raw.phrase_are_you_coming));
        words.add(new Word("Yes, I’m coming.", "hәә’ әәnәm", R.raw.phrase_yes_im_coming));
        words.add(new Word("I’m coming.", "әәnәm", R.raw.phrase_im_coming));
        words.add(new Word("Let’s go.", "yoowutis", R.raw.phrase_lets_go));
        words.add(new Word("Come here.", "әnni'nem", R.raw.phrase_come_here));
        WordAdapter itemsAdapter = new WordAdapter(this, words, R.color.category_phrases);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            releaseMediaPlayer();
            Word word = words.get(position);
            mediaPlayer = MediaPlayer.create(PhrasesActivity.this, word.getmAudioResourceId());
            int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(completionListener);
            } else {
                Toast.makeText(PhrasesActivity.this, "Could not play audio", Toast.LENGTH_SHORT);
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