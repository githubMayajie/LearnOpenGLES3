package com.maxll.learnopengles6;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-16.
 */

public abstract class MyGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private Context context;

    public MyGlSurfaceView(Context context) {
        super(context);

        this.context = context;
        setEGLContextClientVersion(GLSurfaceViewConfig.OPENGLES_CLIENT_VERSION);
        setRenderer(this);
    }
}
