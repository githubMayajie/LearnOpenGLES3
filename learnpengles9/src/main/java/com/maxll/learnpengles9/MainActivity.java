package com.maxll.learnpengles9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maxll.learnpengles9.MipMap2D.JavaMipMap2DGlSurfaceView;
import com.maxll.learnpengles9.TextureWrap.JavaTextureWrapGlV;

public class MainActivity extends AppCompatActivity {

    MyGlSurfaceView myGlSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generateView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGlSurfaceView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        myGlSurfaceView.onPause();
    }

    private void generateView(){
        switch (MyGlSurfaceViewConfig.Choose.ProjectID){
            case MyGlSurfaceViewConfig.TYPE.PROJECT_1:
            {
                if(MyGlSurfaceViewConfig.Choose.IsJavaProject)
                    myGlSurfaceView = new JavaSimpleTexture2D(this);
                else
                    myGlSurfaceView = new JniSimpleTexture2D(this);

                break;
            }
            case MyGlSurfaceViewConfig.TYPE.PROJECT_2:
            {
                if(MyGlSurfaceViewConfig.Choose.IsJavaProject)
                    myGlSurfaceView = new JavaMipMap2DGlSurfaceView(this);
                break;
            }
            case MyGlSurfaceViewConfig.TYPE.PROJECT_3:
            {
                if(MyGlSurfaceViewConfig.Choose.IsJavaProject)
                    myGlSurfaceView = new JavaTextureWrapGlV(this);
                break;
            }
        }

        setContentView(myGlSurfaceView);
    }
}
