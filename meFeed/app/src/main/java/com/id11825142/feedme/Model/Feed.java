package com.id11825142.feedme.Model;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;


/**
 * This class is present for data model containing feed and details
 */
public class Feed {
    //feed id
    private String mId;
    //feed content
    private String mContent;
    //time that post feed
    private long mTimestamp;
    //URL to download pictures or images
    private String mDownloadUrl;
    //display file type in the URL, 1 is for image and 2 is for video
    private String mFileType;



    HashMap<String, Object> value;
    private DatabaseReference mDatabase;

    public Feed(String id, String content, long timestamp,String downloadUrl, String fileType){
        mId =id;
        mContent = content;
        mTimestamp = timestamp;
        this.mDownloadUrl = downloadUrl;
        mFileType = fileType;
    }

    public Feed(){

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public HashMap<String, Object> getValue(){
        return value;
    }

    public void setValue(HashMap<String, Object> value){
        this.value = value;
    }

    public String getFileType() {
        return mFileType;
    }

    public void setFileType(String fileType) {
        mFileType = fileType;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.mDownloadUrl = downloadUrl;
    }

    /*
    Save feed which don't have pic or video to Firebase
     */
    public void saveFeed(String content){
        mDatabase = FirebaseDatabase.getInstance().getReference().push();
        value = new HashMap<>();
        value.put("id",mDatabase.getKey());
        value.put("content",content);
        value.put("timestamp", ServerValue.TIMESTAMP);
        value.put("fileType", "nothing");
        value.put("downloadUrl", "nothing");
        mDatabase.setValue(value);
    }

    /*
    Save feed that have video or picture to Firebase
     */
    public void saveFeed(String content, String downloadUrl, String fileType){
        mDatabase = FirebaseDatabase.getInstance().getReference().push();
        value = new HashMap<>();
        value.put("id",mDatabase.getKey());
        value.put("content",content);
        value.put("timestamp", ServerValue.TIMESTAMP);
        value.put("fileType", fileType);
        value.put("downloadUrl",downloadUrl);
        mDatabase.setValue(value);
    }

}
