package com.id11825142.feedme.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.id11825142.feedme.Model.Feed;
import com.id11825142.feedme.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * This class handle adding feed activities
 */
public class AddFeedActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PHOTO_UPLOAD_TYPE = "photos";
    public static final String VIDEO_UPLOAD_TYPE = "videos";
    EditText mEditText;
    Button mChoosePicButton;
    Button mAddButton;
    Button mChooseVideoButton;
    ImageView mImageView;
    Feed mFeed;
    Uri mURI;
    VideoView mVideoView;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_content_activity);
        // Create a storage reference from our app
        storageRef = FirebaseStorage.getInstance().getReference();
        setupView();
    }

    /**
     * Set references and OnClick listeners for the interface components
     */
    void setupView() {
        mImageView = (ImageView) findViewById(R.id.item_imageview_IMG);
        mVideoView = (VideoView) findViewById(R.id.item_videoview_VIDEO);
        mEditText = (EditText) findViewById(R.id.AddFeed_Activity_Edittext);
        mChoosePicButton = (Button) findViewById(R.id.AddFeed_Activity_choosePic_Button);
        mChoosePicButton.setOnClickListener(this);
        mChooseVideoButton = (Button) findViewById(R.id.AddFeed_Activity_chooseVideo_Button);
        mChooseVideoButton.setOnClickListener(this);
        mAddButton = (Button) findViewById(R.id.AddFeed_Activity_Add_Button);
        mAddButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.AddFeed_Activity_Add_Button:
                uploadFile();
                startActivity(new Intent(this, DisplayActivity.class));
                break;
            case R.id.AddFeed_Activity_choosePic_Button:
                Intent imageGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(imageGallery, 100);
                break;
            case R.id.AddFeed_Activity_chooseVideo_Button:
                Intent videoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(videoIntent, 101);
                break;
        }
    }

    /**
     * Set
     */
    private void uploadFile() {
        mFeed = new Feed();
        //check if file mURI is null or contain picture or contain video
        if (mURI == null) {
            mFeed.saveFeed(mEditText.getText().toString());
        } else if (mURI.toString().contains("images")) {
            new ImageCompressionAsyncTask().execute();
            //IT CAN BE UPDATE THE PICTURE, BUT NOT THE
        } else uploadToFirebase(VIDEO_UPLOAD_TYPE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            mURI = data.getData();
//            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(0,0);
//            mImageView.setLayoutParams(parms);
            mVideoView.setVisibility(View.INVISIBLE);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageURI(mURI);
        } else if (resultCode == RESULT_OK && requestCode == 101) {
            mURI = data.getData();
            mVideoView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.INVISIBLE);
            mVideoView.setVideoURI(mURI);
            mVideoView.start();
        }
    }

    /**
     * This AsyncTask shows an indeterminate progress dialog while compressing and uploading images
     * . The type parameters are:
     * <p/>
     * Void - No parameters passed to doInBackground()
     * Void - No parameters passed to onProgressUpdate()
     * Void - No result passed to onPostExecute()
     */
    class ImageCompressionAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mProgressDialog;

        public ImageCompressionAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(AddFeedActivity.this);
            mProgressDialog.setMessage(getString(R.string.uploading_message));
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(30000);
                compressImage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            uploadToFirebase(PHOTO_UPLOAD_TYPE);
            // Update the UI with the selected photo. For now the photo just comes from the mimpap folder but in realitiy should be downloaded from the internet or wherever in the doInBackground(..) part of the AsyncTask
            mProgressDialog.dismiss();
        }
    }

    /**
     * Upload file to Firebase Storage
     * @param fileType type of the uploading file
     */
    private void uploadToFirebase(String fileType) {
        StorageReference filePath = storageRef.child(fileType).child(mURI.getLastPathSegment());
        if(fileType == PHOTO_UPLOAD_TYPE){
        filePath.putFile(mURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                mFeed.saveFeed(mEditText.getText().toString(), downloadUrl, PHOTO_UPLOAD_TYPE);
            }
        });} else {filePath.putFile(mURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                mFeed.saveFeed(mEditText.getText().toString(), downloadUrl, VIDEO_UPLOAD_TYPE);
            }
        });}
        Toast.makeText(AddFeedActivity.this, getString(R.string.upload_successfully) + fileType, Toast.LENGTH_LONG);
    }


    /**
     * Check if the ImageView has file or not
     */
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    /**
     * Compress the picture to smaller size
     * @return the file
     */
    public Uri compressImage() {

        String filePath = getRealPathFromURI();
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        scaledBitmap = checkRotation(filePath, scaledBitmap);
        return fromBitmapToUri(scaledBitmap);
    }

    /**
     * Transform Bitmap file to Uri
     * @param scaledBitmap the already resized bitmap file
     * @return Uri version of the bitmap file
     */
    private Uri fromBitmapToUri(Bitmap scaledBitmap) {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        tempDir.mkdir();

        try {
            File tempFile = File.createTempFile("temp", ".jpg", tempDir);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] bitmapData = bytes.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            mURI = Uri.fromFile(tempFile);
            return mURI;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mURI;
    }

    //Gives the actual filepath of the image from its contentUri
    private String getRealPathFromURI() {
        Cursor cursor = getContentResolver().query(mURI, null, null, null, null);
        if (cursor == null) {
            return mURI.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    //calculates a proper value for inSampleSize based on the actual and required dimensions
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    // check the rotation of the image and display it properly
    Bitmap checkRotation(String filePath, Bitmap scaledBitmap) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }


}


