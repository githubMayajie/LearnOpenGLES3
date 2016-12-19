//
// Created by maxll on 16-12-16.
//

#include <jni.h>
#include "../../../../nativeCommon/Include/esUtil.h"
#include "GlCommon.h"

#ifdef __cplusplus
extern "C" {
#endif

GLuint g_width,g_height;
GLuint g_program;
GLuint g_vboIds[2];
GLuint g_VBOIds[3];
GLuint g_offsetLoc;

#define VERTEX_POS_SIZE     3
#define VERTEX_COLOR_SIZE   4

#define VERTEX_POS_INDX     0
#define VERTEX_COLOR_INDX   1

void drawWithoutVBOs(GLfloat* vertices,GLint vtxStride,GLuint numIndices,GLushort* indices);
void drawWithVBOs(GLint numVertices,GLfloat* vtxBuf,GLint vtxStride,GLint numIndices,
                  GLushort* indices);

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIVBOGlSurfaceView_init(JNIEnv* env,jobject obj){

    char vShaderStr[] =
            "#version 300 es                          \n"
            "layout(location=0) in vec4 a_position;   \n"
            "layout(location=1) in vec4 a_color;      \n"
            "uniform float u_offset;                  \n"
            "out vec4 v_color;                        \n"
            "void main()                              \n"
            "{                                        \n"
            "       v_color = a_color;                \n"
            "       gl_Position = a_position;         \n"
            "       gl_Position.x += u_offset;        \n"
            "}";
    char fShaderStr[] =
            "#version 300 es                          \n"
            "precision mediump float;                 \n"
            "in vec4 v_color;                         \n"
            "out vec4 o_fragColor;                    \n"
            "void main()                              \n"
            "{                                        \n"
            "       o_fragColor = v_color;            \n"
            "}";

    g_program = GlCommon::getInstance()->LoadProgram(vShaderStr,fShaderStr);
    g_offsetLoc = glGetUniformLocation(g_program,"u_offset");

    g_vboIds[0] = 0;
    g_vboIds[1] = 0;
    glClearColor(1.0f,1.0f,1.0f,0.0f);
}


JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIVBOGlSurfaceView_onChangeSize(JNIEnv *env, jobject instance,
                                                               jint width, jint height) {
    g_width = width;
    g_height = height;
}

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIVBOGlSurfaceView_jnidraw(JNIEnv* env,jobject obj){


     float vertexData[3 * (VERTEX_COLOR_SIZE + VERTEX_POS_SIZE)] = {
            -0.5f,0.5f,0.0f,        //v0
            1.0f,0.0f,0.0f,1.0f,    //color 0
            -1.0f,-0.5f,0.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.0f,-0.5f,0.0f,
            0.0f,0.0f,1.0f,1.0f
    };

    //index buffer data
    GLushort indicesData[] = {
            0,1,2
    };
    int VERTEX_STRIDE = (sizeof(GLfloat) * (VERTEX_COLOR_SIZE + VERTEX_POS_SIZE));
    glViewport(0,0,g_width,g_height);
    glClear(GL_COLOR_BUFFER_BIT);
    glUseProgram(g_program);
    glUniform1f(g_offsetLoc,0.0f);
    drawWithoutVBOs(vertexData,VERTEX_STRIDE,3,indicesData);

    glUniform1f(g_offsetLoc,1.0f);
    drawWithVBOs(3,vertexData,VERTEX_STRIDE,3,indicesData);

}

void drawWithoutVBOs(GLfloat* vertices,GLint vtxStride,GLuint numIndices,GLushort* indices){
    GLfloat* vtxBuf = vertices;

    glBindBuffer(GL_ARRAY_BUFFER,0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);

    glEnableVertexAttribArray(VERTEX_COLOR_INDX);
    glEnableVertexAttribArray(VERTEX_POS_INDX);

    glVertexAttribPointer(VERTEX_POS_INDX,VERTEX_POS_SIZE,GL_FLOAT,GL_FALSE,vtxStride,vtxBuf);
    vtxBuf += VERTEX_POS_SIZE;

    glVertexAttribPointer(VERTEX_COLOR_INDX,VERTEX_COLOR_SIZE,GL_FLOAT,GL_FALSE,vtxStride,vtxBuf);
    glDrawElements(GL_TRIANGLES,numIndices,GL_UNSIGNED_SHORT,indices);

    glDisableVertexAttribArray(VERTEX_COLOR_INDX);
    glDisableVertexAttribArray(VERTEX_POS_INDX);
}

void drawWithVBOs(GLint numVertices,GLfloat* vtxBuf,GLint vtxStride,GLint numIndices,
                  GLushort* indices){
    GLuint offset = 0;
    if(g_vboIds[0] == 0 && g_vboIds[1] == 0){
        glGenBuffers(2,g_vboIds);

        glBindBuffer(GL_ARRAY_BUFFER,g_vboIds[0]);
        glBufferData(GL_ARRAY_BUFFER,vtxStride* numVertices,vtxBuf,GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,g_vboIds[1]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(GLushort) * numIndices,indices,GL_STATIC_DRAW);
    }

    glBindBuffer(GL_ARRAY_BUFFER,g_vboIds[0]);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,g_vboIds[1]);

    glEnableVertexAttribArray(VERTEX_COLOR_INDX);
    glEnableVertexAttribArray(VERTEX_POS_INDX);

    glVertexAttribPointer(VERTEX_POS_INDX, VERTEX_POS_SIZE, GL_FLOAT, GL_FALSE, vtxStride,
                          (const void *) offset);

    offset += VERTEX_POS_SIZE * sizeof(GL_FLOAT);

    glVertexAttribPointer(VERTEX_COLOR_INDX, VERTEX_COLOR_SIZE, GL_FLOAT, GL_FALSE, vtxStride,
                          (const void *) offset);

    glDrawElements(GL_TRIANGLES,numIndices,GL_UNSIGNED_SHORT,0);
    glDisableVertexAttribArray(VERTEX_COLOR_INDX);
    glDisableVertexAttribArray(VERTEX_POS_INDX);

}


//MapBuffer
JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIMapBufferGlSurfaceView_jniinit(JNIEnv *env, jobject instance) {
    char vShaderStr[] =
            "#version 300 es                          \n"
            "layout(location=0) in vec4 a_position;   \n"
            "layout(location=1) in vec4 a_color;      \n"
            "out vec4 v_color;                        \n"
            "void main()                              \n"
            "{                                        \n"
            "       v_color = a_color;                \n"
            "       gl_Position = a_position;         \n"
            "}";
    char fShaderStr[] =
            "#version 300 es                          \n"
            "precision mediump float;                 \n"
            "in vec4 v_color;                         \n"
            "out vec4 o_fragColor;                    \n"
            "void main()                              \n"
            "{                                        \n"
            "       o_fragColor = v_color;            \n"
            "}";

    g_program = GlCommon::getInstance()->LoadProgram(vShaderStr,fShaderStr);
    g_vboIds[0] = 0;
    g_vboIds[1] = 0;

    glClearColor(1.0f,1.0f,1.0f,0.0f);

}

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIMapBufferGlSurfaceView_onChangeSize(JNIEnv *env, jobject instance,
                                                                     jint width, jint height) {
    g_width = width;
    g_height = height;
}

void drawWithVBOMapBuffer(GLint numVertices,GLfloat* vtxBuf,GLint vtxStride,
                          GLint numIndices,GLushort* indices){
    GLuint offset = 0;
    if(g_vboIds[0] == 0 && g_vboIds[1] == 0){
        GLfloat* vtxMappedBuf;
        GLushort* idxMappedBuf;

        //
        glGenBuffers(2,g_vboIds);

        glBindBuffer(GL_ARRAY_BUFFER,g_vboIds[0]);
        glBufferData(GL_ARRAY_BUFFER,vtxStride * numVertices,NULL,GL_STATIC_DRAW);
        vtxMappedBuf = (GLfloat *) glMapBufferRange(GL_ARRAY_BUFFER, 0, vtxStride * numVertices,
                                GL_MAP_WRITE_BIT|GL_MAP_INVALIDATE_BUFFER_BIT);
        memcpy(vtxMappedBuf,vtxBuf,vtxStride * numVertices);
        glUnmapBuffer(GL_ARRAY_BUFFER);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,g_vboIds[1]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(GLushort) * numIndices,NULL,GL_STATIC_DRAW);
        idxMappedBuf = (GLushort *) glMapBufferRange(GL_ELEMENT_ARRAY_BUFFER, 0,
                                                     sizeof(GLushort) * numIndices,
                                GL_MAP_WRITE_BIT|GL_MAP_INVALIDATE_BUFFER_BIT);
        memcpy(idxMappedBuf,indices,sizeof(GLushort) * numIndices);
        glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
    }

    glBindBuffer(GL_ARRAY_BUFFER,g_vboIds[0]);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,g_vboIds[1]);

    glEnableVertexAttribArray(VERTEX_POS_INDX);
    glEnableVertexAttribArray(VERTEX_COLOR_INDX);

    glVertexAttribPointer(VERTEX_POS_INDX, VERTEX_POS_SIZE, GL_FLOAT, false,
                          vtxStride, (const void *) offset);
    offset += VERTEX_POS_SIZE * sizeof(GLfloat);

    glVertexAttribPointer(VERTEX_COLOR_INDX, VERTEX_COLOR_SIZE, GL_FLOAT, false,
                          vtxStride, (const void *) offset);
    glDrawElements(GL_TRIANGLES,numIndices,GL_UNSIGNED_SHORT,0);

    glDisableVertexAttribArray(VERTEX_POS_INDX);
    glDisableVertexAttribArray(VERTEX_COLOR_INDX);

    glBindBuffer(GL_ARRAY_BUFFER,0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
}

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIMapBufferGlSurfaceView_jniDraw(JNIEnv *env, jobject instance) {

    float vertexData[3 * (VERTEX_COLOR_SIZE + VERTEX_POS_SIZE)] = {
            0.0f,0.5f,0.0f,        //v0
            1.0f,0.0f,0.0f,1.0f,    //color 0
            -0.5f,-0.5f,0.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.5f,-0.5f,0.0f,
            0.0f,0.0f,1.0f,1.0f
    };
    GLushort indices[] = {0,1,2};
    glViewport(0,0,g_width,g_height);
    glClear(GL_COLOR_BUFFER_BIT);
    glUseProgram(g_program);

    drawWithVBOMapBuffer(3,vertexData,
                         sizeof(GL_FLOAT) * (VERTEX_POS_SIZE + VERTEX_COLOR_SIZE),
                        3,indices);
}



///emample6
JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIExample6GlSurfaceView_init(JNIEnv *env, jobject instance) {

    char vShaderStr[] =
            "#version 300 es                          \n"
                    "layout(location=0) in vec4 a_position;   \n"
                    "layout(location=1) in vec4 a_color;      \n"
                    "out vec4 v_color;                        \n"
                    "void main()                              \n"
                    "{                                        \n"
                    "       v_color = a_color;                \n"
                    "       gl_Position = a_position;         \n"
                    "}";
    char fShaderStr[] =
            "#version 300 es                          \n"
                    "precision mediump float;                 \n"
                    "in vec4 v_color;                         \n"
                    "out vec4 o_fragColor;                    \n"
                    "void main()                              \n"
                    "{                                        \n"
                    "       o_fragColor = v_color;            \n"
                    "}";

    g_program = GlCommon::getInstance()->LoadProgram(vShaderStr,fShaderStr);
    g_VBOIds[0] = 0;
    g_VBOIds[1] = 0;
    g_VBOIds[2] = 0;

    glClearColor(1.0f,1.0f,1.0f,0.0f);
}

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIExample6GlSurfaceView_onChangeSize(JNIEnv *env, jobject instance,
                                                                    jint width, jint height) {
    g_width =width;
    g_height = height;

}

void drawWithVBOs222(GLint numVertices,GLfloat** vtxBuf,
                  GLint* vtxStrides, GLint numIndices,GLushort* indecies){
    if(g_VBOIds[0] == 0 && g_VBOIds[1] == 0 && g_VBOIds[2] == 0 ){
        glGenBuffers(3,g_VBOIds);

        glBindBuffer(GL_ARRAY_BUFFER,g_VBOIds[0]);
        glBufferData(GL_ARRAY_BUFFER,vtxStrides[0] * numVertices,vtxBuf[0],GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER,g_VBOIds[1]);
        glBufferData(GL_ARRAY_BUFFER,vtxStrides[1] * numVertices,vtxBuf[1],GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,g_VBOIds[2]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,sizeof(GLshort) * numIndices,indecies,GL_STATIC_DRAW);
    }

    glBindBuffer(GL_ARRAY_BUFFER,g_VBOIds[0]);
    glEnableVertexAttribArray(VERTEX_POS_INDX);
    glVertexAttribPointer(VERTEX_POS_INDX, VERTEX_POS_SIZE,GL_FLOAT,GL_FALSE,vtxStrides[0],0);

    glBindBuffer(GL_ARRAY_BUFFER,g_VBOIds[1]);
    glEnableVertexAttribArray(VERTEX_COLOR_INDX);
    glVertexAttribPointer(VERTEX_COLOR_INDX, VERTEX_COLOR_SIZE,GL_FLOAT,GL_FALSE,vtxStrides[1],0);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,g_VBOIds[2]);
    glDrawElements(GL_TRIANGLES,numIndices,GL_UNSIGNED_SHORT,0);

    glDisableVertexAttribArray(VERTEX_POS_INDX);
    glDisableVertexAttribArray(VERTEX_COLOR_INDX);

    glBindBuffer(GL_ARRAY_BUFFER,0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
}

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles6_JNIExample6GlSurfaceView_jnidraw(JNIEnv *env, jobject instance) {
    glViewport(0,0,g_width,g_height);
    glClear(GL_COLOR_BUFFER_BIT);
    glUseProgram(g_program);

    GLfloat posData[3 * VERTEX_POS_SIZE] = {
            0.0f,0.5f,0.0f,
            -0.5f,-0.5f,0.0f,
            0.5f,-0.5f,0.0f
    };
    GLfloat color[3 * VERTEX_COLOR_SIZE] = {
        1.0f,0.0f,0.0f,1.0f,
        0.0f,1.0f,0.0f,1.0f,
        0.0f,0.0f,1.0f,1.0f
    };
    GLint vtxStrides[2] = {
            VERTEX_POS_SIZE * sizeof(GLfloat),
            VERTEX_COLOR_SIZE * sizeof(GLfloat)
    };
    GLushort indices[3] = {0,1,2};
    GLfloat* vtxBuf[2] = {posData,color};

    drawWithVBOs222(3,vtxBuf,vtxStrides,3,indices);
}

#ifdef __cplusplus
}
#endif