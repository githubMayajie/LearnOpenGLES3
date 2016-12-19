package com.maxll.learnopengles6;

/**
 * Created by maxll on 16-12-16.
 */

public class GLSurfaceViewConfig {

    public static final int OPENGLES_CLIENT_VERSION = 3;

    public static class ProjectConfig{
        public boolean isJNI = false;
        public int PROJECTID = 0;
    }

    public final static ProjectConfig projectConfig = new ProjectConfig();

    public static class EVENT{
        public static final int VAO = 0;
        public static final int VBO = 1;
        public static final int MAPBUFFER = 2;
        public static final int EXAMPLE6 = 3;
    }

    static {
        projectConfig.isJNI = true;
        projectConfig.PROJECTID = EVENT.EXAMPLE6;
    }
}
