package com.example.android.dictionary;


public class Word {

    //default translation for the word

    private String mDefaultTranslation;

   //german translation for the word

    private String mGermanTranslation;

    // variable for image resource ID, initial value -1, when it changes = word has an image

    private int mImageResourceId = NO_IMAGE_PROVIDED;

    // create a constant that represents the no image state

    private static final int NO_IMAGE_PROVIDED = -1;

    // variable for audio resource ID

    private int mAudioResourceId;

    //new Word object

    public Word(String defaultTranslation, String germanTranslation, int audioResourceId) {
        mDefaultTranslation = defaultTranslation;
        mGermanTranslation = germanTranslation;
        mAudioResourceId = audioResourceId;
    }

    // second constructor for 2 textviews, 1 image view and 1 audio file

    public Word(String defaultTranslation, String germanTranslation, int imageResourceId, int audioResourceId) {
        mDefaultTranslation = defaultTranslation;
        mGermanTranslation = germanTranslation;
        mImageResourceId = imageResourceId;
        mAudioResourceId = audioResourceId;

    }

    public String getDefaultTranslation() {
        return mDefaultTranslation;
    }

    public String getGermanTranslation() {
        return mGermanTranslation;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    // method for checking if a word is associated with an image; not equal to -1
    public boolean hasImage(){
    return mImageResourceId != NO_IMAGE_PROVIDED;
    }

    public int getAudioResourceId () {return mAudioResourceId;}

    }



