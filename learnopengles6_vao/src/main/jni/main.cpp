//
// Created by maxll on 16-12-15.
//

#include <jni.h>
#include "../../../../nativeCommon/Include/esUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

GLuint g_program;
GLuint g_width,g_height;

GLuint LoadShader(GLenum type,const char* shaderSrc){
    GLuint shader;
    GLint complied;

    shader = glCreateShader(type);

    glShaderSource(shader,1,&shaderSrc,NULL);
    glCompileShader(shader);

    glGetShaderiv(shader,GL_COMPILE_STATUS,&complied);
    if(!complied){
        GLint infoLen = 0;
        glDeleteShader(shader);
        return 0;
    }

    return shader;
}




JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6vao_JNIGlSurfaceView_init(JNIEnv* env,jobject obj){
    char vShaderSrc[] =
            "#version 300 es                        \n"
            "layout(location=0) in vec4 a_color;    \n"
            "layout(location=1) in vec4 a_position; \n"
            "out vec4 v_color;                      \n"
            "void main()                            \n"
            "{                                      \n"
            "      v_color = a_color;               \n"
            "       gl_Position = a_position;       \n"
            "}";
    char fShaderSrc[] =
            "#version 300 es                        \n"
            "precision mediump float;               \n"
            "in vec4 v_color;                       \n"
            "out vec4 o_fragColor;                  \n"
            "void main()                            \n"
            "{                                      \n"
            "      o_fragColor = v_color;           \n"
            "}";

    GLuint vertexShader,fragmentShader,program;
    GLint linked;

    vertexShader = LoadShader(GL_VERTEX_SHADER,vShaderSrc);
    fragmentShader = LoadShader(GL_FRAGMENT_SHADER,fShaderSrc);

    program = glCreateProgram();

    glAttachShader(program,vertexShader);
    glAttachShader(program,fragmentShader);

    glLinkProgram(program);

    //check
    glGetProgramiv(program,GL_LINK_STATUS,&linked);
    if(!linked){
        GLint infoLen = 0;
        glGetProgramiv(program,GL_INFO_LOG_LENGTH,&infoLen);
        if(infoLen > 1){
            char* info = (char *) malloc(infoLen * sizeof(char));
            glGetProgramInfoLog(program,infoLen,NULL,info);
//            esLogMessage("Error:\n%s\n",info);
            free(info);
        }

        glDeleteProgram(program);
        return;
    }

    //free uo no langer needed shader resource
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);

    g_program = program;

    glClearColor(1.0f,1.0f,1.0f,0.0f);
}

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6vao_JNIGlSurfaceView_size(JNIEnv *env, jobject instance, jint width,
                                                         jint height) {
    g_width = width;
    g_height = height;
}

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6vao_JNIGlSurfaceView_draw(JNIEnv *env, jobject instance) {

    float data[] = {
            0.0f,0.5f,0.0f,
            -0.5f,-0.5f,0.0f,
            0.5f,-0.5f,0.0f
    };

    glViewport(0,0,g_width,g_height);

    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(g_program);

    glVertexAttrib4f(0,1.0f,0.0f,0.0f,1.0f);//red

    glVertexAttribPointer(1,3,GL_FLOAT,GL_FALSE,0,data);

    glEnableVertexAttribArray(1);
    glDrawArrays(GL_TRIANGLES,0,3);
    glDisableVertexAttribArray(1);

}

#ifdef __cplusplus
}
#endif
