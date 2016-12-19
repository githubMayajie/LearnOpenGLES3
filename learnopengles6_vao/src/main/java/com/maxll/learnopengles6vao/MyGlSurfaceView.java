package com.maxll.learnopengles6vao;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-15.
 */

public abstract class MyGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    protected Context ctx;
    public MyGlSurfaceView(Context context) {
        super(context);
        ctx = context;

        setEGLContextClientVersion(3);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initSurface(gl,config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        onSizeChange(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        draw(gl);
    }

    abstract void initSurface(GL10 gl,EGLConfig config);
    abstract void onSizeChange(int width,int height);
    abstract void draw(GL10 gl);

}
