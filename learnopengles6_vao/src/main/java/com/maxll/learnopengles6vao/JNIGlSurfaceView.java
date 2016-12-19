package com.maxll.learnopengles6vao;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-15.
 */

public class JNIGlSurfaceView extends MyGlSurfaceView {

    public JNIGlSurfaceView(Context context) {
        super(context);
    }

    @Override
    void initSurface(GL10 gl, EGLConfig config) {
        init();
    }

    @Override
    void onSizeChange(int width, int height) {
        size(width,height);
    }

    @Override
    void draw(GL10 gl) {
        draw();
    }

    private native void init();
    private native void size(int width,int height);
    private native void draw();

    static {
        System.loadLibrary("implgles");
    }
}
