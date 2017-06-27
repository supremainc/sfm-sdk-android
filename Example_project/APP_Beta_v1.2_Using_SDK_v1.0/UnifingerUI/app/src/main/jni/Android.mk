LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := serial_port
LOCAL_SRC_FILES := SerialPort.c
LOCAL_EXPORT_C_INCLUDES :=./jni
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)

ifeq ($(TARGET_ARCH_ABI),armeabi)
include $(CLEAR_VARS)
LOCAL_MODULE := UniFinger
LOCAL_SRC_FILES :=../jnilib/armeabi/libSFM_SDK_android.so
LOCAL_EXPORT_C_INCLUDES :=./jni
include $(PREBUILT_SHARED_LIBRARY)
endif

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
include $(CLEAR_VARS)
LOCAL_MODULE := UniFinger
LOCAL_SRC_FILES :=../jnilib/armeabi-v7a/libSFM_SDK_android.so
LOCAL_EXPORT_C_INCLUDES :=./jni
include $(PREBUILT_SHARED_LIBRARY)
endif

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
include $(CLEAR_VARS)
LOCAL_MODULE := UniFinger
LOCAL_SRC_FILES := ../jnilib/arm64-v8a/libSFM_SDK_android.so
LOCAL_EXPORT_C_INCLUDES :=./jni
include $(PREBUILT_SHARED_LIBRARY)
endif


include $(CLEAR_VARS)
LOCAL_EXPORT_C_INCLUDES :=./jni
LOCAL_MODULE := Command
LOCAL_SRC_FILES := Command.cpp

LOCAL_LDLIBS    := -lm -llog

APP_OPTIM := release

ifeq ($(TARGET_ARCH_ABI),$(filter $(TARGET_ARCH_ABI), arm64-v8a))
LOCAL_CFLAGS += -DARCH_ARMV8A
LOCAL_STATIC_LIBRARIES := libUniFinger
else
LOCAL_STATIC_LIBRARIES := libUniFinger
endif

LOCAL_CFLAGS += -std=c99 -DNDEBUG

include $(BUILD_SHARED_LIBRARY)






