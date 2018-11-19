package com.example.rajiv.dictionary

import android.app.ActivityOptions
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import java.io.IOException
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private var model: VM1? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var textWord: EditText
    private var btnPronounce: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inside your activity (if you did not enable transitions in your theme)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            // set an exit transition
            val fade: Fade = Fade(Fade.IN)
            fade.duration = 2000
            exitTransition = fade
            //enterTransition = fade
        }

        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        textWord = findViewById(R.id.textWord)
        btnPronounce = findViewById(R.id.pronounceBtn)
        model = ViewModelProviders.of(this).get(VM1::class.java)

    }

    fun findMeaning(view: View) {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)

        val word = textWord!!.text.toString().toLowerCase()
        progressBar!!.visibility = View.VISIBLE

        val definition = model!!.getMeaning(word)
        definition.observe(this, Observer {
            it?.let{
                intent = Intent(this, Main2Activity::class.java)
                intent.apply { putParcelableArrayListExtra("def", it )
                putExtra("word", word.capitalize())
                }
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                progressBar.visibility = View.GONE
                textWord.text = null
            }
        })
    }
}



 /*   fun more(view: View) {
        //CardView parent = (CardView) view.getParent().getParent();
        val parent = view.parent as LinearLayout
        val vPosition = parent.indexOfChild(view)
        val meaningTV = (view.parent as LinearLayout).getChildAt(vPosition - 1) as TextView
        meaningTV.maxLines = Integer.MAX_VALUE

        val v = view as TextView
        v.text = "less"
        v.setOnClickListener { v -> less(v) }
    }

    fun less(view: View) {
        val parent = view.parent as LinearLayout
        val vPosition = parent.indexOfChild(view)
        val meaningTV = (view.parent as LinearLayout).getChildAt(vPosition - 1) as TextView
        meaningTV.maxLines = 3

        val v = view as TextView
        v.text = "more"
        v.setOnClickListener { v -> more(v) }


    }


    fun pronounce(view: View) {
        val word = textWord!!.text.toString()
        val audioURL = model!!.getPronunciation(word)
        audioURL.observe(this, Observer { s ->
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaPlayer.setDataSource(s)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (E: IOException) {
            }
        })


    }

    fun dpToPx(dp: Int): Int {
        val density = resources
                .displayMetrics
                .density
        return Math.round(dp.toFloat() * density)
    }

} */
