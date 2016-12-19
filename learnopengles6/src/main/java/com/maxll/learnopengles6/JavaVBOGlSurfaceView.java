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

public class JavaVBOGlSurfaceView extends MyGlSurfaceView {

    private int programObject,width,height;
    private FloatBuffer vertexfb,vertexfb1;
    private ShortBuffer indicesfb;
    private int[] vboIds = new int[2];

    private final float[] vertexData = {
            -0.5f,0.5f,0.0f,        //v0
            1.0f,0.0f,0.0f,1.0f,    //color 0
            -1.0f,-0.5f,0.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.0f,-0.5f,0.0f,
            0.0f,0.0f,1.0f,1.0f
    };

    private final short[] indicesData = {
            0,1,2
    };

    private final int VERTEX_POS_SIZE = 3;//x,y and z
    private final int VERTEX_COLOR_SIZE = 4;// r g b a

    private final int VERTEX_POS_INDX = 0;
    private final int VERTEX_COLOR_INDX = 1;

    private final int VERTEX_STRIDE = (4 * (VERTEX_COLOR_SIZE + VERTEX_POS_SIZE));


    public JavaVBOGlSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        draw();
    }

    private void init(){
        //init data
        vertexfb = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexfb.put(vertexData).position(0);

        //modity data
        for (int i = 0;i < 3;i++){
            vertexData[i * (VERTEX_POS_SIZE + VERTEX_COLOR_SIZE) + 0] += 1.0f;
        }

        vertexfb1 = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexfb1.put(vertexData).position(0);

        indicesfb = ByteBuffer.allocateDirect(indicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesfb.put(indicesData).position(0);

        //
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
        vboIds[0] = 0;
        vboIds[1] = 0;

        GLES30.glClearColor(1.0f,1.0f,1.0f,0.0f);
    }

    private void draw(){
        GLES30.glViewport(0,0,width,height);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(programObject);

        drawWithoutVBO();
//        drawWithVBO();
        drawWithVBO();
    }

    private void drawWithoutVBO(){
        int numIndices = 3;
        int vtxStride = 4 * (VERTEX_COLOR_SIZE + VERTEX_POS_SIZE);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,0);

        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glEnableVertexAttribArray(VERTEX_COLOR_INDX);

        vertexfb.position(0);
        GLES30.glVertexAttribPointer(VERTEX_POS_INDX,VERTEX_POS_SIZE,GLES30.GL_FLOAT,false,
                    vtxStride,vertexfb);

        vertexfb.position(VERTEX_POS_SIZE);
        GLES30.glVertexAttribPointer(VERTEX_COLOR_INDX,VERTEX_COLOR_SIZE,GLES30.GL_FLOAT,false,
                    vtxStride,vertexfb);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,numIndices,GLES30.GL_UNSIGNED_SHORT,indicesfb);

        GLES30.glDisableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glDisableVertexAttribArray(VERTEX_COLOR_INDX);
    }

    private void drawWithVBO(){
        int offset = 0;
        int numVertices = 3;
        int numIndices = 3;
        int vtxStride = 4 * (VERTEX_COLOR_SIZE + VERTEX_POS_SIZE);

        //vboids[0] used to store vertex attribute data
        //vboids[1] used to store element indics
        if(vboIds[0] == 0 && vboIds[1] == 0){
            GLES30.glGenBuffers(2,vboIds,0);
            vertexfb1.position(0);

            //GL_ARRAY_BUFFER //zuo biao color
            //GL_ELEMENT_ATTAY_BUFFER //suo yin zuo biao
            //GL_TEXTURE_BUFFER //wen li huan cun
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vboIds[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,vtxStride * numVertices,
                                vertexfb1,GLES30.GL_STATIC_DRAW);

            indicesfb.position(0);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,vboIds[1]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,2 * numIndices,
                    indicesfb,GLES30.GL_STATIC_DRAW);
        }
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vboIds[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,vboIds[1]);

        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glEnableVertexAttribArray(VERTEX_COLOR_INDX);

        GLES30.glVertexAttribPointer(VERTEX_POS_INDX,VERTEX_POS_SIZE,GLES30.GL_FLOAT,
                false,vtxStride,offset);
        offset += VERTEX_POS_SIZE * 4;
        GLES30.glVertexAttribPointer(VERTEX_COLOR_INDX,VERTEX_POS_SIZE,GLES30.GL_FLOAT,
                false,vtxStride,offset);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,numIndices, GLES30.GL_UNSIGNED_SHORT,0);

        GLES30.glDisableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glDisableVertexAttribArray(VERTEX_COLOR_INDX);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,0);
    }
}













