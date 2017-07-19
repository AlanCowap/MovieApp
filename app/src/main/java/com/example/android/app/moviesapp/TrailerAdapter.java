package com.example.android.app.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private static final String SINGLE_SPACE = " ";

    // The Click Listener is passed from the parent as a parameter at construction
    final private OnTrailerAdapterClickEvent mOnClickListener;
    private ArrayList<String> mMovieLinks;
    private int viewHolderCount = 0;

    TrailerAdapter(ArrayList<String> movieLinksList, OnTrailerAdapterClickEvent mOnClickListener) {
        super();
        this.mOnClickListener = mOnClickListener;
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        if (movieLinksList.size() > 0) {
            mMovieLinks = movieLinksList;
        } else {
            mMovieLinks = new ArrayList<>();
        }
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.video_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        viewHolder.listItemIndex.setText(String.valueOf(viewHolderCount));
        viewHolderCount++;
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        holder.movie_link.setText(mMovieLinks.get(position));
        holder.listItemIndex.setText(String.valueOf(position));
        holder.visual.setText(holder.visual.getText() + SINGLE_SPACE + String.valueOf(position + 1));
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public int getItemCount() {
        return mMovieLinks.size();
    }

    interface OnTrailerAdapterClickEvent {
        void LaunchTrailerIntent(String trailerString);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView movie_link;
        TextView listItemIndex;
        TextView visual;

        TrailerViewHolder(View itemView) {
            super(itemView);
            listItemIndex = (TextView) itemView.findViewById(R.id.vid_list_item_index);
            movie_link = (TextView) itemView.findViewById(R.id.vid_video_link);
            visual = (TextView) itemView.findViewById(R.id.tv_favourite);
            visual.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
            String result = movie_link.getText().toString();
            Context context = v.getContext();
            mOnClickListener.LaunchTrailerIntent(result);
            Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        }
    }
}
