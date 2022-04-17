package com.ajal.arsocialmessaging.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
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
    private List<File> images = new ArrayList<>();

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

        // If there are no photos, display message saying there are no messages, else display recycler view
        TextView text = root.findViewById(R.id.text_gallery_none);
        RecyclerView rv = root.findViewById(R.id.rv);
        if (images.size() == 0) {
            text.setVisibility(View.VISIBLE);
            rv.setVisibility(View.INVISIBLE);
            return root;
        } else {
            rv.setVisibility(View.VISIBLE);
            text.setVisibility(View.INVISIBLE);
        }

        // Set up Recycler View
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
