package com.maxll.learnpengles9;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-19.
 */

public class JniSimpleTexture2D extends MyGlSurfaceView {
    public JniSimpleTexture2D(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        onSizeChange(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        jniDraw();
    }

    private native void init();
    private native void onSizeChange(int width,int height);
    private native void jniDraw();

    static {
        System.loadLibrary("impl");
    }
}
