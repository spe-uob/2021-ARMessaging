package com.ajal.arsocialmessaging.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajal.arsocialmessaging.R;
import com.squareup.picasso.Picasso;

import java.util.List;

// REFERENCE: https://acomputerengineer.com/2018/04/15/display-image-grid-in-recyclerview-in-android/ 29/11/2021 12:42
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.GridItemViewHolder> {

    private List<String> imageList;
    private Context c;
    private RecyclerView rv;
    private ImageView imageViewFull;
    private int imagePos;

    public int getImagePos() {
        return imagePos;
    }

    public void setImagePos(int imagePos) {
        this.imagePos = imagePos;
    }

    public class GridItemViewHolder extends RecyclerView.ViewHolder {
        SquareImageView siv;

        public GridItemViewHolder(View view) {
            super(view);
            siv = view.findViewById(R.id.siv);
        }
    }

    public ImageGridAdapter(Context c, List imageList, RecyclerView rv, ImageView imageViewFull) {
        this.c = c;
        this.imageList = imageList;
        this.rv = rv;
        this.imageViewFull = imageViewFull;
    }

    @NonNull
    @Override
    public GridItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);

        return new GridItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridItemViewHolder holder, int position) {
        imagePos = position;
        final String path = imageList.get(position);

        Picasso.get()
                .load(path)
                .resize(250, 250)
                .centerCrop()
                .into(holder.siv);

        holder.siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Skywrite: when an image is clicked on, fill the entire screen with it
                Picasso.get()
                        .load(path)
                        .resize(1000, 1000)
                        .centerInside()
                        .into(imageViewFull);

                imageViewFull.setVisibility(View.VISIBLE);
                rv.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

}