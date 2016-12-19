//
// Created by maxll on 16-12-16.
//

#ifndef LEARNOPENGLES9_GLCOMMON_H
#define LEARNOPENGLES9_GLCOMMON_H

#include "../../../../nativeCommon/Include/esUtil.h"


class GlCommon {

public:
    static GlCommon* getInstance();

    GLuint LoadShader(GLenum type,const char* src);
    GLuint LoadProgram(const char* vShaderSrc,const char* fShaderSrc);

    GLuint createSimpleTexture2D();

    GLboolean GenMipMap2D ( GLubyte *src, GLubyte **dst, int srcWidth, int srcHeight,
                            int *dstWidth, int *dstHeight );
    GLubyte *GenCheckImage ( int width, int height, int checkSize );

private:
    static GlCommon* instance;
};


#endif //LEARNOPENGLES301_GLCOMMON_H
