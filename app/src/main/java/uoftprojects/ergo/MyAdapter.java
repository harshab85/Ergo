package uoftprojects.ergo;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.io.*;


/**
 * Created by ryanprimeau on 15-03-20.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<VideoInfo> mVideos;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder





    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public CardView cardViewErgo;
        public View cardViewErgo;

        public CardView mCardView;
        public TextView mTextView;
        public ImageView mImageView;


        public ViewHolder(View v) {
            super(v);
            cardViewErgo = v;
            mCardView = (CardView) v.findViewById(R.id.cardviewHERE);
            mTextView = (TextView) v.findViewById(R.id.textView);
            mImageView = (ImageView) v.findViewById(R.id.imageView4);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<VideoInfo> videos) {
        mVideos = videos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_card_view, parent, false);
        // set the view's size, margins, paddings and layout parameter
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(mVideos.get(position).displayName);


        File imgFile = new  File(mVideos.get(position).thumbPath);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            holder.mImageView.setImageBitmap(myBitmap);

        }

        //holder.mImageView.setImageResource(mVideos.get(position).thumbPath);
        holder.mCardView.setCardElevation(25);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mVideos.size();
    }


}

