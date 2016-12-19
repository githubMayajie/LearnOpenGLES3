package com.maxll.learnopengles8;

import android.content.Context;
import android.opengl.GLES30;
import android.os.SystemClock;

import com.maxll.learnopengles.ShaderHelper;
import com.maxll.learnopengles.ShaperHelper;
import com.maxll.learnopengles.TransformHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-17.
 */

public class JavaGlSurfaceView extends MyGlSurface {
    private int width,height;
    private int programObj;
    private int mvpLoc;
    private ShaperHelper cube = new ShaperHelper();
    private TransformHelper mMVPMatrix = new TransformHelper();
    private long lastTime = 0;
    float angle;

    public JavaGlSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vShaderSrc =
                        "#version 300 es  \n" +
                        "uniform mat4 u_mvpMatrix;      \n" +
                        "layout(location=0) in vec4 a_position; \n" +
                        "layout(location=1) in vec4 a_color;     \n" +
                        "out vec4 v_color;      \n" +
                        "void main() \n" +
                        "{ \n" +
                        "   v_color = a_color;\n" +
                        "   gl_Position = u_mvpMatrix * a_position; \n" +
                        "}";
        String fShaderSrc =
                "#version 300 es \n" +
                "precision mediump float;\n" +
                "in vec4 v_color;   \n" +
                "layout(location=0) out vec4 outColor;  \n" +
                "void main() \n" +
                "{  \n" +
                "   outColor = v_color;\n" +
                "}";

        programObj = ShaderHelper.loadProgram(vShaderSrc,fShaderSrc);
        mvpLoc = GLES30.glGetUniformLocation(programObj,"u_mvpMatrix");
        //generate the vertex data
        cube.genCube(1.0f);

        //Starting rotation angle for the cube
        angle = 45.0f;

        GLES30.glClearColor(1.0f,1.0f,1.0f,0.0f);
    }

    private void update(){
        if(lastTime == 0)
            lastTime = SystemClock.uptimeMillis();

        long curTime = SystemClock.uptimeMillis();
        long elapsedTime = curTime - lastTime;
        float deltaTime = elapsedTime / 1000.0f;
        lastTime = curTime;

        TransformHelper perspective = new TransformHelper();
        TransformHelper modelView = new TransformHelper();
        float aspect;

        angle += (deltaTime * 40.0f);
        if(angle >= 360.0f)
            angle -= 360.0f;

        aspect = (float) width / (float) height;

        //Generate a persective matrix witho a 60 defree FOV
        perspective.matrixLoadIdentity();
        perspective.perspective(60.0f,aspect,1.0f,20.0f);

        //Generate a model view matrix to rotate/translate the cube
        modelView.matrixLoadIdentity();

        //translate away form viewer;
        modelView.translate(0.0f,0.0f,-2.0f);

        //rotate the cube
        modelView.rotate(angle,1.0f,0.0f,1.0f);

        mMVPMatrix.matrixMultiply(modelView.get(),perspective.get());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width =width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        update();

        GLES30.glViewport(0,0,width,height);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(programObj);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,0,cube.getVertices());
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttrib4f(1,1.0f,0.0f,0.0f,1.0f);
        GLES30.glUniformMatrix4fv(mvpLoc,1,false,mMVPMatrix.getAsFloatBuffer());
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,cube.getNumIndices(),GLES30.GL_UNSIGNED_SHORT,
                cube.getIndices());
    }
}
