package com.example.rajiv.dictionary

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel


import com.example.rajiv.dictionary.pronunciation.Data
import com.example.rajiv.dictionary.pronunciation.Pronunciation

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit

import com.example.rajiv.dictionary.RetrofitInstance.getRetrofitInstance
import com.example.rajiv.dictionary.definition.*
import com.example.rajiv.dictionary.lemma.GrammaticalFeature
import com.example.rajiv.dictionary.lemma.InflectionOf
import com.example.rajiv.dictionary.lemma.Lemmatron
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class WordData: ViewModel() {
    private var word: String? = null
    private var wordFullData: List<Result>? = null

    private lateinit var shortDef: MutableLiveData<ArrayList<String>>
    private lateinit var detailDef: MutableLiveData<ArrayList<DefWithCategory>>
    private lateinit var pronounceFile: File

    private val meanings: MutableLiveData<ArrayList<DefWithCategory>> = MutableLiveData()
    val retrofit = RetrofitInstance.getRetrofitInstance()
    val service: OxfordApiService? = retrofit.create(OxfordApiService::class.java)

    fun getWordFullData(inputWord: String) {
        val query = service!!.getFullWordData(inputWord)
        query.enqueue(object: Callback<ResponseData>{
            override fun onResponse(call: Call<ResponseData>?, response: retrofit2.Response<ResponseData>) {
                if(response.isSuccessful){
                    val responseData = response.body()
                    wordFullData = responseData!!.results
                }
                else{

                }
            }

            override fun onFailure(call: Call<ResponseData>?, t: Throwable?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun getShortDef(inputWord: String): MutableLiveData<ArrayList<String>> {
        if (inputWord.equals(word))
            return shortDef
        else{
            getWordFullData(inputWord)
        }
    }

    fun getMeaning(word: String): MutableLiveData<ArrayList<DefWithCategory>> {
                    val result = service!!.getFullWordData(word)
                    result?.enqueue(object : Callback<ResponseData> {
                        override fun onResponse(call: Call<ResponseData>, response: retrofit2.Response<ResponseData>) {
                            val results: List<Result>
                            val lexicalEntries = ArrayList<LexicalEntry>()
                            var entries: List<Entry> = ArrayList()
                            var senses: List<Sense> = ArrayList()
                            var definitions = ArrayList<DefWithCategory>()
                            val nounDefinitions = ArrayList<String>()
                            val verbDefinitions = ArrayList<String>()
                            val adjectiveDefinitions = ArrayList<String>()

                            if(response.isSuccessful) {
                                val responseData = response.body()

                                results = responseData!!.results
                                wordFullData = results

                                // Create LexicalEntry List
                                for (res in results) {
                                    res?.let {
                                        for (lex in it.lexicalEntries) {
                                            lex?.let { lexicalEntries.add(it) }
                                        }
                                    }
                                }

                                for (l in lexicalEntries) {
                                    for (e in l.entries) {
                                        e?.let {
                                            for (s in it.senses) {
                                                val definition = (s.definitions)
                                                        ?: s.crossReferenceMarkers
                                                definition?.let {
                                                    for (def in definition) {
                                                        val defWithCategory = DefWithCategory(def.capitalize()+".\n\n", l.lexicalCategory.capitalize())
                                                        definitions.add(defWithCategory)
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                                val condensedDef = createObjList(definitions)
                                meanings.value = condensedDef
                            }
                            else{
                                val result = service!!.getLemma(word)
                                result?.enqueue(object: Callback<Lemmatron> {
                                    override fun onResponse(call: Call<Lemmatron>, response: Response<Lemmatron>){
                                        val results: List<com.example.rajiv.dictionary.lemma.Result>
                                        val lexical: com.example.rajiv.dictionary.lemma.LexicalEntry
                                        val grammar: GrammaticalFeature
                                        val inflection: InflectionOf

                                        if(response.isSuccessful){
                                            val responseData = response.body()
                                            results = responseData!!.results
                                            lexical = results!!.get(0).lexicalEntries[0]
                                            grammar = lexical.grammaticalFeatures[0]
                                            inflection = lexical.inflectionOf[0]

                                            val definition: String = grammar.text + " of " + inflection.text + ".\n\n"
                                            val cat: String = lexical.lexicalCategory
                                            val def = DefWithCategory(definition, cat)
                                            //val definitions = ArrayList<DefWithCategory>()
                                            definitions.add(def)
                                            val condensedDef = createObjList(definitions)
                                            meanings.value = condensedDef


                                        }
                                        else{
                                            definitions.add(DefWithCategory("Please check the spelling of the word entered.", "Error"))
                                            meanings.value = definitions
                                        }
                                    }

                                    override fun onFailure(call: Call<Lemmatron>?, t: Throwable?) {

                                    }
                                })
                            }


                        }

                        override fun onFailure(call: Call<ResponseData>, t: Throwable) {

                        }
                    })

        return meanings

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

    fun createObjList(list: ArrayList<DefWithCategory>): ArrayList<DefWithCategory>{
        val defList = ArrayList<DefWithCategory>()
        defList.add(list[0])

        loop@ for(l in list.subList(1, list.size)){
            for(def in defList){
                if(def.category == l.category) {
                    def.def +=  l.def
                    continue@loop
                }
            }
            defList.add(DefWithCategory(l.def, l.category))
        }
        return defList
    }
}
