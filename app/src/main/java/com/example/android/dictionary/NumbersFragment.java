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

public class NumbersFragment extends Fragment {

    //handles playback of all the sound files
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

    public NumbersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.words_list, container, false);

        //create and setup AudioManager to request audio focus
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);


        final ArrayList<Word> words = new ArrayList<>();

        // words.add("one");
        words.add(new Word("one", "eins", R.drawable.number_one,
                R.raw.number_one));
        words.add(new Word("two", "zwei", R.drawable.number_two,
                R.raw.number_two));
        words.add(new Word("three", "drei", R.drawable.number_three,
                R.raw.number_three));
        words.add(new Word("four", "vier", R.drawable.number_four,
                R.raw.number_four));
        words.add(new Word("five", "fünf", R.drawable.number_five,
                R.raw.number_five));
        words.add(new Word("six", "sechs", R.drawable.number_six,
                R.raw.number_six));
        words.add(new Word("seven", "sieben", R.drawable.number_seven,
                R.raw.number_seven));
        words.add(new Word("eight", "acht", R.drawable.number_eight,
                R.raw.number_eight));
        words.add(new Word("nine", "neun", R.drawable.number_nine,
                R.raw.number_nine));
        words.add(new Word("ten", "zehn", R.drawable.number_ten,
                R.raw.number_ten));

        //  Custom word adapter
        WordAdapter adapter = new WordAdapter(getActivity(), words, R.color.category_numbers);

        ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get position of word and store it into variable
                Word word = words.get(position);

                // release the media player it it currently exists because it will play a different
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

        // When the activity is stopped, release the media player resources because we won't
        // be playing any more sounds.
        releaseMediaPlayer();
    }

    //clean up the media player by releasing its resources

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
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
