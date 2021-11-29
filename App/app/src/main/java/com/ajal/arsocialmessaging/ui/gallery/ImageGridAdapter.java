package com.ajal.arsocialmessaging.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajal.arsocialmessaging.R;
import com.squareup.picasso.Picasso;

import java.util.List;

// REFERENCE: https://acomputerengineer.com/2018/04/15/display-image-grid-in-recyclerview-in-android/ 29/11/2021 12:42
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.GridItemViewHolder> {

    private List<String> imageList;

    private Context c;

    public class GridItemViewHolder extends RecyclerView.ViewHolder {
        SquareImageView siv;

        public GridItemViewHolder(View view) {
            super(view);
            siv = view.findViewById(R.id.siv);
        }
    }

    public ImageGridAdapter(Context c, List imageList) {
        this.c = c;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public GridItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);

        return new GridItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridItemViewHolder holder, int position) {
        final String path = imageList.get(position);

        Picasso.get()
                .load(path)
                .resize(250, 250)
                .centerCrop()
                .into(holder.siv);

        holder.siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle click event on image
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

}