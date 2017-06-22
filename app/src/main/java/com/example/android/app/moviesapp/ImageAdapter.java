package com.example.android.app.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Created by Laurence on 21/05/2017.
 * Adapted From:  https://www.learn2crack.com/2016/03/grid-recycler view-with-images-and-text.html
 *
 */

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MovieViewHolder>{

    private static final String TAG = ImageAdapter.class.getSimpleName();

    private static int viewHolderCount;


    // The Click Listener is passed from the parent as a parameter at construction
    final private ListItemClickListener mOnClickListener;
    interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    ImageAdapter(ListItemClickListener listener){
        super();
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        viewHolderCount = 0;
        mOnClickListener = listener;
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.grid_image_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        viewHolder.viewHolderIndex.setText(String.valueOf(viewHolderCount));
        viewHolderCount++;
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.MovieViewHolder holder, int position) {
        Log.d(TAG, Generator.LOG_ENTERING  + Thread.currentThread().getStackTrace()[2].getMethodName());
        Log.d(TAG, "NUMBER OF VIEWHOLDERS = " + viewHolderCount);
        holder.bind(position);
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public int getItemCount() {
        return MoviesMainActivity.mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView movie_image;
        TextView movie_id;
        TextView viewHolderIndex;
        TextView listItemIndex;

        MovieViewHolder(View itemView) {
            super(itemView);
            listItemIndex = (TextView)itemView.findViewById(R.id.tv_list_item_index);
            movie_image = (ImageView) itemView.findViewById(R.id.img_view);
            movie_id = (TextView) itemView.findViewById(R.id.img_txt);

            viewHolderIndex = (TextView)itemView.findViewById(R.id.tv_view_holder_index);
            itemView.setOnClickListener(this);
        }

        //convenience method for recycling content of the view
        // note the viewHolderIndex does not change for this
        void bind(int listMovieIndex){
            Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
            listItemIndex.setText(String.valueOf(listMovieIndex));
            Movie m = MoviesMainActivity.mMovies.get(listMovieIndex);
            movie_id.setText(String.valueOf(m.getId()));
            Picasso
                    .with(viewHolderIndex.getContext())
                    .load(m.getPosterPath())
                    .into(movie_image);
            Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
            TextView t = (TextView)v.findViewById(R.id.img_txt);
            int movieId = Integer.parseInt(t.getText().toString());
            mOnClickListener.onListItemClick(movieId);
            Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        }

    }
}
