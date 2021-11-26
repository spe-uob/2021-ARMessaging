package com.ajal.arsocialmessaging.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private static final String TAG = "SkyWrite";
    private List<File> images;
    private GridLayout galleryView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Fresco.initialize(this.getContext());

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        SimpleDraweeView posterImage = root.findViewById(R.id.posterImage);
//        Uri imgURI = Uri.parse("https://media-cldnry.s-nbcnews.com/image/upload/t_fit-2000w,f_auto,q_auto:best/newscms/2021_13/3461005/210331-spongebob-episode-mb-1240.jpg");
//        posterImage.setImageURI(imgURI);

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite");

        if (dir.exists()) {
            images = Arrays.asList(dir.listFiles().clone());

            galleryView = root.findViewById(R.id.gallery_view);
            List<SimpleDraweeView> imageViewList = new ArrayList<>();

            if (images != null) {
                int column = 0;
                int row = 0;
                galleryView.setColumnCount(3);

                for (int i = 0; i < images.size(); i++) {
                    File file = images.get(i);

                    // Create a new Image View
                    ImageView img = new ImageView(this.getContext());
                    img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    img.setPadding(10, 10, 10, 10);
                    Glide.with(this)
                            .load(Uri.fromFile(file))
                            .placeholder(R.drawable.ic_launcher_foreground) // temp
                            .apply(RequestOptions.centerCropTransform())
                            .thumbnail(0.5f)
                            .into(img);

                    galleryView.addView(img);
                    column++;
                    if (column % 3 == 0) {
                        row++;
                        column = 0;
                    }
                    Log.d(TAG, "row="+row+"col="+column);
                }
//                ArrayAdapter<SimpleDraweeView> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.list_content, imageViewList);
//                galleryView.setAdapter(adapter);
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
