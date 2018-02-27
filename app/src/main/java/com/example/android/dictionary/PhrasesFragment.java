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

public class PhrasesFragment extends Fragment {

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

    public PhrasesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.words_list, container, false);

        //create and setup AudioManager to request audio focus
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Word> words = new ArrayList<>();

        words.add(new Word("Hello. My name is...", "Hallo. Mein Name ist...",
                R.raw.phrase_hello_my_name_is));
        words.add(new Word("Where are you from?", "Wo kommen Sie her?",
                R.raw.phrase_where_are_you_from));
        words.add(new Word("Nice to meet you.", "Freut mich.",
                R.raw.phrase_nice_to_meet_you));
        words.add(new Word("How are you?", "Wie geht es dir?",
                R.raw.phrase_how_are_you));
        words.add(new Word("What do you do for a living?", "Was sind Sie von Beruf?",
                R.raw.phrase_what_do_you_do_for_a_living));
        words.add(new Word("How old are you?", "Wie alt sind Sie?",
                R.raw.phrase_how_old_are_you));
        words.add(new Word("I don't understand.", "Ich verstehe nicht.",
                R.raw.phrase_i_dont_understand));
        words.add(new Word("Please", "Bitte",
                R.raw.phrase_please));
        words.add(new Word("Excuse me", "Entschuldigen Sie mich",
                R.raw.phrase_excuse_me));
        words.add(new Word("Thank you", "Danke",
                R.raw.phrase_thank_you));

        //  Custom word adapter; ArrayAdapter<Word> itemsAdapter
        WordAdapter adapter = new WordAdapter(getActivity(), words, R.color.category_phrases);

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
                    mMediaPlayer = MediaPlayer.create(getContext(), word.getAudioResourceId());
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
