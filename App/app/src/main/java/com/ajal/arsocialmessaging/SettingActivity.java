package com.ajal.arsocialmessaging;

import static com.ajal.arsocialmessaging.R.id.text_size;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

public class SettingActivity extends PreferenceActivity {

//    public class BaseActivity extends SettingActivity {
//        @Override
//        public void onStart() {
//            super.onStart();
//
//            try {
//                SharedPreferences textSize =
//                        getSharedPreferences("font_size", Context.MODE_PRIVATE);
//
//                // Get the font size option.  Use "font_size" as the key.
//                // Make sure to use this key when you set the value in SharedPreferences.
//                // We specify "Medium" as the default value, if it does not exist.
//                String fontSizePref = textSize.getString("font_size", "Medium");
//
//                // Select the proper theme ID.
//                // These will correspond to your theme names as defined in themes.xml.
//                int themeID = R.style.FontSizeMedium;
//                if (fontSizePref == "Small") {
//                    themeID = R.style.FontSizeSmall;
//                } else if (fontSizePref == "Large") {
//                    themeID = R.style.FontSizeLarge;
//                }
//
//                // Set the theme for the activity.
//                setTheme(themeID);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

//    public class AppActivity extends BaseActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            SeekBar textSize = (SeekBar) findViewById(text_size);

            textSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                SharedPreferences settings =
//                        getSharedPreferences("font_size", Context.MODE_PRIVATE);
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Context context = getApplicationContext();
                    CharSequence text = "Hello toast!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

//            Preference darkMode = findPreference("dark_mode");


        }

//    }
}
