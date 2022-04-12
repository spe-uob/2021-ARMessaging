package com.ajal.arsocialmessaging.ui.gallery;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ajal.arsocialmessaging.R;

public class RefreshActivity extends Activity {

    private SwipeRefreshLayout refreshLayout;
   @Override
    protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.fragment_gallery);

       refreshLayout = findViewById(R.id.refreshLayout);
       refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
               Toast toast = Toast.makeText(getApplicationContext(), "refreshed", Toast.LENGTH_SHORT);
               toast.show();

               refreshLayout.setRefreshing(false);
           }
       });
   }

}
