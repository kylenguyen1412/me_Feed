package com.id11825142.feedme.Data;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.id11825142.feedme.Model.Feed;
import com.id11825142.feedme.R;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is created to populate data into the recyclerview
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    ArrayList<Feed> mFeedList = new ArrayList<>();
    Context mContext;


    public FeedAdapter(Context context, ArrayList<Feed> feedList) {
        mFeedList = feedList;
        mContext = context;
    }

    /**
     * return number of items in the feed list
     */
    @Override
    public int getItemCount() {
        return mFeedList.size();
    }

    /**
     * Inflates the XML layout file
     */
    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_list, parent, false);
        FeedViewHolder feedViewHolder = new FeedViewHolder(v);
        return feedViewHolder;
    }

    /**
     * Initialise and assign the values for each row in the recyclerview
     */
    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        holder.mContent.setText(mFeedList.get(position).getContent());
        holder.mID.setText("User");
        holder.setIcon(mFeedList.get(position).getDownloadUrl(),
                mContext,
                mFeedList.get(position).getFileType());
    }

    /**
     * This class holds all the GUI elements that we use in one row
     */
    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public static final String PHOTO_UPLOAD_TYPE = "photos";
        public static final String VIDEO_UPLOAD_TYPE = "videos";
        TextView mID;
        TextView mContent;
        ImageView mImageView;
        VideoView mVideoView;

        /**
         * Initialise all the UI elements link the to the XML layout file
         *
         * @param itemView
         */
        public FeedViewHolder(View itemView) {
            super(itemView);
            mID = (TextView) itemView.findViewById(R.id.item_textView_id);
            mContent = (TextView) itemView.findViewById(R.id.item_textView_content);
        }

        /**
         * Set picture to the imageview of each collumn
         *
         * @param url     url address to download
         * @param context context of
         */
        public void setIcon(String url, Context context, String fileType) {
            if (fileType.equals(PHOTO_UPLOAD_TYPE)) {
                mImageView = (ImageView) itemView.findViewById(R.id.item_imageview_IMG_list);
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mImageView.getLayoutParams();
                params.height = 450;
                mImageView.setLayoutParams(params);
                Glide.with(context).load(url).into(mImageView);
            } else if (fileType.equals(VIDEO_UPLOAD_TYPE)) {
                downloadVideo(url);
            }
        }

        public void downloadVideo(String url){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(url);

            final File rootPath = new File(Environment.getExternalStorageDirectory(), "file_name");
            if(!rootPath.exists()) {
                rootPath.mkdirs();
            }

            final File localFile = new File(rootPath,"imageName.txt");

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ",";local tem file created  created " +localFile.toString());
                    mVideoView = (VideoView) itemView.findViewById(R.id.item_viewView_list);
                    Uri mURI = Uri.fromFile(localFile);
                    mVideoView.setVideoURI(mURI);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ",";local tem file not created  created " +exception.toString());
                }
            });
        }

    }


}
