package com.ajal.arsocialmessaging.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.ViewPagerActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

// REFERENCE: https://acomputerengineer.com/2018/04/15/display-image-grid-in-recyclerview-in-android/ 29/11/2021 12:42
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.GridItemViewHolder> {

    private List<String> images;
    private Context context;
    private RecyclerView rv;
    private int imagePos;

    public class GridItemViewHolder extends RecyclerView.ViewHolder {
        SquareImageView siv;

        public GridItemViewHolder(View view) {
            super(view);
            siv = view.findViewById(R.id.siv);
        }
    }

    public ImageGridAdapter(Context c, List<String> images, RecyclerView rv) {
        this.context = c;
        this.images = images;
        this.rv = rv;
    }

    @NonNull
    @Override
    public GridItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);

        return new GridItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridItemViewHolder holder, int position) {
        int pos = position;
        final String path = images.get(pos);

        Picasso.get()
                .load(path)
                .resize(250, 250)
                .centerCrop()
                .into(holder.siv);

        holder.siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePos = pos;
                openViewPagerActivity(imagePos);
            }
        });
    }

    /**
     * Loads up a new activity to display the images in full size
     * @param imagePos
     */
    public void openViewPagerActivity(int imagePos) {
        Intent intent = new Intent(context, ViewPagerActivity.class);
        intent.putExtra("imagePos", imagePos);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    /**
     * reference: https://guides.codepath.com/android/implementing-pull-to-refresh-guide
     */
    // For refresh
    // Clean all elements of the recycler
    public void clear() {
        images.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<String> list) {
        images.addAll(list);
        notifyDataSetChanged();
    }

}