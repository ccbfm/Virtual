package com.virtual.evolute;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.virtual.ui.plane.PlaneManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        setContentView(PlaneManager.instance().contentView(this, width, height));
    }
}