package com.bcq.looper;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.looper.Logger;
import com.looper.PipelineQueue;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.looper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testApply();
            }
        });
    }

    public void testApply() {
        List<String> os = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            os.add("Queue_" + i);
        }
        boolean ok = PipelineQueue.getQueue().apply(os, 0);
//        boolean ok = CircluarLineQueue.getQueue().apply(os, 0);
        Logger.e("PipelineQueue", "apply ok = " + ok);
    }
}