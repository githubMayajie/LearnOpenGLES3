package com.maxll.learnopengles301.JavaImpl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.maxll.learnopengles301.GLImplConfigure;
//import com.maxll.learnopengles301.GLSurfaceLife;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-15.
 */

public class JavaGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer{

    private Context context;

    public JavaGlSurfaceView(Context ctx) {
        super(ctx);

        context = ctx;
//        surfaceLife = new JavaGlSurfaceLife();
        setEGLContextClientVersion(GLImplConfigure.GLES_CLIENT_VERSION);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initGLSurface();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        onSizeChange(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        draw();
    }


//    private class JavaGlSurfaceLife implements GLSurfaceLife{
//
//        @Override
//        public void onResume() {
//            JavaGlSurfaceView.this.onResume();
//        }
//
//        @Override
//        public void onPause() {
//            JavaGlSurfaceView.this.onPause();
//        }
//    }

    ///////////////////////////////
    private final static String TAG = JavaGlSurfaceView.class.getName();
    //设置view大小的时候需要
    private int height,width;
    private void onSizeChange(int width,int height){
        this.height = height;
        this.width = width;
    }


    private final float[] vertexDataSource = {
            //x     y   z
            0.0f,0.5f,0.0f,
            -0.5f,-0.5f,0.0f,
            0.5f,-0.5f,0.0f

//            ,0.0f,0.5f,0.0f,
//            0.25f,1.0f,0.0f,
//            0.5f,0.5f,0.0f
    };
    //the main reason is performace:ByteFloat and the othe nio classes enable accelerated
    //operations when interfacing widh native code(typically by avoiding the need to copy data into
    // a temporary buffer)
    //主要是为了适配native代码中数据共享相同的内存，而不是各自一份
    private FloatBuffer floatBuffer;
    private int programObject;

    private byte[] readAssetFile(String fileName){
        try {
            InputStream is = context.getAssets().open(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = is.read(buffer)) != -1)
                baos.write(buffer,0,len);

            byte[] data = baos.toByteArray();
            baos.close();
            is.close();
            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String transBytesToString(byte[] bData){
        String ret = new String(bData);
        return ret;
    }

    private int LoadShader(int type,String shaderSrc){
        int shader;
        int[] compiled = new int[1];

        shader = GLES30.glCreateShader(type);

        //load
        GLES30.glShaderSource(shader,shaderSrc);

        //compile
        GLES30.glCompileShader(shader);

        //check
        GLES30.glGetShaderiv(shader,GLES30.GL_COMPILE_STATUS,compiled,0);
        if(compiled[0] == 0){
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    private void initGLSurface(){
        //init data
        //usr the device hadrware`s native byte order
        // and create a floating point buffer form ByteBuffer
        floatBuffer = ByteBuffer.allocateDirect(vertexDataSource.length * 4)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();

        floatBuffer.put(vertexDataSource);//add the data to floatBuffer
        floatBuffer.position(0);//set the buffer to read the first data

        //init gles
        int vShader,fShader;
        int[] linked = new int[1];

        //load the v and f shader
        vShader = LoadShader(GLES30.GL_VERTEX_SHADER,transBytesToString(readAssetFile("vshader.glsl")));
        fShader = LoadShader(GLES30.GL_FRAGMENT_SHADER,transBytesToString(readAssetFile("fshader.glsl")));

        int program = GLES30.glCreateProgram();

        //attach
        GLES30.glAttachShader(program,vShader);
        GLES30.glAttachShader(program,fShader);

        //Bind vshader Self define vPosition
        GLES30.glBindAttribLocation(program,0,"vPosition");

        //link
        GLES30.glLinkProgram(program);

        //check
        GLES30.glGetProgramiv(program,GLES30.GL_LINK_STATUS,linked,0);
        if(linked[0] == 0){
            GLES30.glDeleteProgram(program);
            program = 0;
            return;
        }
        programObject = program;
        //生成背景
        GLES30.glClearColor(1.0f,1.0f,1.0f,0.0f);
    }

    private void draw(){

        GLES30.glViewport(0,0,width,height);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUseProgram(programObject);

        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,0,floatBuffer);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,3);
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,6);
    }
}
