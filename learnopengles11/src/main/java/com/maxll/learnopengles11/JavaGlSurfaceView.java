package com.maxll.learnopengles11;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView;

import com.maxll.learnopengles.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-19.
 */

public class JavaGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private Context context;
    private int program,width,height;
    private int[] fbo = new int[1];

    private int textureWidth,textureHeight;
    private int[] colorTexIds = new int[4];

    private int InitFBO(){
        int defaultFrameBuffer[] = new int[]{0};
        int[] attachments = new int[]{
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_COLOR_ATTACHMENT1,
            GLES30.GL_COLOR_ATTACHMENT2,
            GLES30.GL_COLOR_ATTACHMENT3
        };

        GLES30.glGetIntegerv(GLES30.GL_FRAMEBUFFER_BINDING,defaultFrameBuffer,0);

        //Set up fbo
        GLES30.glGenFramebuffers(1,fbo,0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,fbo[0]);

        //Set up four output buffers and attach to fbo
        textureWidth = 400;
        textureHeight = 400;
        GLES30.glGenTextures(4,colorTexIds,0);
        for (int i = 0; i < 4; i++){
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,colorTexIds[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGB,
                    textureWidth,textureHeight,0,GLES30.GL_RGB,GLES30.GL_UNSIGNED_BYTE,null);

            //Set the filtering mode
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_NEAREST);

            GLES30.glFramebufferTexture2D(GLES30.GL_DRAW_FRAMEBUFFER,attachments[i],
                    GLES30.GL_TEXTURE_2D,colorTexIds[i],0);
        }

        GLES30.glDrawBuffers(4,attachments,0);
        if(GLES30.GL_FRAMEBUFFER_COMPLETE != GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER))
            return 0;

        //Restore the original framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,defaultFrameBuffer[0]);

        return 1;
    }

    public JavaGlSurfaceView(Context context) {
        super(context);
        this.context = context;

        setEGLContextClientVersion(3);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //init
        String vShaderSrc =
                "#version 300 es    \n" +
                "layout(location=0) in vec4 a_position; \n" +
                "void main()    \n" +
                "{  \n"+
                "   gl_Position = a_position;   \n" +
                "}";
        String fShaderSrc =
                "#version 300 es    \n" +
                "precision mediump float;   \n" +
                "layout(location=0) out vec4 fragData0; \n" +
                "layout(location=1) out vec4 fragData1; \n" +
                "layout(location=2) out vec4 fragData2; \n" +
                "layout(location=3) out vec4 fragData3; \n" +
                "void main()        \n" +
                "{                  \n" +
                "   fragData0 = vec4(1,0,0,1);  \n" +
                "   fragData1 = vec4(0,1,0,1);  \n" +
                "   fragData2 = vec4(0,0,1,1);  \n" +
                "   fragData3 = vec4(0.5,0.5,0.5,1);    \n" +
                "}";

        program = ShaderHelper.loadProgram(vShaderSrc,fShaderSrc);
        InitFBO();

        GLES30.glClearColor(1.0f,1.0f,1.0f,0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width =width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        int defaultFrameBuffer[] = new int[]{0};
        int[] attachments = new int[]{
                GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_COLOR_ATTACHMENT1,
                GLES30.GL_COLOR_ATTACHMENT2,
                GLES30.GL_COLOR_ATTACHMENT3
        };

        GLES30.glGetIntegerv(GLES30.GL_FRAMEBUFFER_BINDING,defaultFrameBuffer,0);

        //FIRST use MRT to output four colors to four buffers
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,fbo[0]);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glDrawBuffers(4,attachments,0);
        DrawGeometry();

        //Copy the output gray buffer to upper tight quadrant
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER,defaultFrameBuffer[0]);

        BlitTextures();

    }

    private void BlitTextures(){
        GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER,fbo[0]);

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT0);
        GLES30.glBlitFramebuffer(0,0,textureWidth,textureHeight,
                0,0,width /2,height/2,GLES30.GL_COLOR_BUFFER_BIT,GLES30.GL_LINEAR);

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT1);
        GLES30.glBlitFramebuffer(0,0,textureWidth,textureHeight,
                width / 2,0,width ,height/2,GLES30.GL_COLOR_BUFFER_BIT,GLES30.GL_LINEAR);

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT2);
        GLES30.glBlitFramebuffer(0,0,textureWidth,textureHeight,
                0,height / 2,width /2,height,GLES30.GL_COLOR_BUFFER_BIT,GLES30.GL_LINEAR);

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT3);
        GLES30.glBlitFramebuffer(0,0,textureWidth,textureHeight,
                width / 2,height / 2,width,height,GLES30.GL_COLOR_BUFFER_BIT,GLES30.GL_LINEAR);
    }

    private void DrawGeometry(){
        float[] verticesData = {
                -1.0f,1.0f,0.0f,
                -1.0f,-1.0f,0.0f,
                1.0f,-1.0f,0.0f,
                1.0f,1.0f,0.0f
        };
        short[] indicesData = {
                0,1,2,0,2,3
        };

        FloatBuffer vertices = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertices.put(verticesData).position(0);

        ShortBuffer indices = ByteBuffer.allocateDirect(indicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indices.put(indicesData).position(0);

        GLES30.glViewport(0,0,width,height);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(program);

        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,3 * 4,vertices);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES,6,GLES30.GL_UNSIGNED_SHORT,indices);
    }
}














