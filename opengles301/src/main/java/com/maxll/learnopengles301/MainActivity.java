package com.maxll.learnopengles301;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.maxll.learnopengles301.JNIImpl.JNIGlSurfaceView;
import com.maxll.learnopengles301.JavaImpl.JavaGlSurfaceView;

public class MainActivity extends AppCompatActivity {

    Handler finishHandler;
//    private GLSurfaceLife surfaceLife;
    private GLSurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!GLImplConfigure.isSupportGLES3(this)){
            noSupportGLES3();
            return;
        }

        addContent();
    }



    private void noSupportGLES3(){
        Toast.makeText(this,"no supportGLES3.0",Toast.LENGTH_SHORT);
        finishHandler = new Handler();
        finishHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },3000);
    }

    private void addContent(){
//        GLSurfaceView view = null;
        if(GLImplConfigure.choose == GLImplConfigure.EVENT.TYPE_JAVA){
            surfaceView = new JavaGlSurfaceView(this);
        }else if(GLImplConfigure.choose == GLImplConfigure.EVENT.TYPE_JNI){
            surfaceView = new JNIGlSurfaceView(this);
        }
        setContentView(surfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(surfaceLife != null){
//            surfaceLife.onPause();
//        }
        if(surfaceView != null)
            surfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(surfaceLife != null){
//            surfaceLife.onResume();
//        }
        if(surfaceView != null)
            surfaceView.onResume();
    }
}
