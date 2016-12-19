package com.maxll.learnpengles9;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView;

/**
 * Created by maxll on 16-12-18.
 */

public abstract class MyGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    public MyGlSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(MyGlSurfaceViewConfig.CHOOSE_CLIENT_VERSION);
        setRenderer(this);
    }
}
