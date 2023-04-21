package com.virtual.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.virtual.util.log.VLogExported;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View view = findViewById(R.id.text);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Virtual", "app onClick " + v);
            }
        });
        //VLogExported.i("app", "MainActivity-onCreate");
    }

}