package com.id11825142.feedme.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.id11825142.feedme.Data.FeedAdapter;
import com.id11825142.feedme.Data.FeedManager;
import com.id11825142.feedme.Model.Feed;
import com.id11825142.feedme.R;

import static java.lang.Long.parseLong;

/*
  This class handle recycleview related activities
 */
public class DisplayActivity extends AppCompatActivity {
    private RecyclerView mItemRecyclerView;
    private FeedManager mFeedManager;
    private FeedAdapter mFeedAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Declare Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        //Declare the only mFeedManager
        mFeedManager = FeedManager.getInstance(this);
        updateData();
        initiateRecyleView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_in:
                startActivity(new Intent(this, AddFeedActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Get data and listen to data changed from the Firebase server
     */
    void updateData() {
        ValueEventListener feedListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Feed feed = new Feed(child.getKey(),
                            child.child("content").getValue().toString(),
                            parseLong(child.child("timestamp").getValue().toString()),
                            child.child("downloadUrl").getValue().toString(),
                            child.child("fileType").getValue().toString());
                    mFeedManager.checkAndAddFeed(feed);
                    Log.w("FeedManager", child.child("content").getValue().toString());
                    mFeedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRef.addValueEventListener(feedListener);

    }

    /**
     * Initialize recycle view
     */
    void initiateRecyleView(){
        //Reference to the Recyclerview
        mItemRecyclerView = (RecyclerView) findViewById(R.id.main_activity_feed_recyclerView);
        //Choose layout orienation for recyclerview
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Create the adapter and set data list to it
        mFeedAdapter = new FeedAdapter(this, mFeedManager.getFeedLists());
        //Link the adapter with the recyclerview
        mItemRecyclerView.setAdapter(mFeedAdapter);
    }
}
