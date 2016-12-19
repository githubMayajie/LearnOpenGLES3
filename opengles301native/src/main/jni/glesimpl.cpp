//
// Created by maxll on 16-12-15.
//
#include <jni.h>
#include "../../../../nativeCommon/Include/esUtil.h"

#ifdef __cplusplus
extern "C"{
#endif


typedef struct {
    GLuint programObject;
}UserData;


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

int Init(ESContext* esContext){
    UserData* userData = (UserData *) esContext->userData;
    char vshaderSrc[] =
                    "#version 300 es                            \n"
                    "layout(location=0) in vec4 vPosition;    \n"
                    "void main()                                \n"
                    "{                                          \n"
                    "   gl_Position = vPosition;                \n"
                    "}                                          \n";
    char fshaderSrc[] =
                    "#version 300 es                            \n"
                    "precision mediump float;                   \n"
                    "out vec4 fragColor;                        \n"
                    "void main()                                \n"
                    "{                                          \n"
                    "   fragColor = vec4(1.0,0.0,0.0,1.0);      \n"
                    "}                                          \n";

    GLuint vertexShader,fragmentShader,program;
    GLint linked;

    vertexShader = LoadShader(GL_VERTEX_SHADER,vshaderSrc);
    fragmentShader = LoadShader(GL_FRAGMENT_SHADER,fshaderSrc);

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
            esLogMessage("Error:\n%s\n",info);
            free(info);
        }

        glDeleteProgram(program);
        return FALSE;
    }

    userData->programObject = program;
    glClearColor(1.0f,1.0f,1.0f,0.0f);
    return TRUE;
}

void draw(ESContext* esContext){
    UserData* userData = (UserData *) esContext->userData;

    GLfloat vertexData[] = {
            0.0f,0.5f,0.0f,
            -0.5f,-0.5f,0.0f,
            0.5f,-0.5f,0.0f
    };

    glViewport(0,0,esContext->width,esContext->height);

    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(userData->programObject);

    glVertexAttribPointer(0,3,GL_FLOAT,GL_FALSE,0,vertexData);
    glEnableVertexAttribArray(0);

    glDrawArrays(GL_TRIANGLES,0,3);
}

void shutdown(ESContext* esContext){
    UserData* userData = (UserData *) esContext->userData;
    glDeleteProgram(userData->programObject);
}

int esMain(ESContext* esContext){
    esContext->userData = malloc(sizeof(UserData));

    esCreateWindow(esContext,"Hello Triangle",320,240,ES_WINDOW_RGB);
    if(!Init(esContext))
        return GL_FALSE;

    esRegisterShutdownFunc(esContext,shutdown);
    esRegisterDrawFunc(esContext,draw);

    return GL_TRUE;
}

#ifdef __cplusplus
};
#endif