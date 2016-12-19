package com.maxll.learnopengles6;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-16.
 */

public class JNIMapBufferGlSurfaceView extends MyGlSurfaceView {

    public JNIMapBufferGlSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        jniinit();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        onChangeSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        jniDraw();
    }

    private native void jniinit();
    private native void onChangeSize(int width,int height);
    private native void jniDraw();
    static {
        System.loadLibrary("implgles");
    }
}
