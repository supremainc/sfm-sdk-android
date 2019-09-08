//
// Created by Brian Lee on 2019-08-22.
//

#define LOG_TAG "SFM_SDK_ANDROID_JNI"

#include <jni.h>
#include <string.h>
#include <android/log.h>
#include "com_supremainc_sfm_sdk_SFM_SDK_ANDROID.h"
#include "UF_API.h"

// Android log function wrappers
static const char* kTAG = "jni_SFM_SDK";
#define LOGI(...) \
  ((void)__android_log_print(ANDROID_LOG_INFO, kTAG, __VA_ARGS__))
#define LOGW(...) \
  ((void)__android_log_print(ANDROID_LOG_WARN, kTAG, __VA_ARGS__))
#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, kTAG, __VA_ARGS__))

#ifdef __cplusplus
extern "C"
{
#endif


jobject g_obj;
jclass g_cls;
JNIEnv *g_env;

////////////////////////////////////////////////////////////////////////////////////////////////////

jobject getObjectofRetCode(JNIEnv* env, jobject thiz, UF_RET_CODE retCode) {

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_RET_CODE");
    if (cls == nullptr) return NULL;

    jmethodID mid = env->GetStaticMethodID(cls, "ToRetCode",
                                           "(I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;");
    if (mid == nullptr) return NULL;

    jobject ret = env->CallStaticObjectMethod(cls, mid, retCode);
    if (ret == nullptr) return NULL;

    env->DeleteLocalRef(cls);

    return ret;
}

////////////////////////////////////////////////////////////////////////////////////////////////////


void SetupSerialCallback(int baudrate) {

    static jmethodID cbSetupSerial = NULL;

    if (cbSetupSerial == NULL) {
        cbSetupSerial = g_env->GetMethodID(g_cls, "cbSetupSerial", "(I)V");
        if (cbSetupSerial == NULL)
            return;
    }

    g_env->CallVoidMethod(g_obj, cbSetupSerial, baudrate);
}

jbyteArray as_byte_array(unsigned char* buf, int len) {
    jbyteArray array = g_env->NewByteArray (len);
    g_env->SetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return array;
}

unsigned char* as_unsigned_char_array(jbyteArray array) {
    int len = g_env->GetArrayLength (array);
    unsigned char* buf = new unsigned char[len];
    g_env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}


int ReadSerialCallback(unsigned char *data, int size, int timeout) {
    static jmethodID cbReadSerial = NULL;

    if (cbReadSerial == NULL) {
        cbReadSerial = g_env->GetMethodID(g_cls, "cbReadSerial", "([BI)I");
        if (cbReadSerial == NULL)
            return -1;
    }

    jbyteArray _data = g_env->NewByteArray(size);
    int ret = (int)g_env->CallIntMethod(g_obj, cbReadSerial, _data, timeout);

    if(ret > 0)
    {
        unsigned char * buf = (unsigned char*)g_env->GetByteArrayElements(_data, 0);
        memcpy(data, buf, ret);
        g_env->ReleaseByteArrayElements(_data, (jbyte *)buf, 0);
    }

    return ret;
}

int WriteSerialCallback(unsigned char *data, int size, int timeout) {

    static jmethodID cbWriteSerial = NULL;

    if (cbWriteSerial == NULL) {
        cbWriteSerial = g_env->GetMethodID(g_cls, "cbWriteSerial", "([BI)I");
        if (cbWriteSerial == NULL)
            return -1;
    }

    jbyteArray _data = as_byte_array(data, size);

    int ret = (int)g_env->CallIntMethod(g_obj,cbWriteSerial, _data, timeout);
    //g_env->DeleteLocalRef(_data);

    return ret;
}


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("JNI_OnLoad\n");
//    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&g_env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // Find your class. JNI_OnLoad is called from the correct class loader context for this to work.
    jclass cls = g_env->FindClass("com/supremainc/sfm_sdk/SFM_SDK_ANDROID");
    if (cls == nullptr) return JNI_ERR;

    g_cls = reinterpret_cast<jclass>(g_env->NewGlobalRef(cls));

    g_env->DeleteLocalRef(cls);

//    g_env = env;
//    g_cls = cls;

    return JNI_VERSION_1_6;
}

JNIEXPORT jstring JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_stringFromJNI
        (JNIEnv *env, jobject thiz) {
#if defined(__arm__)
#if defined(__ARM_ARCH_7A__)
#if defined(__ARM_NEON__)
#if defined(__ARM_PCS_VFP)
#define ABI "armeabi-v7a/NEON (hard-float)"
#else
#define ABI "armeabi-v7a/NEON"
#endif
#else
#if defined(__ARM_PCS_VFP)
#define ABI "armeabi-v7a (hard-float)"
#else
#define ABI "armeabi-v7a"
#endif
#endif
#else
#define ABI "armeabi"
#endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif
    return env->NewStringUTF( "Hello from JNI !  Compiled with ABI " ABI ".");
}

JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetSDKVersion
        (JNIEnv *env, jobject obj, jintArray version_major, jintArray version_minor,
         jintArray version_revision) {
    int major, minor, revision;

    UF_GetSDKVersion(&major, &minor, &revision);

    env->SetIntArrayRegion(version_major, 0, 1, &major);
    env->SetIntArrayRegion(version_minor, 0, 1, &minor);
    env->SetIntArrayRegion(version_revision, 0, 1, &revision);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_InitCommPort
 * Signature: (Ljava/lang/String;IZ)Lcom/supremainc/sfm_sdk/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1InitCommPort
        (JNIEnv *env, jobject obj, jstring port, jint baudrate, jboolean ascii)
{
    g_obj = obj;

    const char* temp = g_env->GetStringUTFChars(port,0);
    int ascii_mode = (ascii == true ? 1 : 0);
    int _baudrate = (int)baudrate;
    UF_RET_CODE result = UF_InitCommPort(temp, _baudrate, ascii_mode);

    g_env->ReleaseStringUTFChars(port, temp);

    return getObjectofRetCode(env, obj, result);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_CloseCommPort
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1CloseCommPort
        (JNIEnv *env, jobject obj)
{
    g_obj = obj;
    UF_RET_CODE ret = UF_CloseCommPort();
    return getObjectofRetCode(env, obj, ret);
}


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetSetupSerialCallback_Android
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetSetupSerialCallback_1Android
        (JNIEnv *env, jobject obj)
{
    g_obj = obj;
    UF_SetSetupSerialCallback_Android(SetupSerialCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetReadSerialCallback_Android
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetReadSerialCallback_1Android
        (JNIEnv *env, jobject obj)
{
    g_obj = obj;
    UF_SetReadSerialCallback_Android(ReadSerialCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetWriteSerialCallback_Android
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetWriteSerialCallback_1Android
        (JNIEnv *env, jobject obj)
{
    g_obj = obj;
    UF_SetWriteSerialCallback_Android(WriteSerialCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Reconnect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Reconnect
        (JNIEnv *env, jobject obj)
{
    g_obj = obj;
    UF_Reconnect();
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetBaudrate
 * Signature: (I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetBaudrate
        (JNIEnv *env, jobject obj, jint baudrate)
{
    g_obj = obj;
    UF_RET_CODE ret = UF_SetBaudrate(baudrate);
    return getObjectofRetCode(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetAsciiMode
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetAsciiMode
        (JNIEnv *env, jobject obj, jboolean asciiMode)
{
    g_obj = obj;
    UF_SetAsciiMode(asciiMode);
}

// Basic packet interface

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SendPacket
 * Signature: (BIIBI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SendPacket
        (JNIEnv *env, jobject obj, jbyte _command, jint _param, jint _size, jbyte _flag, jint _timeout)
{
    g_obj = obj;
    BYTE command = (BYTE)_command;
    UINT32 param = (UINT32)_param;
    UINT32 size = (UINT32)_size;
    BYTE flag = (BYTE)_flag;
    int timeout = (int)_timeout;

    UF_RET_CODE ret = UF_SendPacket(command, param, size, flag, timeout);
    return getObjectofRetCode(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SendNetworkPacket
 * Signature: (BSIIBI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SendNetworkPacket
        (JNIEnv *env, jobject obj, jbyte _command, jshort _terminalID, jint _param, jint _size, jbyte _flag, jint _timeout)
{
    g_obj = obj;
    BYTE command = (BYTE)_command;
    USHORT terminalID = (USHORT)_terminalID;
    UINT32 param = (UINT32)_param;
    UINT32 size = (UINT32)_size;
    BYTE flag = (BYTE)_flag;
    int timeout = (int)_timeout;

    UF_RET_CODE ret = UF_SendNetworkPacket(command, terminalID, param, size, flag, timeout);
    return getObjectofRetCode(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReceivePakcet
 * Signature: ([BI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReceivePakcet
        (JNIEnv *env, jobject obj, jbyteArray _packet, jint _timeout)
{
    g_obj = obj;
    jsize len = env->GetArrayLength(_packet);
    BYTE *packet = (BYTE*)env->GetByteArrayElements(_packet, 0);
    int timeout = (int)_timeout;
    UF_RET_CODE ret = UF_ReceivePacket(packet, timeout);

    if(ret == UF_RET_SUCCESS)
    {
        env->SetByteArrayRegion(_packet, 0, len, reinterpret_cast<const jbyte *>(packet));
    }

    env->ReleaseByteArrayElements(_packet, reinterpret_cast<jbyte *>(packet), 0);

    return getObjectofRetCode(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReceiveNetworkPakcet
 * Signature: ([BI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReceiveNetworkPakcet
        (JNIEnv *env, jobject obj, jbyteArray _packet, jint _timeout)
{
    g_obj = obj;
    jsize len = env->GetArrayLength(_packet);
    BYTE *packet = (BYTE*)env->GetByteArrayElements(_packet, 0);
    int timeout = (int)_timeout;
    UF_RET_CODE ret = UF_ReceiveNetworkPacket(packet, timeout);
    if(ret == UF_RET_SUCCESS)
    {
        env->SetByteArrayRegion(_packet, 0, len, reinterpret_cast<const jbyte *>(packet));
    }
    env->ReleaseByteArrayElements(_packet, reinterpret_cast<jbyte *>(packet), 0);

    return getObjectofRetCode(env, obj, ret);
}



/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetModuleID
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jlong JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetModuleID
        (JNIEnv *env, jobject obj)
{
    g_obj = obj;
    long moduleID = UF_GetModuleID();

    return moduleID;
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_InitSysParameter
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1InitSysParameter
        (JNIEnv *env, jobject obj)
{
    g_obj = obj;
    UF_InitSysParameter();
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetSysParameter
 * Signature: (Lcom/supremainc/sfm_sdk/UF_SYS_PARAM;[J)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetSysParameter
        (JNIEnv *env, jobject obj, jobject parameter, jintArray array)
{
    g_obj = obj;

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/UF_SYS_PARAM");
    if(cls == NULL) return NULL;

    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");
    if(mid == NULL) return NULL;

    jint param = env->CallIntMethod(parameter, mid);

    env->DeleteLocalRef(cls);

    jsize len = env->GetArrayLength(array);
    jint *body = env->GetIntArrayElements(array, 0);


    UINT32 value = 0;
    UF_SYS_PARAM param_id = (UF_SYS_PARAM)param;

    UF_RET_CODE ret = UF_GetSysParameter(UF_SYS_BAUDRATE, &value);

    body[0] = value;

    env->ReleaseIntArrayElements(array, body, 0);

    return getObjectofRetCode(env, obj, ret);
}


#ifdef __cplusplus
}
#endif