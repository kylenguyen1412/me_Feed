package com.id11825142.feedme.Data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.id11825142.feedme.Model.Feed;
import com.id11825142.feedme.View.DisplayActivity;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

/**
 * This class is the link between the database from firebase and the view.
 * This class is used to create only one database at all time
 */
public class FeedManager {
    private static FeedManager sFeedManager;
    private DatabaseReference mDatabase;
    private ArrayList<Feed> mFeedArrayList;

    /**
     * Declare the class
     * @param context
     */
    private FeedManager(Context context) {
        //Firebase - Initialize
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFeedArrayList = new ArrayList<Feed>();
    }

    //only one FeedManager
    public static FeedManager getInstance(Context context) {
        if (sFeedManager == null) {
            sFeedManager = new FeedManager(context);
        }
        return sFeedManager;
    }

    /**
     * Return the arraylist of subjects
     * @return
     */
    public ArrayList<Feed> getFeedLists() {
        return mFeedArrayList;
    }

    public void updateData() {
//        //Create listener for new data
//        mDatabase.addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Feed newItem = dataSnapshot.getValue(Feed.class);
//                mFeedArrayList.add(newItem);
//
//                if( dataSnapshot != null && dataSnapshot.getValue() != null){
//                    try{
//
//                    } catch (Exception exception){
//                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
////                if( dataSnapshot != null && dataSnapshot.getValue() != null){
////                    try{
////                        Feed removedItem = dataSnapshot.getValue(Feed.class);
////                        mFeedArrayList.remove(removedItem);
////
////                    } catch (Exception exception){
////
////                    }
////                }
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        });
    }

   /*
    Check if the feed added duplicatedly or not, by measuring the timestamp
     */
    public void checkAndAddFeed(Feed feed) {
        if (mFeedArrayList.size() == 0) {
            mFeedArrayList.add(feed);
        } else if (mFeedArrayList.get(mFeedArrayList.size() - 1).getTimestamp() < feed.getTimestamp()) {
            mFeedArrayList.add(feed);
        }
    }


}