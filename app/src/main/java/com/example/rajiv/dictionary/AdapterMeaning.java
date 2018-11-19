package com.example.rajiv.dictionary;

import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class AdapterMeaning extends RecyclerView.Adapter<AdapterMeaning.VHolder>{
    private List<List<String>> mDataset;

    public static class VHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public VHolder(TextView v){
            super(v);
            mTextView = v;
        }
    }

    public AdapterMeaning(List<List<String>> dataset){
        mDataset = dataset;
    }

    @Override
    public AdapterMeaning.VHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return null;
    }

    @Override
    public void onBindViewHolder(VHolder holder, int position){


    }

    @Override
    public int getItemCount(){
        return 0;
    }



}
