package com.example.rajiv.dictionary

import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.example.rajiv.dictionary.DefWithCategoryFragment.OnListFragmentInteractionListener
//import com.example.rajiv.dictionary.dummy.DummyContent.DefWithCategory

import kotlinx.android.synthetic.main.fragment_defwithcategory.view.*
import java.util.ArrayList

/**
 * [RecyclerView.Adapter] that can display a [DefWithCategory] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyDefWithCategoryRecyclerViewAdapter(
        private val mValues: ArrayList<DefWithCategory>?)
    : RecyclerView.Adapter<MyDefWithCategoryRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_defwithcategory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues!![position]
        val category: SpannableString = SpannableString(item.category)
        category.setSpan(UnderlineSpan(),0, category.length, 0)
        holder.mIdView.text = category
        holder.mContentView.text = item.def

        with(holder.mView) {
            tag = item
        }
    }

    override fun getItemCount(): Int = mValues!!.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
