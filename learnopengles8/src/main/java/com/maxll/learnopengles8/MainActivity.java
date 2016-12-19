package com.maxll.learnopengles8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private MyGlSurface myGlSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMyContentView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGlSurface.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myGlSurface.onPause();
    }

    private void addMyContentView(){
        if(true){
            myGlSurface = new JavaGlSurfaceView(this);
        }else{

        }
        setContentView(myGlSurface);
    }
}
