package com.maxll.learnopengles6;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-16.
 */

public class JNIExample6GlSurfaceView extends MyGlSurfaceView {

    public JNIExample6GlSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        onChangeSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        jnidraw();
    }

    private native void init();
    private native void onChangeSize(int width,int height);
    private native void jnidraw();


    static {
        System.loadLibrary("implgles");
    }

}
