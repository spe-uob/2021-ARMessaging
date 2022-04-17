package com.ajal.arsocialmessaging;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter {

        private List<String> messages;
        private Activity context;
        private List<Integer> imageid;

        public CustomArrayAdapter(Activity context, List<String> messages, List<Integer> imageid) {
            super(context, R.layout.row_item, messages);
            this.context = context;
            this.messages = messages;
            this.imageid = imageid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row=convertView;
            LayoutInflater inflater = context.getLayoutInflater();
            if(convertView==null)
                row = inflater.inflate(R.layout.row_item, null, true);
            TextView textViewCountry = (TextView) row.findViewById(R.id.textViewMessage);
            ImageView imageFlag = (ImageView) row.findViewById(R.id.imageViewMessage);

            textViewCountry.setText(messages.get(position));
            imageFlag.setImageResource(imageid.get(position));
            return  row;
        }
}
