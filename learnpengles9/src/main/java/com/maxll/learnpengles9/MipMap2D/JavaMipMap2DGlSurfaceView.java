package com.maxll.learnpengles9.MipMap2D;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.maxll.learnopengles.MipMap2DHelper;
import com.maxll.learnopengles.ShaderHelper;
import com.maxll.learnpengles9.MyGlSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-19.
 */

public class JavaMipMap2DGlSurfaceView extends MyGlSurfaceView {

    private int width,height;
    private int programObject;
    private int samplerLoc;
    private int offsetLoc;
    private int textureId;
    private FloatBuffer vertices;
    private ShortBuffer indices;

    private final float[] verticesData = {
            -0.5f,0.5f,0.0f,1.5f //position 0
            ,0.0f,0.0f
            ,-0.5f,-0.5f,0.0f,0.75f
            ,0.0f,1.0f
            ,0.5f,-0.5f,0.0f,0.75f
            ,1.0f,1.0f
            ,0.5f,0.5f,0.0f,1.5f
            ,1.0f,0.0f
    };

    private final short[] indicesData = {
            0,1,2,0,2,3
    };

    private int createMipMappedTexture2D(){
        int[] textureId = new int[1];
        int wid = 256,hei = 256;
        int level;
        byte[] pixels;
        byte[] prevImage;
        byte[] newImage;
        pixels = MipMap2DHelper.genCheckImage(wid,hei,8);
        GLES30.glGenTextures(1,textureId,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureId[0]);

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(wid * hei * 3);
        pixelBuffer.put(pixels).position(0);

        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGB,wid,hei,
                0,GLES30.GL_RGB,GLES30.GL_UNSIGNED_BYTE,pixelBuffer);
        level = 1;
        prevImage = pixels;
        while (wid > 1 && hei > 1){
            int newWidth,newHeight;
            newWidth = wid / 2;
            if(newWidth <= 0){
                newWidth = 1;
            }

            newHeight = hei / 2;
            if(newHeight <= 0)
                newHeight = 1;

            newImage = MipMap2DHelper.genMipMap2D(prevImage,wid,hei,newWidth,newHeight);

            //Load the mipmap level
            pixelBuffer = ByteBuffer.allocateDirect(newWidth * newHeight * 3);
            pixelBuffer.put(newImage).position(0);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,level,GLES30.GL_RGB,newWidth,newHeight,0,
                    GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE,pixelBuffer);

            prevImage = newImage;
            level++;

            wid = newWidth;
            hei = newHeight;
        }

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST_MIPMAP_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR);

        return textureId[0];
    }

    public JavaMipMap2DGlSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //init data
        vertices = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertices.put(verticesData).position(0);

        indices = ByteBuffer.allocateDirect(indicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indices.put(indicesData).position(0);


        //init gles
        String vShaderSrc =
                "#version 300 es    \n" +
                "uniform float u_offset;    \n" +
                "layout(location=0) in vec4 a_position; \n" +
                "layout(location=1) in vec2 a_texCoord; \n" +
                "out vec2 v_texCoord;   \n" +
                "void main()        \n" +
                "{      \n" +
                "   gl_Position = a_position;   \n" +
                "   gl_Position.x += u_offset;  \n" +
                "   v_texCoord = a_texCoord;    \n" +
                "}";

        String fShaderSrc =
                "#version 300 es\n"+
                "precision mediump float;   \n" +
                "in vec2 v_texCoord;    \n"+
                "layout(location=0) out vec4 outColor;  \n"+
                "uniform sampler2D s_texture;   \n" +
                "void main()    \n"+
                "{  \n" +
                "   outColor = texture(s_texture,v_texCoord);   \n"+
                "}";

        programObject = ShaderHelper.loadProgram(vShaderSrc,fShaderSrc);
        samplerLoc = GLES30.glGetUniformLocation(programObject,"s_texture");
        offsetLoc = GLES30.glGetUniformLocation(programObject,"u_offset");
        textureId = createMipMappedTexture2D();
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
        vertices.position(0);
        GLES30.glVertexAttribPointer(0,4,GLES30.GL_FLOAT,false,6 * 4,vertices);

        vertices.position(4);
        GLES30.glVertexAttribPointer(1,2,GLES30.GL_FLOAT,false,6 * 4,vertices);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);

        //Draw quad with nearest sampliing
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        GLES30.glUniform1f(offsetLoc,-0.6f);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,6,GLES30.GL_UNSIGNED_SHORT,indices);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glUniform1f(offsetLoc,0.6f);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,6,GLES30.GL_UNSIGNED_SHORT,indices);
    }
}
