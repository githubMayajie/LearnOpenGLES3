//
// Created by maxll on 16-12-19.
//
#include <jni.h>
#include "GlCommon.h"

#ifdef __cplusplus
extern "C" {
#endif

GLuint programObj;
GLuint width, height;
GLuint textureId;
GLuint sampleLoc;



JNIEXPORT void JNICALL
Java_com_maxll_learnpengles9_JniSimpleTexture2D_init(JNIEnv *env, jobject instance) {
    const char *vShaderSrc =
            "#version 300 es    \n"
                    "layout(location=0) in vec4 a_position;\n"
                    "layout(location=1) in vec2 a_texCoord;\n"
                    "out vec2 v_texCoord;                   \n"
                    "void main()                            \n"
                    "{  \n"
                    "   gl_Position = a_position;\n"
                    "   v_texCoord = a_texCoord;\n"
                    "}";
    const char *fShaderSrc =
            "#version 300 es       \n"
                    "precision mediump float;   \n"
                    "in vec2 v_texCoord;        \n"
                    "layout(location=0) out vec4 outColor;  \n"
                    "uniform sampler2D s_texture;       \n"
                    "void main()    \n"
                    "{  \n"
                    "   outColor = texture(s_texture,v_texCoord);\n"
                    "}";

    programObj = GlCommon::getInstance()->LoadProgram(vShaderSrc, fShaderSrc);
    sampleLoc = glGetUniformLocation(programObj, "s_texture");
    textureId = GlCommon::getInstance()->createSimpleTexture2D();
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
}


JNIEXPORT void JNICALL
Java_com_maxll_learnpengles9_JniSimpleTexture2D_onSizeChange(JNIEnv *env, jobject instance,
                                                             jint wid, jint hei) {
    width = wid;
    height = hei;
}

JNIEXPORT void JNICALL
Java_com_maxll_learnpengles9_JniSimpleTexture2D_jniDraw(JNIEnv *env, jobject instance) {
    GLfloat vertices[] = {
            -0.5f, 0.5f, 0.0f,
            0.0f, 0.0f,

            -0.5f, -0.5f, 0.0f,
            0.0f, 1.0f,

            0.5f, -0.5f, 0.0f,
            1.0f, 1.0f,

            0.5f, 0.5f, 0.0f,
            1.0f, 0.0f
    };

    GLushort indices[] = {
            0, 1, 2, 0, 2, 3
    };

    glViewport(0, 0, width, height);
    glClear(GL_COLOR_BUFFER_BIT);
    glUseProgram(programObj);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FLOAT, 5 * sizeof(GLfloat), vertices);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FLOAT, 5 * sizeof(GLfloat), vertices);

    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textureId);

    //set the sample texture unit to 0
    glUniform1i(sampleLoc, 0);

    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indices);
}


GLuint offsetLoc;

GLuint createMipMappedTexture2D(){
    GLuint textureId;
    int wid = 256,hei = 256;
    int level;
    GLubyte* pixels;
    GLubyte* prevImage;
    GLubyte* newImage;

    pixels = GlCommon::getInstance()->GenCheckImage(wid,hei,8);
    if(pixels == NULL)
        return 0;

    glGenTextures(1,&textureId);

    glBindTexture(GL_TEXTURE_2D,textureId);

    glTexImage2D(GL_TEXTURE_2D,0,GL_RGB,wid,hei,0,GL_RGB,GL_UNSIGNED_BYTE,pixels);
    level = 1;
    prevImage = &pixels[0];

    while (wid > 1 && hei > 1){
        int newWidth,newHeight;
        GlCommon::getInstance()->GenMipMap2D(prevImage,&newImage,wid,hei,&newWidth,&newHeight);

        glTexImage2D(GL_TEXTURE_2D,level,GL_RGB,newWidth,newHeight,0,GL_RGB,GL_UNSIGNED_BYTE,newImage);

        free(prevImage);

        prevImage = newImage;
        level++;

        wid = newWidth;
        hei = newHeight;
    }

    free(newImage);

    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST_MIPMAP_NEAREST);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
}

JNIEXPORT void JNICALL
Java_com_maxll_learnpengles9_MipMap2D_JniMipMap2DGlSurfaceView_init(JNIEnv *env, jobject instance) {


}

JNIEXPORT void JNICALL
Java_com_maxll_learnpengles9_MipMap2D_JniMipMap2DGlSurfaceView_onChangeSize(JNIEnv *env,
                                                                            jobject instance,
                                                                            jint width,
                                                                            jint height) {

    // TODO

}

JNIEXPORT void JNICALL
Java_com_maxll_learnpengles9_MipMap2D_JniMipMap2DGlSurfaceView_jniDraw(JNIEnv *env,
                                                                       jobject instance) {

    // TODO

}


#ifdef __cplusplus
}
#endif
























