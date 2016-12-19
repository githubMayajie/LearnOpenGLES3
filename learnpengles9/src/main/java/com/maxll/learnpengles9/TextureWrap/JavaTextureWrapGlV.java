package com.maxll.learnpengles9.TextureWrap;

import android.content.Context;
import android.opengl.GLES30;

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

public class JavaTextureWrapGlV extends MyGlSurfaceView {
    private int width,height;
    private int program,samplerLoc,offsetLoc,textureId;
    private FloatBuffer vertices;
    private ShortBuffer indices;
    private final float[] verticesData = {
            -0.3f,0.3f,0.0f,1.0f
            ,-1.0f,-1.0f
            ,-0.3f,-0.3f,0.0f,1.0f
            ,-1.0f,2.0f
            ,0.3f,-0.3f,0.0f,1.0f
            ,2.0f,2.0f,
            0.3f,0.3f,0.0f,1.0f
            ,2.0f,-1.0f
    };
    private final short[] indicesData = {
            0,1,2,0,2,3
    };

    ///
    //  Generate an RGB8 checkerboard image
    //
    private ByteBuffer genCheckImage (int width, int height, int checkSize )
    {
        int x, y;
        byte[] pixels = new byte[width * height * 3];


        for ( y = 0; y < height; y++ )
            for ( x = 0; x < width; x++ )
            {
                byte rColor = 0;
                byte bColor = 0;

                if ( ( x / checkSize ) % 2 == 0 )
                {
                    rColor = ( byte ) ( 127 * ( ( y / checkSize ) % 2 ) );
                    bColor = ( byte ) ( 127 * ( 1 - ( ( y / checkSize ) % 2 ) ) );
                }
                else
                {
                    bColor = ( byte ) ( 127 * ( ( y / checkSize ) % 2 ) );
                    rColor = ( byte ) ( 127 * ( 1 - ( ( y / checkSize ) % 2 ) ) );
                }

                pixels[ ( y * width + x ) * 3] = rColor;
                pixels[ ( y * width + x ) * 3 + 1] = 0;
                pixels[ ( y * width + x ) * 3 + 2] = bColor;
            }

        ByteBuffer result = ByteBuffer.allocateDirect ( width * height * 3 );
        result.put ( pixels ).position ( 0 );
        return result;
    }

    //
    // Create a 2D texture image
    //
    private int createTexture2D( )
    {
        // Texture object handle
        int[] textureId = new int[1];
        int width = 256, height = 256;
        ByteBuffer pixels;

        pixels = genCheckImage ( width, height, 64 );

        // Generate a texture object
        GLES30.glGenTextures ( 1, textureId, 0 );

        // Bind the texture object
        GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, textureId[0] );

        // Load mipmap level 0
        GLES30.glTexImage2D ( GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB, width, height,
                0, GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, pixels );

        // Set the filtering mode
        GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR );
        GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR );

        return textureId[0];
    }


    public JavaTextureWrapGlV(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        vertices = ByteBuffer.allocateDirect ( verticesData.length * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        vertices.put ( verticesData ).position ( 0 );
        indices = ByteBuffer.allocateDirect ( indicesData.length * 2 )
                .order ( ByteOrder.nativeOrder() ).asShortBuffer();
        indices.put ( indicesData ).position ( 0 );

        String vShaderStr =
                "#version 300 es                            \n" +
                        "uniform float u_offset;      				\n" +
                        "layout(location = 0) in vec4 a_position;   \n" +
                        "layout(location = 1) in vec2 a_texCoord;   \n" +
                        "out vec2 v_texCoord;     					\n" +
                        "void main()                  				\n" +
                        "{                            				\n" +
                        "   gl_Position = a_position; 				\n" +
                        "   gl_Position.x += u_offset;				\n" +
                        "   v_texCoord = a_texCoord;  				\n" +
                        "}                            				\n";

        String fShaderStr =
                "#version 300 es                                     \n" +
                        "precision mediump float;                            \n" +
                        "in vec2 v_texCoord;                            	 \n" +
                        "layout(location = 0) out vec4 outColor;             \n" +
                        "uniform sampler2D s_texture;                        \n" +
                        "void main()                                         \n" +
                        "{                                                   \n" +
                        "   outColor = texture( s_texture, v_texCoord );  	 \n" +
                        "}                                                   \n";
        program = ShaderHelper.loadProgram(vShaderStr,fShaderStr);
        samplerLoc = GLES30.glGetUniformLocation(program,"s_texture");
        offsetLoc = GLES30.glGetUniformLocation(program,"u_offset");
        textureId = createTexture2D();
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
        GLES30.glVertexAttribPointer(0,4,GLES30.GL_FLOAT,false,6 * 4,vertices);

        vertices.position(4);
        GLES30.glVertexAttribPointer(1,2,GLES30.GL_FLOAT,false,6 * 4,vertices);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureId);

        GLES30.glUniform1i(samplerLoc,0);

        //Draw quad with repeat unit 0
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_R,GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
        GLES30.glUniform1f(offsetLoc,-0.7f);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,6,GLES30.GL_UNSIGNED_SHORT,indices);


        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_R,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glUniform1f(offsetLoc,0.0f);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,6,GLES30.GL_UNSIGNED_SHORT,indices);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_R,GLES30.GL_MIRRORED_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_MIRRORED_REPEAT);
        GLES30.glUniform1f(offsetLoc,0.7f);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,6,GLES30.GL_UNSIGNED_SHORT,indices);
    }

}
