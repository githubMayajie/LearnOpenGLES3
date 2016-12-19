package com.maxll.learnopengles301;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by maxll on 16-12-15.
 */

public class GLImplConfigure {

    public static class EVENT{
        public static final int TYPE_JAVA = 1;
        public static final int  TYPE_JNI= 1;
    }

    public static final int choose = EVENT.TYPE_JAVA;

    public static final int GLES_CLIENT_VERSION = 3;


    public static boolean isSupportGLES3(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        /**
         * The GLES version used by an application. The upper order 16 bits represent the
         * major version and the lower order 16 bits the minor version.
         */
        return am.getDeviceConfigurationInfo().reqGlEsVersion >= 0x00030000;
    }
}
