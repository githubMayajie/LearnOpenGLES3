//
// Created by maxll on 16-12-16.
//
#include "GlCommon.h"
#include <stdio.h>

GlCommon* GlCommon::instance = NULL;


GlCommon* GlCommon::getInstance() {
    if(instance == NULL)
        instance = new GlCommon();

    return instance;
}

GLuint GlCommon::LoadShader(GLenum type, const char *src) {
    GLuint shader;
    GLint compiled;

    shader = glCreateShader(type);

    glShaderSource(shader,1,&src,NULL);
    glCompileShader(shader);

    glGetShaderiv(shader,GL_COMPILE_STATUS,&compiled);
    if(!compiled){
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}


GLuint GlCommon::LoadProgram(const char *vShaderSrc, const char *fShaderSrc) {
    GLuint vShader,fShader,program;
    GLint linked;
    vShader = LoadShader(GL_VERTEX_SHADER,vShaderSrc);
    fShader = LoadShader(GL_FRAGMENT_SHADER,fShaderSrc);

    program = glCreateProgram();

    glAttachShader(program,vShader);
    glAttachShader(program,fShader);

    glLinkProgram(program);

    glGetProgramiv(program,GL_LINK_STATUS,&linked);
    if(!linked){
        glDeleteProgram(program);
        return 0;
    }

    glDeleteShader(vShader);
    glDeleteShader(fShader);

    return program;
}


