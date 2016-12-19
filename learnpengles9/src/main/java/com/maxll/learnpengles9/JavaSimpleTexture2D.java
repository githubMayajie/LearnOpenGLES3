package com.maxll.learnpengles9;

import android.content.Context;
import android.opengl.GLES30;

import com.maxll.learnopengles.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-18.
 */

public class JavaSimpleTexture2D extends MyGlSurfaceView {

    private int width,height,programObj;
    private int sampleLoc,textureId;
    private FloatBuffer vertices;
    private ShortBuffer indices;
    private final float[] vertexData = {
            -0.5f, 0.5f, 0.0f, // Position 0
            0.0f, 0.0f, // TexCoord 0
            -0.5f, -0.5f, 0.0f, // Position 1
            0.0f, 1.0f, // TexCoord 1
            0.5f, -0.5f, 0.0f, // Position 2
            1.0f, 1.0f, // TexCoord 2
            0.5f, 0.5f, 0.0f, // Position 3
            1.0f, 0.0f // TexCoord 3
    };
    private final short[] indexData = {
            0,1,2,0,2,3
    };

    public JavaSimpleTexture2D(Context context) {
        super(context);
    }

    private int createSimpleTexture2D(){
        int[] texId = new int[1];

        //2*2 image RGB
        byte[] pixels = {
                (byte)0xff,0,0,
                0,(byte)0xff,0,
                0,0,(byte)0xff,
                (byte)0xff,(byte)0xff,0
        };
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4 * 3);
        pixelBuffer.put(pixels).position(0);

        //use tightly packed data
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT,1);

        //generate a texture object
        GLES30.glGenTextures(1,texId,0);

        //Bind the texture object
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texId[0]);

        //Load the texture
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGB,2,2,0,
                GLES30.GL_RGB,GLES30.GL_UNSIGNED_BYTE,pixelBuffer);

        //set The filtering mode
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_NEAREST);

        return texId[0];
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        vertices = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertices.put(vertexData).position(0);

        indices = ByteBuffer.allocateDirect(indexData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indices.put(indexData).position(0);

        String vShaderSrc =
                "#version 300 es    \n" +
                "layout(location=0) in vec4 a_position; \n" +
                "layout(location=1) in vec2 a_texCoord; \n" +
                "out vec2 v_texCoord;                   \n" +
                "void main()                            \n" +
                "{                                      \n" +
                "   gl_Position = a_position;           \n" +
                "   v_texCoord = a_texCoord;            \n" +
                "}";

        String fShaderSrc =
                "#version 300 es        \n" +
                "precision mediump float;       \n" +
                "in vec2 v_texCoord;            \n" +
                "layout(location=0)out vec4 outColor;       \n" +
                "uniform sampler2D s_texture;               \n" +
                "void main()                                \n" +
                "{                                          \n" +
                "   outColor = texture(s_texture,v_texCoord);   \n" +
                "}";
        programObj = ShaderHelper.loadProgram(vShaderSrc,fShaderSrc);
        sampleLoc = GLES30.glGetUniformLocation(programObj,"s_texture");
        //load the texture
        textureId = createSimpleTexture2D();

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

        GLES30.glUseProgram(programObj);

        //load the vertex position
        vertices.position(0);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,5 * 4,vertices);

        //load the texture coordinate
        vertices.position(3);
        GLES30.glVertexAttribPointer(1,2,GLES30.GL_FLOAT,false,5 * 4,vertices);

        //bind the texture
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);

        //Bind the texture
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureId);

        //Set the sampler texture unit to 0
        GLES30.glUniform1i(sampleLoc,0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES,6,GLES30.GL_UNSIGNED_SHORT,indices);
    }
}















