package com.example.rajiv.dictionary;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.example.rajiv.dictionary.definition.Definition;
import com.example.rajiv.dictionary.definition.Entry;
import com.example.rajiv.dictionary.definition.Sense;
import com.example.rajiv.dictionary.lemma.InflectionOf;
import com.example.rajiv.dictionary.lemma.Lemmatron;
import com.example.rajiv.dictionary.lemma.LexicalEntry;
import com.example.rajiv.dictionary.lemma.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WordData {
    private MutableLiveData<String> wordMeaning;
    private  OxfordApiService service;


    public WordData(){
        wordMeaning = new MutableLiveData<String>();
        wordMeaning.setValue("");
    }

    private MutableLiveData<String> getLemma(String word){
        Retrofit retrofit = (new RetrofitInstance()).getRetrofitInstance();
        OxfordApiService service = retrofit.create(OxfordApiService.class);

        final MutableLiveData<String> headWord = new MutableLiveData<String>();
        Call<Lemmatron> result = service.getLemma(word);
        result.enqueue(new Callback<Lemmatron>(){
            @Override
            public void onResponse(Call<Lemmatron> call,
                                   Response<Lemmatron> response){
                Lemmatron lemmatron = response.body();
                List<Result> result = lemmatron.getResults();
                List<LexicalEntry> lexicalEntry = result.get(0).getLexicalEntries();
                List<InflectionOf> inflectionOf = lexicalEntry.get(0).getInflectionOf();
                String lemma = inflectionOf.get(0).getText();
                headWord.setValue(lemma);
            }

            public void onFailure(Call<Lemmatron> call, Throwable t){

            }
        });
        return headWord;
    }

    public MutableLiveData<String> getMeaning(String word){
        MutableLiveData<String> headWord = getLemma(word);
        headWord.observeForever(new Observer<String>() {
            @Override
            public void onChanged(@Nullable String lemma) {
                Retrofit retrofit = (new RetrofitInstance()).getRetrofitInstance();
                OxfordApiService service = retrofit.create(OxfordApiService.class);

                Call<Definition> result = service.getMeaning(lemma);
                result.enqueue(new Callback<Definition> () {
                    @Override
                    public void onResponse(Call<Definition> call, Response<Definition> response) {
                        List<com.example.rajiv.dictionary.definition.Result> results;
                        List<com.example.rajiv.dictionary.definition.LexicalEntry> lexicalEntries =
                                new ArrayList<>();
                        List<Entry> entries = new ArrayList<>();
                        List<Sense> senses = new ArrayList<>();
                        List<String> definitions = new ArrayList<>();
                        String meaning = "- ";

                        Definition definition = response.body();
                        results = definition.getResults();

                        // Create LexicalEntry List
                        int resultLength = results.size();
                        for (int i=0;i<resultLength;i++){
                            int lexicalSize = results.get(i).getLexicalEntries().size();
                            for (int j=0;j<lexicalSize;j++){
                                lexicalEntries.add(results.get(i).getLexicalEntries().get(j));
                            }
                        }

                        // Create Entry List
                        int lexicalLength = lexicalEntries.size();
                        for (int i=0; i<lexicalLength; i++){
                            int entrySize = lexicalEntries.get(i).getEntries().size();
                            for (int j=0; j<entrySize; j++)
                                entries.add(lexicalEntries.get(i).getEntries().get(j));
                        }

                        //Create Sense list
                        int  entryLength = entries.size();
                        for (int i=0; i<entryLength; i++){
                            int senseSize = entries.get(i).getSenses().size();
                            for (int j=0; j<senseSize; j++)
                                senses.add(entries.get(i).getSenses().get(j));
                        }

                        // Create Definition List
                        int senseLength = senses.size();
                        for (int i=0; i<senseLength; i++){
                            int definitionSize = senses.get(i).getDefinitions().size();
                            for (int j=0; j<definitionSize; j++)
                                definitions.add(senses.get(i).getDefinitions().get(j));
                        }

                        // Create meaning string
                        for (int i=0; i<definitions.size(); i++){
                            meaning = meaning+definitions.get(i)+"\n\n- ";
                        }

                        wordMeaning.setValue(meaning);
                    }

                    @Override
                    public void onFailure(Call<Definition> call, Throwable t) {

                    }
                });

            }
        });

        return wordMeaning;

    }

   // public String getPronounciation(){ }
}
