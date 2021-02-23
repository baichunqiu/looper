package com.bcq.looper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.looper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LineQueue.getQueue().testApply();
                CurveQueue.getQueue().testApply();
            }
        });
    }
}