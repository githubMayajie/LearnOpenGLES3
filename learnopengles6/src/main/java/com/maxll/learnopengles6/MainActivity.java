package com.maxll.learnopengles6;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAndAddContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    private void createAndAddContent(){

        if(!GLSurfaceViewConfig.projectConfig.isJNI){
            if(GLSurfaceViewConfig.projectConfig.PROJECTID == GLSurfaceViewConfig.EVENT.VBO)
            {
                glSurfaceView = new JavaVBOGlSurfaceView(this);
            }else if(GLSurfaceViewConfig.projectConfig.PROJECTID == GLSurfaceViewConfig.EVENT.MAPBUFFER){
                glSurfaceView = new JavaMapBufferGlSurfaceView(this);
            }else if(GLSurfaceViewConfig.projectConfig.PROJECTID == GLSurfaceViewConfig.EVENT.EXAMPLE6){
                glSurfaceView = new JavaExample6GlSurfaceView(this);
            }
        }else{
            if(GLSurfaceViewConfig.projectConfig.PROJECTID == GLSurfaceViewConfig.EVENT.VBO){
                glSurfaceView = new JNIVBOGlSurfaceView(this);
            }else if(GLSurfaceViewConfig.projectConfig.PROJECTID == GLSurfaceViewConfig.EVENT.MAPBUFFER){
                glSurfaceView = new JNIMapBufferGlSurfaceView(this);
            }else if(GLSurfaceViewConfig.projectConfig.PROJECTID == GLSurfaceViewConfig.EVENT.EXAMPLE6){
                glSurfaceView = new JNIExample6GlSurfaceView(this);
            }
        }
        setContentView(glSurfaceView);
    }
}
