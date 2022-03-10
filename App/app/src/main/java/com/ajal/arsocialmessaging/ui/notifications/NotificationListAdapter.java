package com.ajal.arsocialmessaging.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.util.database.Banner;

import java.util.List;

// REFERENCE: https://stackoverflow.com/questions/8166497/custom-adapter-for-list-view 28/02/2022 15:03
public class NotificationListAdapter extends ArrayAdapter<Banner> {
    private int resourceLayout;
    private Context mContext;

    public NotificationListAdapter(Context context, int resource, List<Banner> banners) {
        super(context, resource, banners);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Banner banner = getItem(position);

        if (banner != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.postcode_line);
            TextView tt2 = (TextView) v.findViewById(R.id.message_line);
            TextView tt3 = (TextView) v.findViewById(R.id.timestamp_line);

            // Do not try and use ttx.getText() because it will end up merging items in the list together
            if (tt1 != null) {
                tt1.setText("Message: " + banner.getPostcode());
            }

            if (tt2 != null) {
                tt2.setText("Postcode: " + banner.getMessageAsString());
            }

            if (tt3 != null) {
                tt3.setText("Sent at: " + banner.getTimestamp());
            }
        }

        return v;
    }
}
