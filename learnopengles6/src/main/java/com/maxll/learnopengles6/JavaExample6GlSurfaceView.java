package com.maxll.learnopengles6;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-16.
 */

public class JavaExample6GlSurfaceView extends MyGlSurfaceView {

    private int width,height,programObject;
    private FloatBuffer posfb,colorfb;
    private ShortBuffer indexSb;

    private int[] VBOIds = new int[3];
    private final float[] vertexData = {
            0.0f,0.5f,0.0f,
            -0.5f,-0.5f,0.0f,
            0.5f,-0.5f,0.0f
    };
    private final short[] indicesData = {
            0,1,2
    };
    private final float[] colorData = {
            1.0f,0.0f,0.0f,1.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.0f,0.0f,1.0f,1.0f
    };
    final int VERTEX_POS_SIZE = 3;
    final int VERTEX_COLOR_SIZE = 4;
    final int VERTEX_POS_INDX = 0;
    final int VERTEX_COLOR_INDX = 1;
    private int vtxStrides[] = {
            VERTEX_POS_SIZE * 4,
            VERTEX_COLOR_SIZE * 4
    };


    public JavaExample6GlSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //init data
        posfb = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        posfb.put(vertexData).position(0);

        colorfb = ByteBuffer.allocateDirect(colorData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorfb.put(colorData).position(0);

        indexSb = ByteBuffer.allocateDirect(indicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indexSb.put(indicesData).position(0);

        String vShaderStr =
                "#version 300 es                          \n" +
                        "layout(location=0) in vec4 a_position;   \n" +
                        "layout(location=1) in vec4 a_color;       \n" +
                        "out vec4 v_color;                        \n" +
                        "void main()                              \n" +
                        "{                                        \n" +
                        "       v_color = a_color;                \n" +
                        "       gl_Position = a_position;         \n" +
                        "}";
        String fShaderStr =
                "#version 300 es                          \n" +
                        "precision mediump float;                 \n" +
                        "in vec4 v_color;                         \n" +
                        "out vec4 o_fragColor;                    \n" +
                        "void main()                              \n" +
                        "{                                        \n" +
                        "       o_fragColor = v_color;            \n" +
                        "}";
        programObject = JavaGlCommon.loadProgram(vShaderStr,fShaderStr);
        for (int i = 0; i < VBOIds.length; i++)
            VBOIds[i] = 0;

        GLES30.glClearColor(1.0f,1.0f,1.0f,0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glViewport(0,0,width,height);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(programObject);

        drawWithVBOs();
    }

    private void drawWithVBOs(){
        int numVertices = 3;
        int numIndices = 3;
        if(VBOIds[0] == 0 && VBOIds[1] == 0 && VBOIds[2] == 0 ){
            GLES30.glGenBuffers(3,VBOIds,0);

            posfb.position(0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBOIds[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,vtxStrides[0] * numVertices,
                    posfb,GLES30.GL_STATIC_DRAW);

            colorfb.position(0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBOIds[1]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,vtxStrides[1] * numVertices,
                    colorfb,GLES30.GL_STATIC_DRAW);

            indexSb.position(0);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,VBOIds[2]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,2 * numVertices,
                    indexSb,GLES30.GL_STATIC_DRAW);
        }

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBOIds[0]);

        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glVertexAttribPointer(VERTEX_POS_INDX,VERTEX_POS_SIZE,GLES30.GL_FLOAT,
                false,vtxStrides[0],0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBOIds[1]);
        GLES30.glEnableVertexAttribArray(VERTEX_COLOR_INDX);
        GLES30.glVertexAttribPointer(VERTEX_COLOR_INDX,VERTEX_COLOR_SIZE,GLES30.GL_FLOAT,
                false,vtxStrides[1],0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,VBOIds[2]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,numIndices,
                GLES30.GL_UNSIGNED_SHORT,0);

        GLES30.glDisableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glDisableVertexAttribArray(VERTEX_COLOR_INDX);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,0);
    }
}
















