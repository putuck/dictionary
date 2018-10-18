package com.example.rajiv.dictionary

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

import com.example.rajiv.dictionary.definition.ResponseData
import com.example.rajiv.dictionary.definition.Entry
import com.example.rajiv.dictionary.definition.Sense
import com.example.rajiv.dictionary.lemma.InflectionOf
import com.example.rajiv.dictionary.lemma.Lemmatron
import com.example.rajiv.dictionary.lemma.LexicalEntry
import com.example.rajiv.dictionary.lemma.Result
import com.example.rajiv.dictionary.pronunciation.Data
import com.example.rajiv.dictionary.pronunciation.Pronunciation

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit

import com.example.rajiv.dictionary.RetrofitInstance.getRetrofitInstance
import java.util.*

class WordData {
    private val meanings: MutableLiveData<List<List<String>>>
    val retrofit = RetrofitInstance.getRetrofitInstance()
    val service: OxfordApiService? = retrofit.create(OxfordApiService::class.java)

    init {
        meanings = MutableLiveData()
        val lists = ArrayList<List<String>>()
        meanings.value = lists


    }

    private fun getLemma(word: String): MutableLiveData<MutableList<String?>>? {


        val headWord: MutableLiveData<MutableList<String?>> = MutableLiveData()

        val query = service?.getLemma(word)
        query?.enqueue(object : Callback<Lemmatron> {
            override fun onResponse(call: Call<Lemmatron>,
                                    response: retrofit2.Response<Lemmatron>) {
                if (response.isSuccessful) {
                    val lemmatron = response.body()
                    val result = (lemmatron?.results)!!.filterNotNull()
                    val lexicalEntry: MutableList<LexicalEntry?> = mutableListOf<LexicalEntry?>()
                    for(res in result!! ) {
                        for (lex in res.lexicalEntries!!)
                             lexicalEntry.add(lex)
                    }
                    val inflectionOf: MutableList<InflectionOf?> = mutableListOf<InflectionOf?>()
                    for(lex in lexicalEntry) {
                        lex?.let {
                            for(inf in it.inflectionOf)
                                inf?.let { inflectionOf.add(it)}
                        }
                    }

                    val lemma: MutableList<String?> = mutableListOf<String?>()
                    for(inf in inflectionOf) {
                           inf?.let { lemma.add(inf.text) }
                    }
                    headWord?.setValue(lemma)
                } else {
                    headWord?.setValue(mutableListOf<String?>(""))
                }
            }

            override fun onFailure(call: Call<Lemmatron>, t: Throwable) {

            }
        })
        return headWord
    }

    fun getMeaning(word: String): MutableLiveData<List<List<String>>> {
        val headWord = getLemma(word)
        headWord?.observeForever { lemmaList ->
            if (lemmaList != null) {
                if (lemmaList.get(0) == "") {
                    meanings.setValue(null)

                } else {
                    val lemma: String = lemmaList.filterNotNull().get(0)
                    val result = service?.getMeaning(lemma)
                    result?.enqueue(object : Callback<ResponseData> {
                        override fun onResponse(call: Call<ResponseData>, response: retrofit2.Response<ResponseData>) {
                            val results: List<com.example.rajiv.dictionary.definition.Result>
                            val lexicalEntries = ArrayList<com.example.rajiv.dictionary.definition.LexicalEntry>()
                            var entries: List<Entry> = ArrayList()
                            var senses: List<Sense> = ArrayList()
                            var definitions: List<String> = ArrayList()
                            val nounDefinitions = ArrayList<String>()
                            val verbDefinitions = ArrayList<String>()
                            val adjectiveDefinitions = ArrayList<String>()


                            val responseData = response.body()
                            results = responseData!!.results

                            // Create LexicalEntry List
                            val resultLength = results.size
                            for (i in 0 until resultLength) {
                                val lexicalSize = results[i].lexicalEntries.size
                                for (j in 0 until lexicalSize) {
                                    lexicalEntries.add(results[i].lexicalEntries[j])
                                }
                            }


                            for (l in lexicalEntries) {
                                entries = l.entries
                                for (e in entries) {
                                    senses = e.senses
                                    for (s in senses) {
                                        definitions = s.definitions
                                        val lexicalCategory = l.lexicalCategory
                                        for (d in definitions) {
                                            if (lexicalCategory == "Noun" && d != null) {
                                                //for (String n : definitions)
                                                nounDefinitions.add(d)
                                            }
                                            if (lexicalCategory == "Verb") {
                                                //for (String v : definitions)
                                                verbDefinitions.add(d)
                                            }
                                            if (lexicalCategory == "Adjective") {
                                                //for (String a : definitions)
                                                adjectiveDefinitions.add(d)
                                            }
                                        }
                                    }
                                }
                            }

                            val meaning = ArrayList<List<String>>()
                            meaning.add(nounDefinitions)
                            meaning.add(adjectiveDefinitions)
                            meaning.add(verbDefinitions)

                            meanings.setValue(meaning)
                        }

                        override fun onFailure(call: Call<ResponseData>, t: Throwable) {


                        }
                    })
                }

            }
        }

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
}
