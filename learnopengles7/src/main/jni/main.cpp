//
// Created by maxll on 16-12-16.
//

#include <jni.h>
#include <math.h>
#include <stdlib.h>
#include <GLES3/gl3.h>
#include "../../../../nativeCommon/Include/esUtil.h"
#ifdef __cplusplus
extern "C" {
#endif


#define NUM_INSTANCES 100
#define POSITION_LOC 0
#define COLOR_LOC 1
#define MVP_LOC 2

typedef struct {
    GLuint programObject;

    //VBO
    GLuint positionVBO;
    GLuint colorVBO;
    GLuint mvpVBO;
    GLuint indicesIBO;

    int numIndices;

    GLfloat angle[NUM_INSTANCES];
}UserData;

//GLuint programObject,positionVBO,colorVBO,mvpVBO,indicesIBO;
//int numIndices;
//GLfloat angle[NUM_INSTANCES];
//GLuint g_width,g_height;

GLuint LoadShader(GLenum type, const char *src) {
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


GLuint LoadProgram(const char *vShaderSrc, const char *fShaderSrc) {
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



//JNIEXPORT void JNICALL
//Java_com_maxll_learnopengles7_JNIGlSurfaceView_init(JNIEnv *env, jobject instance) {
int Init(ESContext* esContext){
    GLfloat* positions;
    GLuint* indices;

    UserData* userData = (UserData *) esContext->userData;

    const char vShaderStr[] =
            "#version 300 es                          \n"
            "layout(location=0) in vec4 a_position;   \n"
            "layout(location=1) in vec4 a_color;      \n"
            "layout(location=2) in mat4 a_mvpMatrix;  \n"
            "out vec4 v_color;                        \n"
            "void main()                              \n"
            "{                                        \n"
            "       v_color = a_color;                \n"
            "       gl_Position = a_mvpMatrix * a_position;  \n"
            "}";
    const char fShaderStr[] =
            "#version 300 es                          \n"
            "precision mediump float;                 \n"
            "in vec4 v_color;                         \n"
            "layout(location=0)out vec4 outColor;     \n"
            "void main()                              \n"
            "{                                        \n"
            "       outColor = v_color;               \n"
            "}";
    userData->programObject = LoadProgram(vShaderStr,fShaderStr);

    //generate the vertex data
    userData->numIndices = esGenCube(0.1f,&positions,NULL,NULL,&indices);

    //Index buffer object
    glGenBuffers(1,&userData->indicesIBO);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,userData->indicesIBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(GLuint) * userData->numIndices,indices,GL_STATIC_DRAW);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
    free(positions);

    //Position VBO for cube mode
    glGenBuffers(1,&userData->positionVBO);
    glBindBuffer(GL_ARRAY_BUFFER,userData->positionVBO);
    glBufferData(GL_ARRAY_BUFFER,24 * sizeof(GLfloat) * 3,positions,GL_STATIC_DRAW);
    free(positions);

    //random color foe ech instance
    {
        GLubyte colors[NUM_INSTANCES][4];
        srandom(0);

        for (int i = 0; i < NUM_INSTANCES; ++i) {
            colors[i][0] = random() % 255;
            colors[i][1] = random() % 255;
            colors[i][2] = random() % 255;
            colors[i][3] = 0;
        }

        glGenBuffers(1,&userData->colorVBO);
        glBindBuffer(GL_ARRAY_BUFFER,userData->colorVBO);
        glBufferData(GL_ARRAY_BUFFER,NUM_INSTANCES * 4,colors,GL_STATIC_DRAW);
    }

    //Allocate storage to store MVP per instance
    {
        for (int i = 0;i < NUM_INSTANCES;i++){
            userData->angle[i] = (float)(random() % 32768) / 32767.0f * 360.0f;
        }
        glGenBuffers(1,&userData->mvpVBO);
        glBindBuffer(GL_ARRAY_BUFFER,userData->mvpVBO);
        glBufferData(GL_ARRAY_BUFFER,NUM_INSTANCES * sizeof(ESMatrix),NULL,GL_DYNAMIC_DRAW);
    }
    glBindBuffer(GL_ARRAY_BUFFER,0);
    glClearColor(1.0f,1.0f,1.0f,0.0f);

    return GL_TRUE;
}

//JNIEXPORT void JNICALL
//Java_com_maxll_learnopengles7_JNIGlSurfaceView_changeSize(JNIEnv *env, jobject instance, jint width,
//                                                          jint height) {
//    g_width = width;
//    g_height = height;
//}

//JNIEXPORT void JNICALL
//Java_com_maxll_learnopengles7_JNIGlSurfaceView_jniDraw(JNIEnv *env, jobject instance) {
void Draw(ESContext* esContext){

    UserData* userData = (UserData *) esContext->userData;

    glViewport(0,0,esContext->width,esContext->height);
    glClear(GL_COLOR_BUFFER_BIT);
    glUseProgram(userData->programObject);

    //load the vertex position
    glBindBuffer(GL_ARRAY_BUFFER,userData->positionVBO);
    glVertexAttribPointer(POSITION_LOC,3,GL_FLOAT,GL_FALSE,3 * sizeof(GLfloat),NULL);
    glEnableVertexAttribArray(POSITION_LOC);

    //Load the instance color buffer
    glBindBuffer(GL_ARRAY_BUFFER,userData->colorVBO);
    glVertexAttribPointer(COLOR_LOC,4,GL_UNSIGNED_BYTE,GL_TRUE,4 * sizeof(GLubyte),NULL);
    glEnableVertexAttribArray(COLOR_LOC);

    //one color per instance
    glVertexAttribDivisor(COLOR_LOC,1);

    glBindBuffer(GL_ARRAY_BUFFER,userData->mvpVBO);

    //Load each matrix row of the MVP
    glVertexAttribPointer(MVP_LOC + 0,4,GL_FLOAT,GL_FALSE,sizeof(ESMatrix),NULL);
    glVertexAttribPointer(MVP_LOC + 1, 4, GL_FLOAT, GL_FALSE, sizeof(ESMatrix),
                          (const void *) (sizeof(GLfloat) * 4));
    glVertexAttribPointer(MVP_LOC + 2,4,GL_FLOAT,GL_FALSE, sizeof(ESMatrix),
                          (const void *) (sizeof(GLfloat) * 8));
    glVertexAttribPointer(MVP_LOC + 3,4,GL_FLOAT,GL_FALSE,sizeof(ESMatrix),
                          (const void *) (sizeof(GLfloat) * 12));
    glEnableVertexAttribArray(MVP_LOC + 0);
    glEnableVertexAttribArray(MVP_LOC + 1);
    glEnableVertexAttribArray(MVP_LOC + 2);
    glEnableVertexAttribArray(MVP_LOC + 3);

    //One MVp per instance
    glVertexAttribDivisor(MVP_LOC + 0,1);
    glVertexAttribDivisor(MVP_LOC + 1,1);
    glVertexAttribDivisor(MVP_LOC + 2,1);
    glVertexAttribDivisor(MVP_LOC + 3,1);

    //Bind the index Buffer
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,userData->indicesIBO);

    //Draw the cubes
    glDrawElementsInstanced(GL_TRIANGLES,userData->numIndices,GL_UNSIGNED_INT,NULL,NUM_INSTANCES);
}

//JNIEXPORT void JNICALL
//Java_com_maxll_learnopengles7_JNIGlSurfaceView_jniUpdate(JNIEnv *env, jobject instance,
//                                                         jfloat deltaTime) {
//
//    // TODO
//
//}

void Shutdown(ESContext* esContext){
    UserData* userData = (UserData *) esContext->userData;

    glDeleteBuffers(1,&userData->positionVBO);
    glDeleteBuffers(1,&userData->colorVBO);
    glDeleteBuffers(1,&userData->mvpVBO);
    glDeleteBuffers(1,&userData->indicesIBO);

    glDeleteProgram(userData->programObject);
}

void Update(ESContext* esContext, float delatTime){
    UserData* userData = (UserData *) esContext->userData;
    ESMatrix* matrixBuf;
    ESMatrix perspective;
    float aspect;
    int i = 0;
    int numRows;
    int numColumns;

    aspect = (GLfloat)esContext->width / (GLfloat)esContext->height;

    esMatrixLoadIdentity(&perspective);
    esPerspective(&perspective,60.0f,aspect,1.0f,20.0f);

    glBindBuffer(GL_ARRAY_BUFFER,userData->mvpVBO);
    matrixBuf = (ESMatrix *) glMapBufferRange(GL_ARRAY_BUFFER, 0,
           sizeof(ESMatrix) * NUM_INSTANCES, GL_MAP_WRITE_BIT);

    numRows = (int)sqrtf(NUM_INSTANCES);
    numColumns = numRows;

    for (i = 0; i < NUM_INSTANCES; i++){
        ESMatrix modelview;
        float translateY = ((float)(i % numRows) / (float)numRows) * 2.0f -1.0f;
        float translateX = ((float)(i / numRows) / (float)numColumns) * 2.0f -1.0f;

        esMatrixLoadIdentity(&modelview);

        esTranslate(&modelview,translateX,translateY,-2.0f);

        userData->angle[i] += (delatTime * 40.0f);
        if(userData->angle[i] >= 360.0f)
            userData->angle[i] -= 360.0f;
        esRotate(&modelview,userData->angle[i],1.0,0.0,1.0);
        esMatrixMultiply(&matrixBuf[i],&modelview,&perspective);
    }

    glUnmapBuffer(GL_ARRAY_BUFFER);
}

int esMain(ESContext* esContext){
    esContext->userData = malloc(sizeof(UserData));

    esCreateWindow(esContext,"Instancing",640,480,ES_WINDOW_RGB|ES_WINDOW_DEPTH);

    if(!Init(esContext))
        return GL_FALSE;

    esRegisterShutdownFunc(esContext,Shutdown);
    esRegisterUpdateFunc(esContext,Update);
    esRegisterDrawFunc(esContext,Draw);

    return GL_TRUE;
}

#ifdef __cplusplus
}
#endif