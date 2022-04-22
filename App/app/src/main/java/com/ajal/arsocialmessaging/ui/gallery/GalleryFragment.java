package com.ajal.arsocialmessaging.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentGalleryBinding;

// REFERENCE: https://acomputerengineer.com/2018/04/15/display-image-grid-in-recyclerview-in-android/ 29/11/2021 12:42
public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private static final String TAG = "SkyWrite";
    private List<File> images;

    private SwipeRefreshLayout refreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load files and store in imageFilenames
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite");
        List<String> imageFilenames = new ArrayList<>();

        if (dir.exists()) {
            images = Arrays.asList(dir.listFiles().clone());
            Collections.sort(images, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    long k = file1.lastModified() - file2.lastModified();
                    if (k > 0) return 1;
                    else if (k == 0) return 0;
                    else return -1;
                }
            });
            Collections.reverse(images); // reverse images so that the newest images are first

            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    File file = images.get(i);
                    imageFilenames.add(Uri.fromFile(file).toString());
                }
            }
        }

        // Set up Recycler View
        RecyclerView rv = root.findViewById(R.id.rv);
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(sglm);

        // Set up Image Grid
        ImageGridAdapter iga = new ImageGridAdapter(this.getContext(), imageFilenames, rv);
        rv.setAdapter(iga);

        // Refresh
        refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                getActivity().recreate();
                Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });

        // Observer
        /**
         * Reference: https://www.demo2s.com/android/android-fileobserver-tutorial-with-examples.html
         */
        final Handler handler = new Handler();
        FileObserver observer = new FileObserver(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite") { //MAY NOT BE SO DEPENDABLE
            @Override
            public void onEvent(int event, final String path) {
                if (event == DELETE) {
                    Log.i(TAG, "Path: "+path);
                    Log.i(TAG, "Event: "+event);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Image Deleted", Toast.LENGTH_SHORT).show();
                            getActivity().recreate();
                        }
                    });
                }
            }
        };
        observer.startWatching();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
