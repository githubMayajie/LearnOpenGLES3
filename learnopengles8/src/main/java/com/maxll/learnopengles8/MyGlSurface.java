package com.maxll.learnopengles8;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView;

/**
 * Created by maxll on 16-12-17.
 */

public abstract class MyGlSurface extends GLSurfaceView implements GLSurfaceView.Renderer {

    public MyGlSurface(Context context) {
        super(context);

        setEGLContextClientVersion(3);
        setRenderer(this);
    }
}
