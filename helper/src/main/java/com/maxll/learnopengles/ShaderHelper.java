package com.maxll.learnopengles;

import android.content.Context;
import android.opengl.GLES30;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by maxll on 16-12-17.
 */

public class ShaderHelper {

    public static String readShader(Context context,String fileName){
        String ret = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);

//            ret = new String(buffer);
//
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(buffer);

            ret = baos.toString();

            is.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int loadShader(int type,String shaderSrc){
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader,shaderSrc);
        GLES30.glCompileShader(shader);

        int compile[] = new int[1];
        GLES30.glGetShaderiv(shader,GLES30.GL_COMPILE_STATUS,compile,0);
        if(compile[0] == 0){
            GLES30.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public static int loadProgram(String vShaderSrc,String fShaderSrc){
        int program = GLES30.glCreateProgram();

        int vShader = loadShader(GLES30.GL_VERTEX_SHADER,vShaderSrc);
        int fShader = loadShader(GLES30.GL_FRAGMENT_SHADER,fShaderSrc);

        GLES30.glAttachShader(program,vShader);
        GLES30.glAttachShader(program,fShader);

        GLES30.glLinkProgram(program);

        int linkResult[] = new int[1];
        GLES30.glGetProgramiv(program,GLES30.GL_LINK_STATUS,linkResult,0);
        if(linkResult[0] == 0){
            GLES30.glDeleteProgram(program);
            return 0;
        }

        GLES30.glDeleteShader(vShader);
        GLES30.glDeleteShader(fShader);

        return program;
    }
}
