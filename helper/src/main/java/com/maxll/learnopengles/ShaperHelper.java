package com.maxll.learnopengles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by maxll on 16-12-17.
 */

public class ShaperHelper {
    private FloatBuffer vertices;
    private FloatBuffer normals;
    private FloatBuffer texCoords;
    private ShortBuffer indices;
    private int numIndices;

    public FloatBuffer getNormals() {
        return normals;
    }

    public FloatBuffer getTexCoords() {
        return texCoords;
    }

    public FloatBuffer getVertices() {
        return vertices;
    }

    public int getNumIndices() {
        return numIndices;
    }

    public ShortBuffer getIndices() {
        return indices;
    }

    public int genShaper(int numSlices, float radius){
        int i,j;
        int numParallels = numSlices;
        int numVerticels = (numParallels + 1) * (numSlices + 1);
        int numIndices = numParallels * numSlices * 6;
        float angleStep = ((2.0f * (float)Math.PI) / numSlices);

        //Allocate memory for buffers
        vertices = ByteBuffer.allocateDirect(numVerticels * 3 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normals = ByteBuffer.allocateDirect(numVerticels * 3 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoords = ByteBuffer.allocateDirect(numVerticels * 2 *4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        indices = ByteBuffer.allocateDirect(numIndices * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        for ( i = 0; i < numParallels + 1; i++ )
        {
            for ( j = 0; j < numSlices + 1; j++ )
            {
                int vertex = ( i * ( numSlices + 1 ) + j ) * 3;

                vertices
                        .put ( vertex + 0,
                                ( float ) ( radius
                                        * Math.sin ( angleStep * ( float ) i ) * Math
                                        .sin ( angleStep * ( float ) j ) ) );

                vertices.put ( vertex + 1,
                        ( float ) ( radius * Math.cos ( angleStep * ( float ) i ) ) );
                vertices
                        .put ( vertex + 2,
                                ( float ) ( radius
                                        * Math.sin ( angleStep * ( float ) i ) * Math
                                        .cos ( angleStep * ( float ) j ) ) );

                normals.put ( vertex + 0, vertices.get ( vertex + 0 ) / radius );
                normals.put ( vertex + 1, vertices.get ( vertex + 1 ) / radius );
                normals.put ( vertex + 2, vertices.get ( vertex + 2 ) / radius );

                int texIndex = ( i * ( numSlices + 1 ) + j ) * 2;
                texCoords.put ( texIndex + 0, ( float ) j / ( float ) numSlices );
                texCoords.put ( texIndex + 1, ( 1.0f - ( float ) i )
                        / ( float ) ( numParallels - 1 ) );
            }
        }

        int index = 0;

        for ( i = 0; i < numParallels; i++ )
        {
            for ( j = 0; j < numSlices; j++ )
            {
                indices.put ( index++, ( short ) ( i * ( numSlices + 1 ) + j ) );
                indices.put ( index++, ( short ) ( ( i + 1 ) * ( numSlices + 1 ) + j ) );
                indices.put ( index++,
                        ( short ) ( ( i + 1 ) * ( numSlices + 1 ) + ( j + 1 ) ) );

                indices.put ( index++, ( short ) ( i * ( numSlices + 1 ) + j ) );
                indices.put ( index++,
                        ( short ) ( ( i + 1 ) * ( numSlices + 1 ) + ( j + 1 ) ) );
                indices.put ( index++, ( short ) ( i * ( numSlices + 1 ) + ( j + 1 ) ) );

            }
        }

        this.numIndices = numIndices;

        return numIndices;
    }

    public int genCube(float scale){
        int i;
        int numVertices = 24;
        int numIndices = 36;

        float[] cubeVerts = { -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f,
        };

        float[] cubeNormals = { 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        };

        float[] cubeTex = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
        };

        // Allocate memory for buffers
        vertices = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        normals = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        texCoords = ByteBuffer.allocateDirect ( numVertices * 2 * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        indices = ByteBuffer.allocateDirect ( numIndices * 2 )
                .order ( ByteOrder.nativeOrder() ).asShortBuffer();

        vertices.put ( cubeVerts ).position ( 0 );

        for ( i = 0; i < numVertices * 3; i++ )
        {
            vertices.put ( i, vertices.get ( i ) * scale );
        }

        normals.put ( cubeNormals ).position ( 0 );
        texCoords.put ( cubeTex ).position ( 0 );

        short[] cubeIndices = { 0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10,
                8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19, 20,
                23, 22, 20, 22, 21
        };

        indices.put ( cubeIndices ).position ( 0 );
        this.numIndices = numIndices;
        return numIndices;
    }
}

























