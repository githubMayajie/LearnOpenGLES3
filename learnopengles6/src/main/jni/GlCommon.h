//
// Created by maxll on 16-12-16.
//

#ifndef LEARNOPENGLES301_GLCOMMON_H
#define LEARNOPENGLES301_GLCOMMON_H

#include "../../../../nativeCommon/Include/esUtil.h"


class GlCommon {

public:
    static GlCommon* getInstance();

    GLuint LoadShader(GLenum type,const char* src);
    GLuint LoadProgram(const char* vShaderSrc,const char* fShaderSrc);
private:
    static GlCommon* instance;
};


#endif //LEARNOPENGLES301_GLCOMMON_H
