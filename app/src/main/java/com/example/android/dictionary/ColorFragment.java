package com.example.android.dictionary;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ColorFragment extends Fragment {

    private MediaPlayer mMediaPlayer;

    // handles audio focus when playing a sound file
    private AudioManager mAudioManager;

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // the AUDIOFOCUS_LOSS_TRANSIENT means lost audio focus for a short amount
                        // of time. AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK means our app
                        // is allowed playing sound but at a lower volume.
                        // Pause playback and reset player to the start of the file
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Resume playback
                        mMediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // lost audio focus, stop playback and clean up resources
                        releaseMediaPlayer();
                    }
                }
            };

    //listener gets triggered when MediaPlayer has completed playing audio file
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };
    public ColorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.words_list, container, false);

        //create and setup AudioManager to request audio focus
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Word> words = new ArrayList<>();

        // words.add("one");
        words.add(new Word("red", "rot", R.drawable.color_red,
                R.raw.color_red));
        words.add(new Word("green", "grün", R.drawable.color_green,
                R.raw.color_green));
        words.add(new Word("brown", "braun", R.drawable.color_brown,
                R.raw.color_brown));
        words.add(new Word("gray", "grau", R.drawable.color_gray,
                R.raw.color_gray));
        words.add(new Word("black", "schwarz", R.drawable.color_black,
                R.raw.color_black));
        words.add(new Word("white", "weiß", R.drawable.color_white,
                R.raw.color_white));
        words.add(new Word("dusty yellow", "staubiges Gelb",
                R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        words.add(new Word("mustard yellow", "Senfgelb",
                R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));

        //  Custom word adapter
        WordAdapter adapter = new WordAdapter(getActivity(), words, R.color.category_colors);

        ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get position of word and store it into variable
                Word word = words.get(position);
                // release the media player if it currently exists because it will play a different
                //sound file
                releaseMediaPlayer();

                // Request audio focus for playback
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // Start playback
                    // get the resource ID for word
                    mMediaPlayer = MediaPlayer.create(getActivity(), word.getAudioResourceId());
                    mMediaPlayer.start();

                    // setup a listener on the media player, so it stops and releases the media player
                    // once the sound is finished playing
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });

        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
        // When the activity is stopped, release the media player resources because it won't
        // be playing any more sounds.
        releaseMediaPlayer();
    }

   //clean up the media player by releasing its resources

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because it no longer needs it.
            mMediaPlayer.release();

            // Media player is not configured to play an audio file at the moment
            mMediaPlayer = null;

            // Whether or not we get audio focus, abandon it. Unregister AudioFocusChangeListener
            // so we don't get callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }
}
