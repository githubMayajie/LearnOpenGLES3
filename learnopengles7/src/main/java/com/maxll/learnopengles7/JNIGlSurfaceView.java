//package com.maxll.learnopengles7;
//
//import android.content.Context;
//import android.opengl.GLSurfaceView;
//import android.opengl.GLSurfaceView;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
///**
// * Created by maxll on 16-12-16.
// */
//
//public class JNIGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer{
//
//    public JNIGlSurfaceView(Context context) {
//        super(context);
//
//        setEGLContextClientVersion(3);
//        setRenderer(this);
//    }
//
//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        init();
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        changeSize(width,height);
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl) {
//        jniDraw();
//    }
////ggVG
//    private native void init();
//    private native void changeSize(int width,int height);
//    private native void jniDraw();
//
//    private native void jniUpdate(float deltaTime);
//
//    static {
//        System.loadLibrary("impl");
//    }
//}
