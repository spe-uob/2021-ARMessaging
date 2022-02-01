package com.ajal.arsocialmessaging.ui.gallery;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentGalleryBinding;
import com.squareup.picasso.Picasso;

// REFERENCE: https://acomputerengineer.com/2018/04/15/display-image-grid-in-recyclerview-in-android/ 29/11/2021 12:42
public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private static final String TAG = "SkyWrite";
    private List<File> images;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load files and store in imageList
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite");
        List<String> imageList = new ArrayList<>();
        if (dir.exists()) {
            images = Arrays.asList(dir.listFiles().clone());
            Collections.reverse(images); // reverse images so that the newest images are first

            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    File file = images.get(i);
                    imageList.add(Uri.fromFile(file).toString());
                }
            }
        }

        // Set up Image Grid
        RecyclerView rv = root.findViewById(R.id.rv);
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(sglm);
        ImageView imageViewFull = root.findViewById(R.id.image_view_full);
        ImageGridAdapter iga = new ImageGridAdapter(this.getContext(), imageList, rv, imageViewFull);
        rv.setAdapter(iga);

        // Add a touch listener to imageViewFull to listen for left/right swipes and taps
        imageViewFull.setOnTouchListener(new OnSwipeTouchListener(this.getContext()) {

            // Minimise imageViewFull when tapped
            @Override
            public void onClick() {
                imageViewFull.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.VISIBLE);
            }

            // Displays previous image
            @Override
            public void onSwipeLeft() {
                if (iga.getImagePos() > 0) {
                    iga.decrementImagePos();
                    final String path = imageList.get(iga.getImagePos());

                    Picasso.get()
                            .load(path)
                            .resize(1000, 1000)
                            .centerInside()
                            .into(imageViewFull);
                }
            }

            // Displays next image
            @Override
            public void onSwipeRight() {
                if (iga.getImagePos() < imageList.size()-1) {
                    iga.incrementImagePos();
                    final String path = imageList.get(iga.getImagePos());

                    Picasso.get()
                            .load(path)
                            .resize(1000, 1000)
                            .centerInside()
                            .into(imageViewFull);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}