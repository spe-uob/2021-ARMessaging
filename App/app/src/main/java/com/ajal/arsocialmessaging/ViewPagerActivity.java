package com.ajal.arsocialmessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
        ViewPager viewPager = findViewById(R.id.viewPagerMain);
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
                // Update the action bar title
                currentPosition = position;
                activity.getSupportActionBar().setTitle((position+1)+"/"+images.size());
            }
        });

        Bundle b = getIntent().getExtras();
        int imagePos = b.getInt("imagePos");
        viewPager.setCurrentItem(imagePos);
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
                // TODO: share image
                Log.d(TAG, "Image shared");
                return true;
            case R.id.action_delete:
                // TODO: delete image
                deleteImage();
                Toast toast = Toast.makeText(getApplicationContext(), "Image deleted", Toast.LENGTH_SHORT);
                toast.show();
                finish();
                this.recreate();

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
        File fdelete = new File(path);
        if (images != null) {
            int i = 0;
            for (File img:images){
                if (i == currentPosition){
                    fdelete = new File(path, img.getName());
                    break;
                }else if (i<images.size()) i ++;
            }
        }
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

    public void shareImage(){

    }
}