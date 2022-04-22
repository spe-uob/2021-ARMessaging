package com.ajal.arsocialmessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajal.arsocialmessaging.util.database.server.ServerDBObserver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.internal.io.FileSystem;

public class ViewPagerActivity extends AppCompatActivity {

    private static final String TAG = "SkyWrite";
    private AppCompatActivity activity = this;
    private List<File> images;
    private int currentPosition;
    private ViewPager viewPager;

    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.f;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        // Set up actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load files and store in imageFilenames
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite");
        List<String> imageFilenames = new ArrayList<>();
        if (dir.exists()) {
            images = Arrays.asList(dir.listFiles().clone());
            Collections.reverse(images); // reverse images so that the newest images are first

            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    File file = images.get(i);
                    imageFilenames.add(Uri.fromFile(file).toString());
                }
            }
        }

        // Set up View Pager
        viewPager = findViewById(R.id.viewPagerMain);
        PagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, this, imageFilenames);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageSelected(int position) {
                mImageView = findViewById(R.id.imageViewMain);
                // Update the action bar title
                currentPosition = position;
                activity.getSupportActionBar().setTitle((position+1)+"/"+images.size());
            }
        });

        Bundle b = getIntent().getExtras();
        int imagePos = b.getInt("imagePos");
        viewPager.setCurrentItem(imagePos);

//        mImageView = findViewById(R.id.imageViewMain);
        scaleGestureDetector =
                new ScaleGestureDetector(this,
                        new ScaleListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_pager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                shareImage();
                Log.d(TAG, "Image shared");
                return true;
            case R.id.action_delete:
                deleteImage();
                finish();
                Log.d(TAG, "Image deleted");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * reference: https://stackoverflow.com/questions/10716642/android-deleting-an-image
     */
    public void deleteImage() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/SkyWrite";
        String imageName = getCurrentImage();
        File fdelete = new File(path, imageName);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.e("-->", "file Deleted :" + fdelete.toString());
                callBroadCast();
            } else {
                Log.e("-->", "file not Deleted :" + fdelete.toString());
            }
        }

    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    // Share image.
    /**
     * Reference: https://guides.codepath.com/android/Sharing-Content-with-Intents
     */
    public void shareImage(){
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        String name = getCurrentImage();
        final File photoFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite", name);
        shareIntent.putExtra(Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile));
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }

    // Pinch gesture to zoom in/out.
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        scaleGestureDetector.onTouchEvent(ev);
        return true;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Matrix matrix = new Matrix();
            mScaleFactor = detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            matrix.setScale(mScaleFactor,mScaleFactor);
            mImageView.setImageMatrix(matrix);

//            mImageView.setScaleX(mScaleFactor);
//            mImageView.setScaleY(mScaleFactor);

            if (mScaleFactor > 1) {
                Toast.makeText(getApplicationContext(), "you zoomed out", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "you zoomed in", Toast.LENGTH_SHORT).show();
            }

//            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    // Helper function
    private String getCurrentImage(){
        String name = new String();
        if (images != null) {
            int i = 0;
            for (File img:images){
                if (i == currentPosition){
                    name = img.getName();
                    break;
                }else if (i<images.size()) i ++;
            }
        }
        return name;
    }
}