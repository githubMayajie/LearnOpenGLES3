package com.maxll.learnopengles6;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-16.
 */

public class JavaMapBufferGlSurfaceView extends MyGlSurfaceView {
    private int width,height,programObject;
    private FloatBuffer vtxMappedBuf;
    private ShortBuffer idxMappedBuf;
    private int[] VBOIds = new int[2];

    private final float[] vertexData = {
            0.0f,0.5f,0.0f,
            1.0f,0.0f,0.0f,1.0f,
            -0.5f,-0.5f,0.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.5f,-0.5f,0.0f,
            0.0f,0.0f,1.0f,1.0f
    };
    private final short[] indicesData = {0,1,2};

    final int VERTEX_POS_SIZE = 3;
    final int VERTEX_COLOR_SIZE = 4;
    final int VERTEX_POS_INDX = 0;
    final int VERTEX_COLOR_INDX = 1;



    public JavaMapBufferGlSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //
        String vShaderStr =
                        "#version 300 es                          \n" +
                        "layout(location=0) in vec4 a_position;   \n" +
                        "layout(location=1) in vec4 a_color;      \n" +
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
        VBOIds[0] = 0;
        VBOIds[1] = 0;

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

        drawWithoVBOMapBuffers();
    }

    private void drawWithoVBOMapBuffers(){
        int offset = 0;
        int numVertices = 3;
        int numIndices = 3;
        int vtxStride = 4 * (VERTEX_POS_SIZE + VERTEX_COLOR_SIZE);

        if(VBOIds[0] == 0 && VBOIds[1] == 0){
            GLES30.glGenBuffers(2,VBOIds,0);

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBOIds[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,vtxStride * numVertices,
                    null,GLES30.GL_STATIC_DRAW);

            vtxMappedBuf = ((ByteBuffer)GLES30.glMapBufferRange(GLES30.GL_ARRAY_BUFFER,0,
                    vtxStride * numVertices,
                    GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT))
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            //copy the data to mapped buffer
            vtxMappedBuf.put(vertexData).position(0);

            //unmap the buffer
            GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);

            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,VBOIds[1]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,2 * numIndices,
                    null,GLES30.GL_STATIC_DRAW);

            idxMappedBuf = ((ByteBuffer)GLES30.glMapBufferRange(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                    0,2 * numIndices,GLES30.GL_MAP_WRITE_BIT|GLES30.GL_MAP_INVALIDATE_BUFFER_BIT))
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            idxMappedBuf.put(indicesData).position(0);
            GLES30.glUnmapBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER);
        }

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBOIds[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,VBOIds[1]);

        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glEnableVertexAttribArray(VERTEX_COLOR_INDX);

        GLES30.glVertexAttribPointer(VERTEX_POS_INDX,VERTEX_POS_SIZE,GLES30.GL_FLOAT,
                false,vtxStride,offset);
        offset += (VERTEX_POS_SIZE * 4);
        GLES30.glVertexAttribPointer(VERTEX_COLOR_INDX,VERTEX_COLOR_SIZE,GLES30.GL_FLOAT,
                false,vtxStride,offset);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,numIndices,GLES30.GL_UNSIGNED_SHORT,0);

        GLES30.glDisableVertexAttribArray(VERTEX_COLOR_INDX);
        GLES30.glDisableVertexAttribArray(VERTEX_POS_INDX);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,0);
    }
}





























