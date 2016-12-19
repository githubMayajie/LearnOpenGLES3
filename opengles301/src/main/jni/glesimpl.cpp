//
// Created by maxll on 16-12-15.
//

#include <jni.h>
#include <stdlib.h>
#include <GLES3/gl3.h>
#include <android/log.h>
#include <android/asset_manager_jni.h>


#define TAG = "glesimpl"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)




#ifdef __cplusplus
extern "C" {
#endif

void init(JNIEnv* env,jobject obj);
char* readAssetsFile(JNIEnv* env,jobject obj,const char* fileName);
GLuint LoadShader(GLenum type,const char* shaderSrc);

GLint g_program = 0;
GLint g_width = 0;
GLint g_height = 0;
GLfloat g_vertexData[] = {
        0.0f,0.5f,0.0f,
        -0.5f,-0.5f,0.0f,
        0.5f,-0.5f,0.0f
};

AAssetManager* g_aassetManager = NULL;


JNIEXPORT void JNICALL
Java_com_maxll_learnopengles301_JNIImpl_JNIGlSurfaceView_init(JNIEnv* env,jobject obj){
    init(env,obj);
}

JNIEXPORT void JNICALL
Java_com_maxll_learnopengles301_JNIImpl_JNIGlSurfaceView_sizeChange(JNIEnv *env, jobject instance,
                                                                    jint width, jint height) {
    g_width = width;
    g_height = height;
}


JNIEXPORT void JNICALL
Java_com_maxll_learnopengles301_JNIImpl_JNIGlSurfaceView_draw(JNIEnv *env, jobject instance) {
    glViewport(0,0,g_width,g_height);

    //clear color
    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(g_program);

    //3条线段 index size
    glVertexAttribPointer(0,3,GL_FLOAT,GL_FALSE,0,g_vertexData);
    glEnableVertexAttribArray(0);

    glDrawArrays(GL_TRIANGLES,0,3);
}

void init(JNIEnv* env,jobject obj){
    char* vertexShaderSrc = readAssetsFile(env,obj,"vshader.glsl");
    char* fragmentShaderSrc = readAssetsFile(env,obj,"fshader.glsl");

    GLuint vertexShader,fragmentShader,program;
    GLint linked;

    vertexShader = LoadShader(GL_VERTEX_SHADER,vertexShaderSrc);
    fragmentShader = LoadShader(GL_FRAGMENT_SHADER,fragmentShaderSrc);

    program = glCreateProgram();

    glAttachShader(program,vertexShader);
    glAttachShader(program,fragmentShader);

    glLinkProgram(program);

    glGetProgramiv(program,GL_LINK_STATUS,&linked);
    if(!linked){
        glDeleteProgram(program);
        return ;
    }

    g_program = program;
    glClearColor(1.0f,1.0f,1.0f,0.0f);
}

GLuint LoadShader(GLenum type,const char* shaderSrc){
    GLuint shader;
    GLint complied;

    shader = glCreateProgram();
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

char* readAssetsFile(JNIEnv* env,jobject obj,const char* fileName){
    if(!g_aassetManager){
        //getAssestObj
        //context->getAssets();
        jclass thiz = env->GetObjectClass(obj);
        jfieldID contextField = env->GetFieldID(thiz,"context","Landroid/content/Context;");
        jobject contextObj = env->GetObjectField(obj,contextField);

        jclass ctxClass = env->GetObjectClass(contextObj);
        jmethodID assetMID = env->GetMethodID(ctxClass,"getAssets","()Landroid/content/res/AssetManager;");
        jobject javaAssetManager = env->CallObjectMethod(contextObj, assetMID);

        g_aassetManager = AAssetManager_fromJava(env,javaAssetManager);

        env->DeleteLocalRef(thiz);
        env->DeleteLocalRef(contextObj);
        env->DeleteLocalRef(ctxClass);
        env->DeleteLocalRef(javaAssetManager);
    }

    AAsset* asset = NULL;
    char* buffer = NULL;
    off_t size = -1;
    int numByte = -1;

    asset = AAssetManager_open(g_aassetManager,fileName,AASSET_MODE_UNKNOWN);
    size = AAsset_getLength(asset);
    buffer = (char *) malloc(size + 1);
    buffer[size] = 0;

    numByte = AAsset_read(asset,buffer,size);
    AAsset_close(asset);

    return buffer;
}

#ifdef __cplusplus
}
#endif