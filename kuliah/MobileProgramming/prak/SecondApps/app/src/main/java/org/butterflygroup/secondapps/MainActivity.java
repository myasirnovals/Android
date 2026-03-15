package org.butterflygroup.secondapps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    String tag = "Lifecycle Step";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(tag,"Activity oncreate");
        int loadingTime = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
            }
        },loadingTime);
    }
    public void onStart(){
        super.onStart();
        Log.d(tag,"Activity onstart");
    }
    public void onPause(){
        super.onPause();
        Log.d(tag,"Activity onPause");
    }
    public void onResume(){
        super.onResume();
        Log.d(tag, "Activity onResume");
    }
    public void onStop(){
        super.onStop();
        Log.d(tag, "Activity on stop");
    }
    public void onDestroy(){
        super.onDestroy();
        Log.d(tag,"Destroyed");
    }
    public void onRestart(){
        super.onRestart();
        Log.d(tag, "Activity direstart");
    }
}