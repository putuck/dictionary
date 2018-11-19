package com.example.rajiv.dictionary

import android.app.Fragment
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatImageButton
import android.transition.Explode
import android.transition.Fade
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.rajiv.dictionary.pronunciation.Data
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException

class Main2Activity : AppCompatActivity() {
        lateinit var tvWord: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                with(window) {
                        requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

                        // set an exit transition
                        val explode: Explode = Explode()
                        explode.duration = 500
                        exitTransition = Fade()
                        enterTransition = explode
                }
                setContentView(R.layout.activity_main2)
                val intent: Intent = getIntent()
                val def: ArrayList<DefWithCategory> = intent.getParcelableArrayListExtra("def")
                val word: String = intent.getStringExtra("word")
                tvWord = findViewById(R.id.textViewWord)
                tvWord.text = word
                val bundle = Bundle()
                bundle.putParcelableArrayList("data", def)

                val fragmentManager = supportFragmentManager
                val fragment = DefWithCategoryFragment()
                fragment.arguments = bundle
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.fragmentContainer, fragment)
                fragmentTransaction.commit()

    }

        fun pronounce(view: View) {
                //val viewId: Int = view.id
                val btn: AppCompatImageButton = view as AppCompatImageButton
                btn.elevation = 2f
                btn.setEnabled(false)
                val word = tvWord.text.toString().toLowerCase()
                val audioURL = getPronunciation(word)
                audioURL.observe(this, Observer { s ->
                        val mediaPlayer = MediaPlayer()
                        try {
                                mediaPlayer.setDataSource(s)
                                mediaPlayer.prepare()
                                mediaPlayer.start()
                        } catch (E: IOException) {
                        }
                        btn.setEnabled(true)
                        btn.elevation = 8f
                })


        }

        fun getPronunciation(word: String): MutableLiveData<String> {
                val retrofit = RetrofitInstance.getRetrofitInstance()
                val service = retrofit.create(OxfordApiService::class.java)

                val pronuciation = MutableLiveData<String>()

                val result = service.getPronunciation(word)
                result.enqueue(object : Callback<Data> {
                        override fun onResponse(call: Call<Data>,
                                                response: retrofit2.Response<Data>) {
                                if (response.isSuccessful) {
                                        val data = response.body()
                                        val result = data!!.results
                                        val lexicalEntry = result[0].lexicalEntries
                                        val pronunciations = lexicalEntry[0].pronunciations
                                        val audioURL = pronunciations[0].audioFile
                                        pronuciation.setValue(audioURL)
                                } else {
                                        pronuciation.setValue("")
                                }
                        }

                        override fun onFailure(call: Call<Data>, t: Throwable) {

                        }
                })
                return pronuciation


        }
}
