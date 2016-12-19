package com.maxll.learnopengles301.JNIImpl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView;

import com.maxll.learnopengles301.GLImplConfigure;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-15.
 */

public class JNIGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private Context context;

    public JNIGlSurfaceView(Context ctx) {
        super(ctx);
        context = ctx;
        this.setEGLContextClientVersion(GLImplConfigure.GLES_CLIENT_VERSION);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        sizeChange(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        draw();
    }

    private native void init();
    private native void sizeChange(int width,int height);
    private native void draw();


    static {
        System.loadLibrary("triangle");
    }
}
