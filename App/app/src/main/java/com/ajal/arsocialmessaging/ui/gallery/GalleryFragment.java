package com.ajal.arsocialmessaging.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentGalleryBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private static final String TAG = "SkyWrite";
    private List<File> images;
    private GridLayout galleryView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite");

        if (dir.exists()) {
            images = Arrays.asList(dir.listFiles().clone());
            galleryView = root.findViewById(R.id.gallery_view);

            if (images != null) {
                galleryView.setColumnCount(3);

                for (int i = 0; i < images.size(); i++) {
                    File file = images.get(i);

                    // Create a new Image View
                    ImageView img = new ImageView(this.getContext());
                    img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    img.setPadding(5, 5, 5, 5);

                    // Use Glide to draw the image
                    Glide.with(this)
                            .load(Uri.fromFile(file))
                            .placeholder(R.drawable.ic_broken_image_black_120dp)
                            .apply(RequestOptions.centerCropTransform())
                            .thumbnail(0.5f)
                            .into(img);

                    galleryView.addView(img);
                }
            }
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
