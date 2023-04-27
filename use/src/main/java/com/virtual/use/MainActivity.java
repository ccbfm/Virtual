package com.virtual.use;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.virtual.util.common.NonNullExecute;
import com.virtual.util.log.VLog;
import com.virtual.util.socket.net.VNetSocket;
import com.virtual.util.socket.net.client.VClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Virtual", "use onClick " + v);
            }
        });
        // VLogExported.i("Use", "MainActivity-onCreate");
        Log.d("Virtual", "Use MainActivity-onCreate");
    }

}