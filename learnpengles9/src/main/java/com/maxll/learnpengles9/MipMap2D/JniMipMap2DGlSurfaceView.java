package com.maxll.learnpengles9.MipMap2D;

import android.content.Context;

import com.maxll.learnpengles9.MyGlSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-19.
 */

public class JniMipMap2DGlSurfaceView extends MyGlSurfaceView{
    public JniMipMap2DGlSurfaceView(Context context) {
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
        jniDraw();
    }

    private native void init();
    private native void onChangeSize(int width,int height);
    private native void jniDraw();

    static {
        System.loadLibrary("impl");
    }
}
