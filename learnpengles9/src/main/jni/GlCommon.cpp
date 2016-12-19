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



GLuint GlCommon::createSimpleTexture2D(){
    //Texture handler
    GLuint textureId;

    GLubyte pixels[4 * 3] = {
            255,0,0,
            0,255,0,
            0,0,255,
            255,255,0
    };

    //use tightly packed data
    glPixelStorei(GL_UNPACK_ALIGNMENT,1);

    //Generate
    glGenTextures(1,&textureId);

    //Bind the texture object
    glBindTexture(GL_TEXTURE_2D,textureId);

    //load the texture
    glTexImage2D(GL_TEXTURE_2D,0,GL_RGB,2,2,0,GL_RGB,GL_UNSIGNED_BYTE,pixels);

    //set the filtering mode
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

    return textureId;
}

GLboolean GlCommon::GenMipMap2D ( GLubyte *src, GLubyte **dst, int srcWidth,
                                  int srcHeight, int *dstWidth, int *dstHeight )
{
    int x,
            y;
    int texelSize = 3;

    *dstWidth = srcWidth / 2;

    if ( *dstWidth <= 0 )
    {
        *dstWidth = 1;
    }

    *dstHeight = srcHeight / 2;

    if ( *dstHeight <= 0 )
    {
        *dstHeight = 1;
    }

    *dst = (GLubyte *) malloc (sizeof ( GLubyte ) * texelSize * ( *dstWidth ) * ( *dstHeight ) );

    if ( *dst == NULL )
    {
        return GL_FALSE;
    }

    for ( y = 0; y < *dstHeight; y++ )
    {
        for ( x = 0; x < *dstWidth; x++ )
        {
            int srcIndex[4];
            float r = 0.0f,
                    g = 0.0f,
                    b = 0.0f;
            int sample;

            // Compute the offsets for 2x2 grid of pixels in previous
            // image to perform box filter
            srcIndex[0] =
                    ( ( ( y * 2 ) * srcWidth ) + ( x * 2 ) ) * texelSize;
            srcIndex[1] =
                    ( ( ( y * 2 ) * srcWidth ) + ( x * 2 + 1 ) ) * texelSize;
            srcIndex[2] =
                    ( ( ( ( y * 2 ) + 1 ) * srcWidth ) + ( x * 2 ) ) * texelSize;
            srcIndex[3] =
                    ( ( ( ( y * 2 ) + 1 ) * srcWidth ) + ( x * 2 + 1 ) ) * texelSize;

            // Sum all pixels
            for ( sample = 0; sample < 4; sample++ )
            {
                r += src[srcIndex[sample]];
                g += src[srcIndex[sample] + 1];
                b += src[srcIndex[sample] + 2];
            }

            // Average results
            r /= 4.0;
            g /= 4.0;
            b /= 4.0;

            // Store resulting pixels
            ( *dst ) [ ( y * ( *dstWidth ) + x ) * texelSize ] = ( GLubyte ) ( r );
            ( *dst ) [ ( y * ( *dstWidth ) + x ) * texelSize + 1] = ( GLubyte ) ( g );
            ( *dst ) [ ( y * ( *dstWidth ) + x ) * texelSize + 2] = ( GLubyte ) ( b );
        }
    }

    return GL_TRUE;
}

///
//  Generate an RGB8 checkerboard image
//
GLubyte* GlCommon::GenCheckImage ( int width, int height, int checkSize )
{
    int x,
            y;
    GLubyte *pixels = (GLubyte *) malloc (width * height * 3 );

    if ( pixels == NULL )
    {
        return NULL;
    }

    for ( y = 0; y < height; y++ )
        for ( x = 0; x < width; x++ )
        {
            GLubyte rColor = 0;
            GLubyte bColor = 0;

            if ( ( x / checkSize ) % 2 == 0 )
            {
                rColor = 255 * ( ( y / checkSize ) % 2 );
                bColor = 255 * ( 1 - ( ( y / checkSize ) % 2 ) );
            }
            else
            {
                bColor = 255 * ( ( y / checkSize ) % 2 );
                rColor = 255 * ( 1 - ( ( y / checkSize ) % 2 ) );
            }

            pixels[ ( y * width + x ) * 3] = rColor;
            pixels[ ( y * width + x ) * 3 + 1] = 0;
            pixels[ ( y * width + x ) * 3 + 2] = bColor;
        }

    return pixels;
}
