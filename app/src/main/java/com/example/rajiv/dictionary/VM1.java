package com.example.rajiv.dictionary;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;


public class VM1 extends ViewModel {
    WordData wordData;

    public MutableLiveData<ArrayList<DefWithCategory>> getMeaning(String word){
        wordData = new WordData();
        return wordData.getMeaning(word);
    }

    public MutableLiveData<String> getPronunciation(String word){
        return wordData.getPronunciation(word);
    }
}
