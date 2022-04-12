package com.ajal.arsocialmessaging.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentGalleryBinding;

// REFERENCE: https://acomputerengineer.com/2018/04/15/display-image-grid-in-recyclerview-in-android/ 29/11/2021 12:42
public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private static final String TAG = "SkyWrite";
    private List<File> images;

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
