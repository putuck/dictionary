package com.example.rajiv.dictionary;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.example.rajiv.dictionary.definition.ResponseData;
import com.example.rajiv.dictionary.definition.Entry;
import com.example.rajiv.dictionary.definition.Sense;
import com.example.rajiv.dictionary.lemma.InflectionOf;
import com.example.rajiv.dictionary.lemma.Lemmatron;
import com.example.rajiv.dictionary.lemma.LexicalEntry;
import com.example.rajiv.dictionary.lemma.Result;
import com.example.rajiv.dictionary.pronunciation.Data;
import com.example.rajiv.dictionary.pronunciation.Pronunciation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.example.rajiv.dictionary.RetrofitInstance.getRetrofitInstance;

public class WordData {
    private MutableLiveData<List<List<String>>> meanings;
    private  OxfordApiService service;


    public WordData(){
        meanings = new MutableLiveData<>();
        List<List<String>> lists = new ArrayList<>();
        meanings.setValue(lists);


    }

    private MutableLiveData<String> getLemma(String word){
        Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
        OxfordApiService service = retrofit.create(OxfordApiService.class);

        final MutableLiveData<String> headWord = new MutableLiveData<>();

        Call<Lemmatron> result = service.getLemma(word);
        result.enqueue(new Callback<Lemmatron>(){
            @Override
            public void onResponse(Call<Lemmatron> call,
                                   retrofit2.Response<Lemmatron> response) {
                if (response.isSuccessful()) {
                    Lemmatron lemmatron = response.body();
                    List<Result> result = lemmatron.getResults();
                    List<LexicalEntry> lexicalEntry = result.get(0).getLexicalEntries();
                    List<InflectionOf> inflectionOf = lexicalEntry.get(0).getInflectionOf();
                    String lemma = inflectionOf.get(0).getText();
                    headWord.setValue(lemma);
                }
                else{
                    headWord.setValue("");
                }
            }

            public void onFailure(Call<Lemmatron> call, Throwable t){

            }
        });
        return headWord;
    }

    public MutableLiveData<List<List<String>>> getMeaning(String word){
        MutableLiveData<String> headWord = getLemma(word);
        headWord.observeForever(new Observer<String>() {
            @Override
            public void onChanged(@Nullable String lemma) {
                if (lemma != null) {
                    if (lemma.equals("")) {
                        meanings.setValue(null);

                    } else {

                        Retrofit retrofit = (new RetrofitInstance()).getRetrofitInstance();
                        OxfordApiService service = retrofit.create(OxfordApiService.class);

                        Call<ResponseData> result = service.getMeaning(lemma);
                        result.enqueue(new Callback<ResponseData>() {
                            @Override
                            public void onResponse(Call<ResponseData> call, retrofit2.Response<ResponseData> response) {
                                List<com.example.rajiv.dictionary.definition.Result> results;
                                List<com.example.rajiv.dictionary.definition.LexicalEntry> lexicalEntries =
                                        new ArrayList<>();
                                List<Entry> entries = new ArrayList<>();
                                List<Sense> senses = new ArrayList<>();
                                List<String> definitions = new ArrayList<>();
                                List<String> nounDefinitions = new ArrayList<>();
                                List<String> verbDefinitions = new ArrayList<>();
                                List<String> adjectiveDefinitions = new ArrayList<>();


                                ResponseData responseData = response.body();
                                results = responseData.getResults();

                                // Create LexicalEntry List
                                int resultLength = results.size();
                                for (int i = 0; i < resultLength; i++) {
                                    int lexicalSize = results.get(i).getLexicalEntries().size();
                                    for (int j = 0; j < lexicalSize; j++) {
                                        lexicalEntries.add(results.get(i).getLexicalEntries().get(j));
                                    }
                                }


                                for (com.example.rajiv.dictionary.definition.LexicalEntry l : lexicalEntries) {
                                    entries = l.getEntries();
                                    for (Entry e : entries) {
                                        senses = e.getSenses();
                                        for (Sense s : senses) {
                                            definitions = s.getDefinitions();
                                            String lexicalCategory = l.getLexicalCategory();
                                            for (String d : definitions) {
                                                if (lexicalCategory.equals("Noun") && d!=null) {
                                                    //for (String n : definitions)
                                                        nounDefinitions.add(d);
                                                }
                                                if (lexicalCategory.equals("Verb")) {
                                                    //for (String v : definitions)
                                                        verbDefinitions.add(d);
                                                }
                                                if (lexicalCategory.equals("Adjective")) {
                                                    //for (String a : definitions)
                                                        adjectiveDefinitions.add(d);
                                                }
                                            }
                                        }
                                    }
                                }

                                List<List<String>> meaning = new ArrayList<>();
                                meaning.add(nounDefinitions);
                                meaning.add(adjectiveDefinitions);
                                meaning.add(verbDefinitions);

                                meanings.setValue(meaning);
                            }

                            @Override
                            public void onFailure(Call<ResponseData> call, Throwable t) {


                            }
                        });
                    }

                }
            }
        });

        return meanings;

    }

    public MutableLiveData<String> getPronunciation(String word){
       Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
       OxfordApiService service = retrofit.create(OxfordApiService.class);

       final MutableLiveData<String> pronuciation = new MutableLiveData<>();

       Call<Data> result = service.getPronunciation(word);
       result.enqueue(new Callback<Data>(){
           @Override
           public void onResponse(Call<Data> call,
                                  retrofit2.Response<Data> response) {
               if (response.isSuccessful()) {
                   Data data = response.body();
                   List<com.example.rajiv.dictionary.pronunciation.Result> result = data.getResults();
                   List<com.example.rajiv.dictionary.pronunciation.LexicalEntry> lexicalEntry = result.get(0).getLexicalEntries();
                   List<Pronunciation> pronunciations = lexicalEntry.get(0).getPronunciations();
                   String audioURL = pronunciations.get(0).getAudioFile();
                   pronuciation.setValue(audioURL);
               }
               else{
                   pronuciation.setValue("");
               }
           }

           public void onFailure(Call<Data> call, Throwable t){

           }
       });
       return pronuciation;


   }
}
