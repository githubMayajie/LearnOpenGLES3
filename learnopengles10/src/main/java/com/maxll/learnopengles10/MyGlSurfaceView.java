package com.maxll.learnopengles10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.maxll.learnopengles.ShaderHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-19.
 */

public class MyGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private int program,width,height;
    private int baseMapLoc,lightMapLoc;
    private int baseMapTexId,lightMapTexId;

    private FloatBuffer vertices;
    private ShortBuffer indices;
    private final float[] verticesData = {
            -0.5f,0.5f,0.0f
            ,0.0f,0.0f
            ,-0.5f,-0.5f,0.0f
            ,0.0f,1.0f
            ,0.5f,-0.5f,0.0f
            ,1.0f,1.0f
            ,0.5f,0.5f,0.0f
            ,1.0f,0.0f
    };
    private final short[] indicesData = {
            0,1,2,0,2,3
    };

    private Context context;
    public MyGlSurfaceView(Context context) {
        super(context);
        this.context = context;

        setEGLContextClientVersion(3);
        setRenderer(this);
    }

    private int loadTextureFromAsset(String fileName){
        int textureId[] = new int[1];
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            is = null;
            return 0;
        }

        bitmap = BitmapFactory.decodeStream(is);

        GLES30.glGenTextures(1,textureId,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureId[0]);

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0,bitmap,0);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

        return textureId[0];
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        vertices = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertices.put(verticesData).position(0);

        indices = ByteBuffer.allocateDirect(indicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indices.put(indicesData).position(0);

        String vShaderSrc =
                "#version 300 es \n" +
                "layout(location=0) in vec4 a_position; \n" +
                "layout(location=1) in vec2 a_texCoord; \n" +
                "out vec2 v_texCoord;                   \n" +
                "void main()    \n" +
                "{      \n" +
                "   gl_Position = a_position;   \n" +
                "   v_texCoord = a_texCoord;       \n" +
                "}";

        String fShaderSrc =
                "#version 300 es    \n" +
                "precision mediump float;   \n" +
                "in vec2 v_texCoord;        \n" +
                "layout(location=0) out vec4 outColor;  \n" +
                "uniform sampler2D s_baseMap;   \n" +
                "uniform sampler2D s_lightMap;  \n" +
                "void main()    \n" +
                "{  \n" +
                "   vec4 baseColor; \n"+
                "   vec4 lightColor;    \n" +
                "   baseColor = texture(s_baseMap,v_texCoord);    \n"+
                "   lightColor = texture(s_lightMap,v_texCoord);     \n" +
                "   outColor = baseColor * (lightColor + 0.25);     \n" +
                "}";

        program = ShaderHelper.loadProgram(vShaderSrc,fShaderSrc);
        baseMapLoc = GLES30.glGetUniformLocation(program,"s_baseMap");
        lightMapLoc = GLES30.glGetUniformLocation(program,"s_lightMap");

        baseMapTexId = loadTextureFromAsset("basemap.png");
        lightMapTexId = loadTextureFromAsset("lightmap.png");

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

        GLES30.glUseProgram(program);

        vertices.position(0);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,5 * 4,vertices);

        vertices.position(3);
        GLES30.glVertexAttribPointer(1,2,GLES30.GL_FLOAT,false,5 * 4,vertices);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);

        //Binmd the base map
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,baseMapTexId);

        GLES30.glUniform1i(baseMapLoc,0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,lightMapTexId);
        //set the light map sample to texturn unit 1
        GLES30.glUniform1i(lightMapLoc,1);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES,6,GLES30.GL_UNSIGNED_SHORT,indices);
    }
}
