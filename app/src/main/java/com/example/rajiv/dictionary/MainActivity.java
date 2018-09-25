package com.example.rajiv.dictionary;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private VM1 model;
    private TextView textMeaning;
    private ProgressBar progressBar;
    private EditText textWord;
    private ImageButton btnPronounce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textMeaning = findViewById(R.id.textMeaning);
        textMeaning.setMovementMethod(new ScrollingMovementMethod());
        progressBar = findViewById(R.id.progressBar);
        textWord = findViewById(R.id.textWord);
        btnPronounce = findViewById(R.id.pronounceBtn);
        model = ViewModelProviders.of(this).get(VM1.class);


        textWord.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")) {
                    textMeaning.setVisibility(View.GONE);
                    btnPronounce.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });




    }


     public void findMeaning(View view) {
         String word = (textWord.getText()).toString().toLowerCase();
         textMeaning.setVisibility(View.GONE);
         progressBar.setVisibility(View.VISIBLE);
         btnPronounce.setVisibility(View.GONE);
         LiveData<String> definition = model.getMeaning(word);
         definition.observe(this, new Observer<String>() {
             @Override
             public void onChanged(@Nullable String s) {
                 if(!s.equals("")) {

                     progressBar.setVisibility(View.GONE);
                     textMeaning.setVisibility(View.VISIBLE);
                     textMeaning.setText(s);
                 }

             }
         });


     }

   /*  public void pronounce(View view){
         int index;
         List<Result> result = dicData.getResults();
         List<LexicalEntry> lexicalEntry = result.get(0).getLexicalEntries();

         index = lexicalEntry.size()-1;

         List<Pronunciation> pronounciation = lexicalEntry.get(index).getPronunciations();
         String audioURL = pronounciation.get(0).getAudioFile();

         MediaPlayer mediaPlayer = new MediaPlayer();
         mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
         try {
             mediaPlayer.setDataSource(audioURL);
             mediaPlayer.prepare();
             mediaPlayer.start();
         }catch (IOException E){};

     } */

}
