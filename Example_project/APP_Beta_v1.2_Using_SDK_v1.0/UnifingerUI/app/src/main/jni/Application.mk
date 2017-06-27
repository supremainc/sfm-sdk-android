APP_STL := gnustl_static
APP_CPPFALGS := -frtti -fexceptions
LOCAL_CFLAGS += -march=armv7-a -mfpu=neon-vfpv4 -mfloat-abi=softfp -ftree-vectorize  -O3 -std=c99 -DNDEBUG
APP_ABI :=  armeabi armeabi-v7a arm64-v8a