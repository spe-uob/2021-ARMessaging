package com.ajal.arsocialmessaging.ui.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;


import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentGalleryBinding;
import com.ajal.arsocialmessaging.databinding.FragmentHomeBinding;
import com.bumptech.glide.Glide;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private static final String TAG = "SkyWrite";
    private List<File> images;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite");

        if (dir.exists()) {
            images = Arrays.asList(dir.listFiles().clone());

            listView = root.findViewById(R.id.gallery_list_view);
            List<ImageView> imageViewList = new ArrayList<>();

            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    // TODO: work here
                    File file = images.get(i);

                    // Create a new Card View
                    ImageView card = new ImageView(this.getContext());
                    card.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    Glide.with(this)
                            .load(Uri.fromFile(file))
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .thumbnail(0.5f)
                            .into(card);

                    imageViewList.add(card);
                }
                ArrayAdapter<ImageView> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_gallery_item, imageViewList);
                listView.setAdapter(adapter);
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
