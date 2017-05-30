package com.example.android.app.moviesapp;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Laurence on 21/05/2017.
 *
 * adapted from https://stacktips.com/tutorials/android/download-and-display-image-in-android-grid view
 * https://stacktips.com/tutorials/android/android-grid view-example-building-image-gallery-in-android
 * NOTE REQUIRES AN ARRAY LIST OF TYPE INTEGER TO BE CREATED FROM MAIN
 */

class MovieAdapter extends ArrayAdapter {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;
    private int layoutResourceId;

    //constructor
    MovieAdapter(Context c, int layoutResourceId, ArrayList<Integer> list){
        super(c, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        mContext = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if(row == null){
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.img = (ImageView)row.findViewById(R.id.img_view);
            row.setTag(holder);

        }else{
            holder = (ViewHolder)row.getTag();
        }

        if(TextUtils.isEmpty(MoviesMainActivity.mMovies.get(position).getPosterPath())){
            Picasso
                    .with(mContext)
                    .load(R.mipmap.ic_launcher_round)
                    .into(holder.img);
        } else{
            Picasso
                    .with(mContext)
                    .load(MoviesMainActivity.mMovies.get(position).getPosterPath())
                    .into(holder.img);
        }

        return row;
    }

    private class ViewHolder{
        ImageView img;
    }
}
