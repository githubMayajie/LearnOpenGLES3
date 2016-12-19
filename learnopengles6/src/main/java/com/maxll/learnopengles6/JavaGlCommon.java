package com.maxll.learnopengles6;

import android.opengl.GLES30;

/**
 * Created by maxll on 16-12-16.
 */

public class JavaGlCommon {
    public static int loadShader ( int type, String shaderSrc )
    {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES30.glCreateShader ( type );

        if ( shader == 0 )
        {
            return 0;
        }

        // Load the shader source
        GLES30.glShaderSource ( shader, shaderSrc );

        // Compile the shader
        GLES30.glCompileShader ( shader );

        // Check the compile status
        GLES30.glGetShaderiv ( shader, GLES30.GL_COMPILE_STATUS, compiled, 0 );

        if ( compiled[0] == 0 )
        {
            GLES30.glDeleteShader ( shader );
            return 0;
        }

        return shader;
    }

    //
    ///
    /// \brief Load a vertex and fragment shader, create a program object, link
    ///    program.
    /// Errors output to log.
    /// \param vertShaderSrc Vertex shader source code
    /// \param fragShaderSrc Fragment shader source code
    /// \return A new program object linked with the vertex/fragment shader
    ///    pair, 0 on failure
    //
    public static int loadProgram ( String vertShaderSrc, String fragShaderSrc )
    {
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = loadShader ( GLES30.GL_VERTEX_SHADER, vertShaderSrc );

        if ( vertexShader == 0 )
        {
            return 0;
        }

        fragmentShader = loadShader ( GLES30.GL_FRAGMENT_SHADER, fragShaderSrc );

        if ( fragmentShader == 0 )
        {
            GLES30.glDeleteShader ( vertexShader );
            return 0;
        }

        // Create the program object
        programObject = GLES30.glCreateProgram();

        if ( programObject == 0 )
        {
            return 0;
        }

        GLES30.glAttachShader ( programObject, vertexShader );
        GLES30.glAttachShader ( programObject, fragmentShader );

        // Link the program
        GLES30.glLinkProgram ( programObject );

        // Check the link status
        GLES30.glGetProgramiv ( programObject, GLES30.GL_LINK_STATUS, linked, 0 );

        if ( linked[0] == 0 )
        {
            GLES30.glDeleteProgram ( programObject );
            return 0;
        }

        // Free up no longer needed shader resources
        GLES30.glDeleteShader ( vertexShader );
        GLES30.glDeleteShader ( fragmentShader );

        return programObject;
    }

}
