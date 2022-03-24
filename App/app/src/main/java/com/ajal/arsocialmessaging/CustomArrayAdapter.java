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

        private List<String> countryNames;
        private List<String> capitalNames;
        private Integer[] imageid;
        private Activity context;

        public CustomArrayAdapter(Activity context, List<String> countryNames, List<String> capitalNames) {
            super(context, R.layout.row_item, countryNames);
            this.context = context;
            this.countryNames = countryNames;
            this.capitalNames = capitalNames;
            this.imageid = imageid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row=convertView;
            LayoutInflater inflater = context.getLayoutInflater();
            if(convertView==null)
                row = inflater.inflate(R.layout.row_item, null, true);
            TextView textViewCountry = (TextView) row.findViewById(R.id.textViewCountry);
            TextView textViewCapital = (TextView) row.findViewById(R.id.textViewCapital);
            ImageView imageFlag = (ImageView) row.findViewById(R.id.imageViewFlag);

            textViewCountry.setText(countryNames.get(position));
            textViewCapital.setText(capitalNames.get(position));
            imageFlag.setImageResource(imageid[position]);
            return  row;
        }
}
