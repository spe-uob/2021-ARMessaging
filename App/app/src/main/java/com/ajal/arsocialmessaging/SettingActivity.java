package com.ajal.arsocialmessaging;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

public class SettingActivity extends PreferenceActivity {

//    SeekBar textSize;
//    SwitchPreference darkMode;
//    SwitchPreference notification;
//    SwitchPreference locationUpdate;
//    Preference about;
//    Preference feedback;


    public class BaseActivity extends SettingActivity {
        @Override
        public void onStart() {
            super.onStart();

            try {
                SharedPreferences settings =
                        getSharedPreferences("com.example.YourAppPackage", Context.MODE_PRIVATE);

                // Get the font size option.  We use "FONT_SIZE" as the key.
                // Make sure to use this key when you set the value in SharedPreferences.
                // We specify "Medium" as the default value, if it does not exist.
                String fontSizePref = settings.getString("FONT_SIZE", "Medium");

                // Select the proper theme ID.
                // These will correspond to your theme names as defined in themes.xml.
                int themeID = R.style.FontSizeMedium;
                if (fontSizePref == "Small") {
                    themeID = R.style.FontSizeSmall;
                } else if (fontSizePref == "Large") {
                    themeID = R.style.FontSizeLarge;
                }

                // Set the theme for the activity.
                setTheme(themeID);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    public class AppActivity extends BaseActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            SeekBar textSize = (SeekBar) findViewById(R.id.text_size);

            textSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textSize.setProgress(progress);
                    Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }

    }
}
