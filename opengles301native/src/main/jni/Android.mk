LOCAL_PATH := $(call my-dir)
COMMON_PATH := ../../../../nativeCommon
COMMON_INCLUDE_PATH := $(COMMON_PATH)/Include
COMMON_SRC_PATH := $(COMMON_PATH)/Source

include $(CLEAR_VARS)

LOCAL_MODULE := triangle
LOCAL_CFLAGS += -DANDROID

LOCAL_C_INCLUDES := . \
                    $(COMMON_INCLUDE_PATH)

LOCAL_SRC_FILES :=  $(COMMON_SRC_PATH)/esShader.c \
                    $(COMMON_SRC_PATH)/esShapes.c \
                    $(COMMON_SRC_PATH)/esTransform.c \
                    $(COMMON_SRC_PATH)/esUtil.c \
                    $(COMMON_SRC_PATH)/Android/esUtil_Android.c \
                    glesimpl.cpp


LOCAL_LDLIBS := -llog -landroid -lEGL -lGLESv3

LOCAL_STATIC_LIBRARIES := android_native_app_glue

include $(BUILD_SHARED_LIBRARY)

$(call import-module,android/native_app_glue)
