package com.example.rajiv.dictionary;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;


public class VM1 extends ViewModel {
    WordData wordData;

    public MutableLiveData<String> getMeaning(String word){
        wordData = new WordData();
        return wordData.getMeaning(word);
    }

}
