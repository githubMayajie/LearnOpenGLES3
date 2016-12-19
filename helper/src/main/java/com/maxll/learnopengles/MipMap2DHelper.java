package com.maxll.learnopengles;

/**
 * Created by maxll on 16-12-19.
 */

public class MipMap2DHelper {

    public static byte[] genMipMap2D(byte[] src,int srcWidth,
                                     int srcHeight,int dstWidth,int dstHeight){
        int x, y;
        int texelSize = 3;

        byte[] dst = new byte[texelSize * ( dstWidth ) * ( dstHeight )];

        for ( y = 0; y < dstHeight; y++ )
        {
            for ( x = 0; x < dstWidth; x++ )
            {
                int[] srcIndex = new int[4];
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
                dst[ ( y * ( dstWidth ) + x ) * texelSize ] = ( byte ) ( r );
                dst[ ( y * ( dstWidth ) + x ) * texelSize + 1] = ( byte ) ( g );
                dst[ ( y * ( dstWidth ) + x ) * texelSize + 2] = ( byte ) ( b );
            }
        }

        return dst;
    }

    ///
    //  Generate an RGB8 checkerboard image
    //
    public static byte[] genCheckImage ( int width, int height, int checkSize )
    {
        int x, y;
        byte[] pixels = new byte[width * height * 3];


        for ( y = 0; y < height; y++ )
            for ( x = 0; x < width; x++ )
            {
                byte rColor = 0;
                byte bColor = 0;

                if ( ( x / checkSize ) % 2 == 0 )
                {
                    rColor = ( byte ) ( 127 * ( ( y / checkSize ) % 2 ) );
                    bColor = ( byte ) ( 127 * ( 1 - ( ( y / checkSize ) % 2 ) ) );
                }
                else
                {
                    bColor = ( byte ) ( 127 * ( ( y / checkSize ) % 2 ) );
                    rColor = ( byte ) ( 127 * ( 1 - ( ( y / checkSize ) % 2 ) ) );
                }

                pixels[ ( y * width + x ) * 3] = rColor;
                pixels[ ( y * width + x ) * 3 + 1] = 0;
                pixels[ ( y * width + x ) * 3 + 2] = bColor;
            }

        return pixels;
    }

}
