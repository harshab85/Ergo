package uoftprojects.ergo;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.util.List;
import java.io.*;

import uoftprojects.ergo.util.StorageUtil;


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
        public ImageView mRewardSticker;


        public ImageView mLeftRewardSticker;


        public ViewHolder(View v) {
            super(v);
            cardViewErgo = v;
            mCardView = (CardView) v.findViewById(R.id.cardviewHERE);
            mTextView = (TextView) v.findViewById(R.id.textView);
            mImageView = (ImageView) v.findViewById(R.id.imageView4);
            mRewardSticker = (ImageView) v.findViewById(R.id.rewardSticker);
            mLeftRewardSticker = (ImageView) v.findViewById(R.id.leftSticker);

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


        boolean videoWatched = StorageUtil.getBoolean(position + "_watched");
        if(videoWatched){
            holder.mRewardSticker.setVisibility(View.VISIBLE);
                int resourceId = StorageUtil.getInt(position + "_sticker");
            if(resourceId >= 0){
                holder.mLeftRewardSticker.setImageResource(resourceId);
                holder.mLeftRewardSticker.setVisibility(View.VISIBLE);
            }else{
                holder.mLeftRewardSticker.setVisibility(View.INVISIBLE);

            }

        }else{
            holder.mRewardSticker.setVisibility(View.INVISIBLE);
            holder.mLeftRewardSticker.setVisibility(View.INVISIBLE);
        }



        holder.mTextView.setText(mVideos.get(position).displayName);

        holder.mImageView.setImageResource(R.drawable.ergoinspace);


        File imgFile = null;
        if(mVideos.get(position).thumbPath != null){
            imgFile = new  File(mVideos.get(position).thumbPath);
        }


        if(imgFile != null && imgFile.exists()){
            //System.out.println("EXISTS");
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            holder.mImageView.setImageBitmap(myBitmap);

        }else{

            //System.out.println("NOT EXISTS");
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

