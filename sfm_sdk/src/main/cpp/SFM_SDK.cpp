//
// Created by Brian Lee on 2019-08-22.
//

#include <jni.h>
#include "../jni/com_supremainc_sfm_sdk_SFM_SDK_ANDROID.h"
#include "../include/UF_API.h"

JNIEXPORT jstring JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_stringFromJNI
        (JNIEnv *, jobject)
{

}

JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetSDKVersion
        (JNIEnv *env, jobject, jintArray version_major, jintArray version_minor, jintArray version_revision)
{
    int major, minor, revision;

    UF_GetSDKVersion(&major, &minor, &revision);

    env->SetIntArrayRegion(version_major, 0, 1, &major);
    env->SetIntArrayRegion(version_minor, 0, 1, &minor);
    env->SetIntArrayRegion(version_revision, 0, 1, &revision);

}