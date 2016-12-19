LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := implgles
LOCAL_SRC_FILES := main.cpp
LOCAL_SRC_FILES += GlCommon.cpp


LOCAL_LDLIBS := -llog
LOCAL_LDLIBS += -landroid
LOCAL_LDLIBS += -lEGL
LOCAL_LDLIBS += -lGLESv3
include $(BUILD_SHARED_LIBRARY)