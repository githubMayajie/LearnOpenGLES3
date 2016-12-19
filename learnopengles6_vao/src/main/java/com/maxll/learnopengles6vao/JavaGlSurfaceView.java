package com.maxll.learnopengles6vao;

import android.content.Context;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by maxll on 16-12-15.
 */

public class JavaGlSurfaceView extends MyGlSurfaceView{

    public JavaGlSurfaceView(Context context) {
        super(context);
    }

    private int width,height,program;
    private FloatBuffer floatBuffer;
    private final float[] vertexData = {
            0.0f,0.5f,0.0f,
            -0.5f,-0.5f,0.0f,
            0.5f,-0.5f,0.0f
    };

    @Override
    void initSurface(GL10 gl, EGLConfig config) {
        //init data
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(vertexData).position(0);

        //init gles
        String vertexShaderSrc =
                "#version 300 es                        \n" +
                "layout(location=0) in vec4 a_color;    \n" +
                "layout(location=1) in vec4 a_position; \n" +
                "out vec4 v_color;                      \n" +
                "void main()                            \n" +
                "{                                      \n" +
                "      v_color = a_color;               \n" +
                "       gl_Position = a_position;       \n" +
                "}";
        String fragmentShaderSrc =
                "#version 300 es                        \n" +
                "precision mediump float;               \n" +
                "in vec4 v_color;                       \n" +
                "out vec4 o_fragColor;                  \n" +
                "void main()                            \n" +
                "{                                      \n" +
                "      o_fragColor = v_color;           \n" +
                "}";


        int vShader,fShader;
        int[] linked = new int[1];

        //load the v and f shader
        vShader = LoadShader(GLES30.GL_VERTEX_SHADER,vertexShaderSrc);
        fShader = LoadShader(GLES30.GL_FRAGMENT_SHADER,fragmentShaderSrc);

        int program = GLES30.glCreateProgram();

        //attach
        GLES30.glAttachShader(program,vShader);
        GLES30.glAttachShader(program,fShader);

        //link
        GLES30.glLinkProgram(program);

        //check
        GLES30.glGetProgramiv(program,GLES30.GL_LINK_STATUS,linked,0);
        if(linked[0] == 0){
            GLES30.glDeleteProgram(program);
            program = 0;
            return;
        }
        this.program = program;

        GLES30.glClearColor(1.0f,1.0f,1.0f,0.0f);
    }

    @Override
    void onSizeChange(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    void draw(GL10 gl) {
        GLES30.glViewport(0,0,width,height);

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUseProgram(program);

        //用于加载通用属性
        //set vertex color to read
        GLES30.glVertexAttrib4f(0,1.0f,0.0f,0.0f,1.0f);

        floatBuffer.position(0);

        //guide : http://blog.csdn.net/ldpxxx/article/details/9253629
        //指定顶点数组
        //1 表示索引，2 顶点属性所指定的分量数量，3,类型，4 是否应该被规范化，5，顶点制定索引，6 顶点数组
        //1 location = 1
        //2 eg position(x,y,z) =3 color(r,g,b,a) = 4
        //3 type
        //4 gui yi hua(true) huo zhe gu ding zhi(false)
        // 5 zhi ding shu xing zhi jian de pian yi liang
        GLES30.glVertexAttribPointer(1,3,GLES30.GL_FLOAT,false,0,floatBuffer);

        //jiang huan chong qu de shuju fuzhi gei yi ge bian liang
        GLES30.glEnableVertexAttribArray(1);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,3);

        //禁用通用顶点属性数组
        //1 对于vertex shader 的location 1
        GLES30.glDisableVertexAttribArray(1);
    }

    private int LoadShader(int type,String shaderSrc){
        int shader;
        int[] compiled = new int[1];

        shader = GLES30.glCreateShader(type);

        //load
        GLES30.glShaderSource(shader,shaderSrc);

        //compile
        GLES30.glCompileShader(shader);

        //check
        GLES30.glGetShaderiv(shader,GLES30.GL_COMPILE_STATUS,compiled,0);
        if(compiled[0] == 0){
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }
}

/*

名词解释：

 顶点属性就是顶点shader里面声明的一个attribute变量，有两种类型的attribute变量，一种是const属性，一种是顶点属性数组

 在顶点shader中如何声明一个顶点属性：

 attribute vec4 a_position;
 attribute vec4 a_color;

 顶点属性的类型只能为：float vec2 vec3 vec4 mat2 mat3 mat4
 不可以为结构体，或者是这些类型的数组都不可以。
 opengl es 2.0支持GL_MAX_VERTEX_ATTRIBS个vec4类型的顶点属性，
 不够vec4的也会被看做vec4
 内部以32为单精度浮点数存储。属性不会被get packed

 顶点属性在顶点shader里面是只读的，不能写入。

 将顶点shader，片段shader都编译好，并且添加到一个program

      里面之后，就可以将顶点属性 绑定到属性位置0,1,2,3,4,5,6,7中的一个上，

 之后这个属性就有地址了，给每个program里面的

      每个属性都绑定一个位置，然后就可以连接program了，

 也可以在linkprogram之后再获取顶点属性的location。

     怎么为顶点属性上传数据呢，要分情况，如果是顶点属性数组：
      调用函数glVertexAttribPointer ();这个函数里面指定顶点属性的位置，和数据指针。然后
 glEnableVertexAttribArray ( 顶点属性的location );

 各种顶点属性数组都上传并且enable之后，就可以调用glDrawElements函数进行绘制了。

 如果是const 顶点属性，则使用函数：
  glVertexAttribnf(GLuint index,GLfloat x,GLfloat y,...);
  glVertexAttribnfv(GLuint index,const GLfloat* data);



const顶点属性：

 这个样的属性，表示所有的顶点的这个属性都是一样的

 使用下面这些函数来给const顶点属性赋值：
 glVertexAttribnf(GLuint index,GLfloat x,GLfloat y,...);
 glVertexAttribnfv(GLuint index,const GLfloat* data);

 只支持float类型vec4类型。



顶点数组：

 这个顶点数据在主内存中，每一帧渲染都要上传到显卡上去，比较慢，要结合vbo使用。

 使用下面这个函数来给顶点数组赋值：
  glVertexAttribPointer(index,属性在shader中的位置索引（最少0-7），
          这个属性的大小，例如position为3,因为有x，y，z，
     属性中单个成分的数据类型，比如float x，GL_FLOAT。
     bool，表示非浮点数转换为浮点数的时候是否normalized。
     stride，在主内存中前后顶点属性之间间隔多少个字节。

     数据指针）；

  这里面的normalized是什么意思呢？是用来控制数据类型转换的，为false的时候，
  把非浮点数直接转换为浮点数，当他为true的时候，有符号的要映射到-1.0--1.0
  无符号数映射到0.0--1.0
  当为true的时候，关于数据类型如何转换的，有下面这个表：

  GL_BYTE   (2c+1)/(2^8-1)
  GL_UNSIGNED_BYTE c/(2^8-1)
  GL_SHORT  (2c+1)/(2^16-1)
  GL_UNSIGNED_SHORT c/(2^16-1)
  GL_FIXED  c/2^16
  GL_FLOAT  c
  GL_HALF_FLOAT_OES c

  无论是true和false都要做转换，所以顶点属性为最下面两种比较好类型

 顶点属性在主内存中的存储方式有两种：

  1 所有顶点属性存在一起，Pos，normal，texcoord等。


  2 分开存储pos，normal，texcoord各自存着。

   注意这种方式的stride可不是0啊。

  两种方式数据的指定，区别不大，主要是stride和数据指针不同。
  使用第二种方式存储比较好一点。

 允许和禁止顶点属性数组：

  void glEnableVertexAttribArray(GLuint index);
  void glDisableVertexAttribArray(GLuint index);

 指定属性的index：

  glBindAttribLocation(programObj,0,"a_color");
  glBindAttribLocation(programObj,1,"a_position");

  这个函数只能在link program程序之前调用。

怎么区分const顶点属性和顶点数组呢？

 假设在顶点shader中定义了一下如下的顶点属性：

  attribute vec4 a_position;

  既可以把这个a_position当做const顶点属性，也可以当做顶点数组。

  关键是看你调用什么函数来设置他的值，如果你调用了glDisableVertexAttribArray(a_position的location)；

  那么这个a_position就是const顶点属性，需要使用函数
   glVertexAttribnf(GLuint index,GLfloat x,GLfloat y,...);
   或glVertexAttribnfv(GLuint index,const GLfloat* data);来设置他的值。

  否则就当做顶点数组来设置他的值。





获得顶点shader最多支持多少个顶点属性：

 GLint maxVertexAttribs; //大于等于8
 glGetIntgerv(GL_MAX_VERTEX_ATTRIBS,&maxVertexAttribs);

   1 获取program中有几个活动的顶点属性：
 glGetProgramiv(program,GL_ACTIVE_ATTRUBUTES,&numActiveAttribs);

   2 获取program中顶点shader里的某个顶点属性的详细信息：
 void glGetActiveAttrib(GLuint program,
   GLuint index,  //属性的index
   GLsizei bufsize, //后面那个name数组的大小
   GLsizei* length, //写入name数组多少个字符，不包括NULL字符
   GLint* size,  //如果属性不是数组返回1，如果是数组
   GLenum* type,
   GLchar* name);

 上面的两个函数是在link program成功之后调用
 通过上面的两个函数就可以知道顶点shader中有多少个顶点属性，以及他们的名字和类型等详细信息。

   3 绑定和获取一个顶点属性的location（也就是那个index）：
 void glBindAttribLocation(GLuint program,GLuint index,const GLchar* name);（这个函数在link program之前调用）
 GLint glGetAttribLocation(GLuint program,const GLchar* char);


使用顶点数组的物体的渲染流程：

 1 写好shader，

 2 编译shader，

 3 创建program

 4 给program添加shader

 5 绑定好program里面各个顶点属性的location，这一步可选，并不是必须的。

 6 如果是顶点数组的话enable它。
   给顶点属性好赋值，


 7 链接program

  链接之后，可以调用函数获得各个顶点属性的location以及uniform属性的location

 8 指定使用program。

 9 调用函数渲染：
  glVertexAttribPointer（...）；
  glEnableVertexAttribArray（）；

  glDrawElements(GL_TRIANGLES,numIndices,GL_UNSIGNED_SHORT,indices);

使用缓存对象结合顶点数组渲染物体流程：

 1 创建vbo，两个vbo，一个保存各种顶点属性，一个保存索引。
 2 给vbo上传好数据，数据分为顶点数据和索引数据

 3 enable各个顶点属性数组，
  绑定vbo，
  调用glVertexAttribPointer 函数给顶点属性上传数据：
  上传数据的时候，数据指针pos为0



 4 绑定顶点属性

 5 渲染：
  glDrawElements(GL_TRIANGLES,numIndices,GL_UNSIGNED_SHORT,0);

 这个是将各个顶点属性存储在一起的方式渲染的，还有分开存储的渲染方式。
 分开存储的渲染方式比较好。

 1 ，创建vbo，给各个vbo上传好数据，三个GL_ARRAY_BUFFER，一个GL_ELEMENT_ARRAY_BUFFER

 2 关键的一点，因为这里有三个GL_ARRAY_BUFFER：
  glBindBuffer(GL_ARRAY_BUFFER,posvbo);
  glEnableVertexAttribArray(POS_INDEX);

  glBindBuffer(GL_ARRAY_BUFFER,normalvbo);
  glEnableVertexAttribArray(NORMAL_INDEX);

  glBindBuffer(GL_ARRAY_BUFFER,texcoordvbo);
  glEnableVertexAttribArray(TEXCOORD_INDEX);

 3 glVertexAttribPointer(POS_INDEX,possize,GL_FLOAT,GL_FALSE,posStride,0);
   glVertexAttribPointer(NORMAL_INDEX,normalsize,GL_FLOAT,GL_FALSE,normalStride,0);
   glVertexAttribPointer(TEXCOORD_INDEX,texcoordsize,GL_FLOAT,GL_FALSE,texcoordStride,0);

 4 绑定好顶点属性location：

 5 glDrawElements(GL_TRIANGLES,numIndices,GL_UNSIGNED_SHORT,0);



顶点缓存对象（VB0）；

   opengles2.0支持两种类型的缓存对象：

 一种是array buffer object，就是GL_ARRAY_BUFFER, 用来存储顶点属性数据。
 另一种：element array buffer object：就是GL_ELEMENT_ARRAY_BUFFER,用来存储索引数据。

 在绑定了GL_ARRAY_BUFFER类型的缓存对象之后，如果后面调用glVertexAttribPointer函数指定

 顶点数据的话，这个函数最后面的那个参数是当前绑定的GL_ARRAY_BUFFER缓存对象显存地址从
 开始处的偏移值。

 同理，在绑定了GL_ELEMENT_ARRAY_BUFFER类型的vbo后，后面调用glDrawElement函数绘制物体的时候

 这个函数最后面的那个参数也是偏移值。

 void glGenBuffers(GLsizei n,GLuint* buffers);

 void glBindBuffer(GLenum target, GLuint buffer);//buffer为0的话表示不绑定

  opengl中的那些glBindxxx函数如果后面的那个参数是0的话，就表示解除绑定。

 void glBufferData(GLenum target, GLsizeiptr size, const void* data, GLenum usage);
  usage有三种：
  GL_STATIC_DRAW: 一次指定多次使用
  GL_DYNAMIC_DARW,多次指定，多次使用
  GL_STREAM_DRAW, 一次指定，比较少使用。

  如果知道了GL_STATIC_DRAW,那么顶点数据上传到显卡之后，就可以释放主内存

  中的顶点数据了，如果是GL_DYNAMIC_DARW的话，就还不能释放主内存中的数据。



更新缓存对象中的数据：

 一种方法是使用：

  void glBufferSubData(GLenum target, GLintptr offset, GLsizeiptr size,
   const void* data);

 第二种方法：
  如果支持OES_map_buffer扩展，可以map和unmap vbo的存储空间到内存。


  void*　glMapBufferOES(GLenum target, GLenum access);

   target必须为GL_ARRAY_BUFFER

   access必须为GL_WRITE_ONLY_OES

  返回一个指针，不能读取这个指针指向的内容，读取是undefined的。

  更新完了之后调用：

  GLboolean glUnmapBufferOES(GLenum target);

   target必须为GL_ARRAY_BUFFER。

   如果屏幕分辨率改变了，context使用了多个屏幕，或者内存访问
   越界了，就可能会导致mapped出来的内存被丢弃。返回false

  注意这个map的方法应该只用来更新vbo的全部内容，不推荐更新部分内容。
  即使更新全部内容，性能也比glBufferData差。

  调用这个map函数钱最好先调用一下glBufferData()指定数据指针为0，表示废弃
  整个内容，因为，显卡当前可能真正使用这个vbo。要等他使用完，才能map，
  既然要整个内容更新，就没有必要等他使用完。


 */
