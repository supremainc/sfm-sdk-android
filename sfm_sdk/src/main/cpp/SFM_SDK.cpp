//
// Created by Brian Lee on 2019-08-22.
//

#define LOG_TAG "SFM_SDK_ANDROID_JNI"

#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <__hash_table>
#include <__split_buffer>
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


jmethodID g_msgCallback_method_id;
jmethodID g_userInfoCallback_method_id;

jobject g_msgCallback;
jobject g_userInfoCallback;

////////////////////////////////////////////////////////////////////////////////////////////////////

jobject jobjUF_RET_CODE(JNIEnv *env, jobject thiz, UF_RET_CODE retCode) {

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

jobject jobjUF_PROTOCOL(JNIEnv *env, jobject thiz, UF_PROTOCOL protocol) {

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_PROTOCOL");
    if (cls == nullptr) return NULL;

    jmethodID mid = env->GetStaticMethodID(cls, "ToProtocol",
                                           "(I)Lcom/supremainc/sfm_sdk/enumeration/UF_PROTOCOL;");
    if (mid == nullptr) return NULL;

    jobject ret = env->CallStaticObjectMethod(cls, mid, protocol);
    if (ret == nullptr) return NULL;

    env->DeleteLocalRef(cls);

    return ret;
}

jobject jobjUF_ADMIN_LEVEL(JNIEnv *env, jobject thiz, UF_ADMIN_LEVEL adminLevel) {

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_ADMIN_LEVEL");
    if (cls == nullptr) return NULL;

    jmethodID mid = env->GetStaticMethodID(cls, "ToSecurityLevel",
                                           "(I)Lcom/supremainc/sfm_sdk/enumeration/UF_ADMIN_LEVEL;");
    if (mid == nullptr) return NULL;

    jobject ret = env->CallStaticObjectMethod(cls, mid, adminLevel);
    if (ret == nullptr) return NULL;

    env->DeleteLocalRef(cls);

    return ret;
}

jobject
jobjUF_USER_SECURITY_LEVEL(JNIEnv *env, jobject thiz, UF_USER_SECURITY_LEVEL securityLevel) {

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_USER_SECURITY_LEVEL");
    if (cls == nullptr) return NULL;

    jmethodID mid = env->GetStaticMethodID(cls, "ToAdminLevel",
                                           "(I)Lcom/supremainc/sfm_sdk/enumeration/UF_USER_SECURITY_LEVEL;");
    if (mid == nullptr) return NULL;

    jobject ret = env->CallStaticObjectMethod(cls, mid, securityLevel);
    if (ret == nullptr) return NULL;

    env->DeleteLocalRef(cls);

    return ret;
}

jobject jobjUF_AUTH_TYPE(JNIEnv *env, jobject thiz, UF_AUTH_TYPE authType) {

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_AUTH_TYPE");
    if (cls == nullptr) return NULL;

    jmethodID mid = env->GetStaticMethodID(cls, "ToAuthType",
                                           "(I)Lcom/supremainc/sfm_sdk/enumeration/UF_AUTH_TYPE;");
    if (mid == nullptr) return NULL;

    jobject ret = env->CallStaticObjectMethod(cls, mid, authType);
    if (ret == nullptr) return NULL;

    env->DeleteLocalRef(cls);

    return ret;
}


//jint jintUF_PROTOCOL(JNIEnv *env, jobject thiz, jobject protocol){
//
//    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_PROTOCOL");
//    if (cls == nullptr) return NULL;
//
//    jfieldID fid = env->GetFieldID(cls, "getValue", "()I");
//
//    jmethodID mid = env->Getfield(cls, "getValue",
//                                           "()I");
//    if (mid == nullptr) return NULL;
//
//    jint ret = (jint)(env->CallIntMethod(cls, mid, protocol));
//
//    env->DeleteLocalRef(cls);
//
//    return ret;
//}


////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Utilities for JNI
 */


jbyteArray as_byte_array(unsigned char *buf, int len) {
    jbyteArray array = g_env->NewByteArray(len);
    g_env->SetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
    return array;
}

unsigned char *as_unsigned_char_array(jbyteArray array) {
    int len = g_env->GetArrayLength(array);
    unsigned char *buf = new unsigned char[len];
    g_env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
    return buf;
}

/**
 * Callback functions
 */

void SetupSerialCallback(int baudrate) {

    static jmethodID cbSetupSerial = NULL;

    if (cbSetupSerial == NULL) {
        cbSetupSerial = g_env->GetMethodID(g_cls, "cbSetupSerial", "(I)V");
        if (cbSetupSerial == NULL)
            return;
    }

    g_env->CallVoidMethod(g_obj, cbSetupSerial, baudrate);
}


int ReadSerialCallback(unsigned char *data, int size, int timeout) {
    static jmethodID cbReadSerial = NULL;

    if (cbReadSerial == NULL) {
        cbReadSerial = g_env->GetMethodID(g_cls, "cbReadSerial", "([BI)I");
        if (cbReadSerial == NULL)
            return -1;
    }

    jbyteArray _data = g_env->NewByteArray(size);
    int ret = (int) g_env->CallIntMethod(g_obj, cbReadSerial, _data, timeout);

    if (ret > 0) {
        unsigned char *buf = (unsigned char *) g_env->GetByteArrayElements(_data, 0);
        memcpy(data, buf, ret);
        g_env->ReleaseByteArrayElements(_data, (jbyte *) buf, 0);
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

    int ret = (int) g_env->CallIntMethod(g_obj, cbWriteSerial, _data, timeout);

    return ret;
}

void SendPacketCallback(unsigned char *data) {
    static jmethodID cbSendPacket = NULL;

    if (cbSendPacket == NULL) {
        cbSendPacket = g_env->GetMethodID(g_cls, "cbSendPacket", "([B)V");
        if (cbSendPacket == NULL)
            return;
    }

    jbyteArray _data = as_byte_array(data, UF_PACKET_LEN);
    g_env->CallVoidMethod(g_obj, cbSendPacket, _data);
}


void ReceivePacketCallback(unsigned char *data) {
    static jmethodID cbReceivePacket = NULL;
    if (cbReceivePacket == NULL) {
        cbReceivePacket = g_env->GetMethodID(g_cls, "cbReceivePacket", "([B)V");
        if (cbReceivePacket == NULL)
            return;
    }

    jbyteArray _data = as_byte_array(data, UF_PACKET_LEN);
    g_env->CallVoidMethod(g_obj, cbReceivePacket, _data);
}

void SendDataPacketCallback(int index, int numOfPacket) {
    static jmethodID cbSendDataPacket = NULL;
    if (cbSendDataPacket == NULL) {
        cbSendDataPacket = g_env->GetMethodID(g_cls, "cbSendDataPacket", "(II)V");
        if (cbSendDataPacket == NULL)
            return;
    }

    g_env->CallVoidMethod(g_obj, cbSendDataPacket, index, numOfPacket);
}

void ReceiveDataPacketCallback(int index, int numOfPacket) {
    static jmethodID cbReceiveDataPacket = NULL;
    if (cbReceiveDataPacket == NULL) {
        cbReceiveDataPacket = g_env->GetMethodID(g_cls, "cbReceiveDataPacket", "(II)V");
        if (cbReceiveDataPacket == NULL)
            return;
    }

    g_env->CallVoidMethod(g_obj, cbReceiveDataPacket, index, numOfPacket);
}

void SendRawDataCallback(int writtenLen, int totalSize) {
    static jmethodID cbSendRawData = NULL;
    if (cbSendRawData == NULL) {
        cbSendRawData = g_env->GetMethodID(g_cls, "cbSendRawData", "(II)V");
        if (cbSendRawData == NULL)
            return;
    }

    g_env->CallVoidMethod(g_obj, cbSendRawData, writtenLen, totalSize);
}

void ReceiveRawDataCallback(int readLen, int totalSize) {
    static jmethodID cbReceiveRawData = NULL;
    if (cbReceiveRawData == NULL) {
        cbReceiveRawData = g_env->GetMethodID(g_cls, "cbReceiveRawData", "(II)V");
        if (cbReceiveRawData == NULL)
            return;
    }

    g_env->CallVoidMethod(g_obj, cbReceiveRawData, readLen, totalSize);
}

BOOL MsgCallback(BYTE msg) {

    g_env->ExceptionClear();
    BOOL ret = (g_env->CallBooleanMethod(g_msgCallback, g_msgCallback_method_id, msg) == true ? 1
                                                                                              : 0);
    if (g_env->ExceptionOccurred())
        g_env->ExceptionClear();

    return ret;
}

void UserInfoCallback(int index, int numOfTemplate) {
    static jmethodID cbUserInfo = NULL;
    if (cbUserInfo == NULL) {
        cbUserInfo = g_env->GetMethodID(g_cls, "cbUserInfo", "(II)V");
        if (cbUserInfo == NULL)
            return;
    }
    g_env->CallVoidMethod(g_obj, cbUserInfo, index, numOfTemplate);
}

void ScanCallback(BYTE msg) {
    static jmethodID cbScan = NULL;
    if (cbScan == NULL) {
        cbScan = g_env->GetMethodID(g_cls, "cbScan", "(B)V");
        if (cbScan == NULL)
            return;
    }

    LOGE("Scan Success\n");

    g_env->CallVoidMethod(g_obj, cbScan, msg);
}


/**
 * JNI_OnLoad
 */

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
    return env->NewStringUTF("Hello from JNI !  Compiled with ABI " ABI ".");
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
        (JNIEnv *env, jobject obj, jstring port, jint baudrate, jboolean ascii) {
    g_obj = obj;

    const char *temp = g_env->GetStringUTFChars(port, 0);
    BOOL ascii_mode = (ascii == true ? 1 : 0);
    int _baudrate = (int) baudrate;
    UF_RET_CODE result = UF_InitCommPort(temp, _baudrate, ascii_mode);

    g_env->ReleaseStringUTFChars(port, temp);

    return jobjUF_RET_CODE(env, obj, result);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_CloseCommPort
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1CloseCommPort
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_CloseCommPort();
    return jobjUF_RET_CODE(env, obj, ret);
}


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetSetupSerialCallback_Android
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetSetupSerialCallback_1Android
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_SetSetupSerialCallback_Android(SetupSerialCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetReadSerialCallback_Android
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetReadSerialCallback_1Android
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_SetReadSerialCallback_Android(ReadSerialCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetWriteSerialCallback_Android
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetWriteSerialCallback_1Android
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_SetWriteSerialCallback_Android(WriteSerialCallback);
    UF_SetSendPacketCallback(SendPacketCallback);
    UF_SetReceivePacketCallback(ReceivePacketCallback);
    UF_SetSendDataPacketCallback(SendDataPacketCallback);
    UF_SetReceiveDataPacketCallback(ReceiveDataPacketCallback);
    UF_SetSendRawDataCallback(SendRawDataCallback);
    UF_SetReceiveRawDataCallback(ReceiveRawDataCallback);
    UF_SetScanCallback(ScanCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Reconnect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Reconnect
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_Reconnect();
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetBaudrate
 * Signature: (I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetBaudrate
        (JNIEnv *env, jobject obj, jint baudrate) {
    g_obj = obj;
    UF_RET_CODE ret = UF_SetBaudrate(baudrate);
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetAsciiMode
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetAsciiMode
        (JNIEnv *env, jobject obj, jboolean asciiMode) {
    g_obj = obj;
    UF_SetAsciiMode(asciiMode);
}

/**
 * Basic packet interface
 */


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SendPacket
 * Signature: (BIIBI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SendPacket
        (JNIEnv *env, jobject obj, jbyte _command, jint _param, jint _size, jbyte _flag,
         jint _timeout) {
    g_obj = obj;
    BYTE command = (BYTE) _command;
    UINT32 param = (UINT32) _param;
    UINT32 size = (UINT32) _size;
    BYTE flag = (BYTE) _flag;
    int timeout = (int) _timeout;

    UF_RET_CODE ret = UF_SendPacket(command, param, size, flag, timeout);
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SendNetworkPacket
 * Signature: (BSIIBI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SendNetworkPacket
        (JNIEnv *env, jobject obj, jbyte _command, jshort _terminalID, jint _param, jint _size,
         jbyte _flag, jint _timeout) {
    g_obj = obj;
    BYTE command = (BYTE) _command;
    USHORT terminalID = (USHORT) _terminalID;
    UINT32 param = (UINT32) _param;
    UINT32 size = (UINT32) _size;
    BYTE flag = (BYTE) _flag;
    int timeout = (int) _timeout;

    UF_RET_CODE ret = UF_SendNetworkPacket(command, terminalID, param, size, flag, timeout);
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReceivePakcet
 * Signature: ([BI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReceivePakcet
        (JNIEnv *env, jobject obj, jbyteArray _packet, jint _timeout) {
    g_obj = obj;
    jsize len = env->GetArrayLength(_packet);
    BYTE *packet = (BYTE *) env->GetByteArrayElements(_packet, 0);
    int timeout = (int) _timeout;
    UF_RET_CODE ret = UF_ReceivePacket(packet, timeout);

    if (ret == UF_RET_SUCCESS) {
        env->SetByteArrayRegion(_packet, 0, len, reinterpret_cast<const jbyte *>(packet));
    }

    env->ReleaseByteArrayElements(_packet, reinterpret_cast<jbyte *>(packet), 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReceiveNetworkPakcet
 * Signature: ([BI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReceiveNetworkPakcet
        (JNIEnv *env, jobject obj, jbyteArray _packet, jint _timeout) {
    g_obj = obj;
    jsize len = env->GetArrayLength(_packet);
    BYTE *packet = (BYTE *) env->GetByteArrayElements(_packet, 0);
    int timeout = (int) _timeout;
    UF_RET_CODE ret = UF_ReceiveNetworkPacket(packet, timeout);
    if (ret == UF_RET_SUCCESS) {
        env->SetByteArrayRegion(_packet, 0, len, reinterpret_cast<const jbyte *>(packet));
    }
    env->ReleaseByteArrayElements(_packet, reinterpret_cast<jbyte *>(packet), 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SendRawData
 * Signature: ([BII)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SendRawData
        (JNIEnv *env, jobject obj, jbyteArray _buf, jint _size, jint _timeout) {
    g_obj = obj;
    jsize len = env->GetArrayLength(_buf);
    BYTE *buf = (BYTE *) env->GetByteArrayElements(_buf, 0);
    int size = (int) _size;
    int timeout = (int) _timeout;
    UF_RET_CODE ret = UF_SendRawData(buf, size, timeout);
    if (ret == UF_RET_SUCCESS) {
        env->SetByteArrayRegion(_buf, 0, len, reinterpret_cast<const jbyte *>(buf));
    }
    env->ReleaseByteArrayElements(_buf, reinterpret_cast<jbyte *>(buf), 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReceiveRawData
 * Signature: ([BIIZ)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReceiveRawData
        (JNIEnv *env, jobject obj, jbyteArray _buf, jint _size, jint _timeout,
         jboolean _checkEndCode) {
    g_obj = obj;
    jsize len = env->GetArrayLength(_buf);
    BYTE *buf = (BYTE *) env->GetByteArrayElements(_buf, 0);
    int size = (int) _size;
    int timeout = (int) _timeout;
    BOOL checkEndCode = (_checkEndCode == true ? 1 : 0);
    UF_RET_CODE ret = UF_ReceiveRawData(buf, size, timeout, checkEndCode);
    if (ret == UF_RET_SUCCESS) {
        env->SetByteArrayRegion(_buf, 0, len, reinterpret_cast<const jbyte *>(buf));
    }
    env->ReleaseByteArrayElements(_buf, reinterpret_cast<jbyte *>(buf), 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SendDataPacket
 * Signature: (B[BII)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SendDataPacket
        (JNIEnv *env, jobject obj, jbyte _command, jbyteArray _buf, jint _dataSize,
         jint _dataPacketSize) {
    g_obj = obj;
    jsize len = env->GetArrayLength(_buf);
    BYTE *buf = (BYTE *) env->GetByteArrayElements(_buf, 0);
    BYTE command = (BYTE) _command;
    int dataSize = (int) _dataSize;
    int dataPacketSize = (int) _dataPacketSize;
    UF_RET_CODE ret = UF_SendDataPacket(command, buf, dataSize, dataPacketSize);
    if (ret == UF_RET_SUCCESS) {
        env->SetByteArrayRegion(_buf, 0, len, reinterpret_cast<const jbyte *>(buf));
    }
    env->ReleaseByteArrayElements(_buf, reinterpret_cast<jbyte *>(buf), 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReceiveDataPacket
 * Signature: (B[BI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReceiveDataPacket
        (JNIEnv *env, jobject obj, jbyte _command, jbyteArray _buf, jint _dataPacketSize) {
    g_obj = obj;
    jsize len = env->GetArrayLength(_buf);
    BYTE *pBuf = (BYTE *) env->GetByteArrayElements(_buf, 0);
    BYTE command = (BYTE) _command;
    int dataPacketSize = (int) _dataPacketSize;
    UF_RET_CODE ret = UF_ReceiveDataPacket(command, pBuf, dataPacketSize);
    if (ret == UF_RET_SUCCESS) {
        env->SetByteArrayRegion(_buf, 0, len, reinterpret_cast<const jbyte *>(pBuf));
    }
    env->ReleaseByteArrayElements(_buf, reinterpret_cast<jbyte *>(pBuf), 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetDefaultPacketSize
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetDefaultPacketSize
        (JNIEnv *env, jobject obj, jint _defaultSize) {
    g_obj = obj;
    int defaultSize = _defaultSize;
    UF_SetDefaultPacketSize(defaultSize);
}


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetDefaultPacketSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetDefaultPacketSize
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    jint ret = (jint) UF_GetDefaultPacketSize();
    return ret;
}

/**
 * Generic command interface
 */


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetProtocol
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetProtocol
        (JNIEnv *env, jobject obj, jobject _protocol, jint _moduleID) {
    g_obj = obj;

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_PROTOCOL");
    if (cls == NULL) return;

    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");
    if (mid == NULL) return;

    UF_PROTOCOL protocol = (UF_PROTOCOL) env->CallIntMethod(_protocol, mid);

    env->DeleteLocalRef(cls);

    UINT32 moduleID = (UINT32) _moduleID;
    UF_SetProtocol(protocol, moduleID);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetProtocol
 * Signature: ()I
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetProtocol
        (JNIEnv *env, jobject obj) {
    g_obj = obj;

    UF_PROTOCOL protocol = (UF_PROTOCOL) UF_GetProtocol();
    return jobjUF_PROTOCOL(env, obj, protocol);
}


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetModuleID
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetModuleID
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    jint moduleID = (jint) UF_GetModuleID();

    return moduleID;
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetGenericCommandTimeout
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetGenericCommandTimeout
        (JNIEnv *env, jobject obj, jint _timeout) {
    g_obj = obj;
    int timeout = (int) _timeout;
    UF_SetGenericCommandTimeout(timeout);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetInputCommandTimeout
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetInputCommandTimeout
        (JNIEnv *env, jobject obj, jint _timeout) {
    g_obj = obj;
    int timeout = (int) _timeout;
    UF_SetInputCommandTimeout(timeout);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetGenericCommandTimeout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetGenericCommandTimeout
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    int ret = UF_GetGenericCommandTimeout();
    return ret;
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetInputCommandTimeout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetInputCommandTimeout
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    int ret = UF_GetInputCommandTimeout();
    return ret;
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetNetWorkDelay
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetNetWorkDelay
        (JNIEnv *env, jobject obj, jint _delay) {
    g_obj = obj;
    int delay = (int) _delay;
    UF_SetNetworkDelay(delay);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetNetworkDelay
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetNetworkDelay
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    int ret = UF_GetNetworkDelay();
    return ret;
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Command
 * Signature: (B[I[I[B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Command
        (JNIEnv *env, jobject obj, jbyte _command, jintArray _param, jintArray _size,
         jbyteArray _flag) {
    g_obj = obj;
    BYTE command = (BYTE) _command;
    jint *pParam = env->GetIntArrayElements(_param, 0);
    jint *pSize = env->GetIntArrayElements(_size, 0);
    jbyte *pFlag = env->GetByteArrayElements(_flag, 0);


    UF_RET_CODE ret = UF_Command(command, reinterpret_cast<UINT32 *>(pParam),
                                 reinterpret_cast<UINT32 *>(pSize),
                                 reinterpret_cast<BYTE *>(pFlag));

    if (ret == UF_RET_SUCCESS) {
        env->SetIntArrayRegion(_param, 0, 1, pParam);
        env->SetIntArrayRegion(_size, 0, 1, pSize);
        env->SetByteArrayRegion(_flag, 0, 1, pFlag);
    }

    env->ReleaseIntArrayElements(_param, pParam, 0);
    env->ReleaseIntArrayElements(_size, pSize, 0);
    env->ReleaseByteArrayElements(_flag, pFlag, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_CommandEx
 * Signature: (B[I[I[BLcom/supremainc/sfm_sdk/SFM_SDK_ANDROID/MsgCallback;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1CommandEx
        (JNIEnv *env, jobject obj, jbyte _command, jintArray _param, jintArray _size,
         jbyteArray _flag, jobject _callback) {
    g_obj = obj;

    BYTE command = (BYTE) _command;
    jint *pParam = env->GetIntArrayElements(_param, 0);
    jint *pSize = env->GetIntArrayElements(_size, 0);
    jbyte *pFlag = env->GetByteArrayElements(_flag, 0);
    BOOL (*msgCallback)(BYTE) = NULL;

    jclass objclass = env->GetObjectClass(_callback);
    jmethodID method = env->GetMethodID(objclass, "callback", "(B)Z");
    if (method == nullptr) {
        return NULL;
    }
    g_msgCallback_method_id = method;
    g_msgCallback = _callback;

    UF_RET_CODE ret = UF_CommandEx(command, reinterpret_cast<UINT32 *>(pParam),
                                   reinterpret_cast<UINT32 *>(pSize),
                                   reinterpret_cast<BYTE *>(pFlag), MsgCallback);

    LOGI("UF_CommandEx : %X %X %X\n", pParam[0], pSize[0], pFlag[0]);

    if (ret == UF_RET_SUCCESS) {
        env->SetIntArrayRegion(_param, 0, 1, pParam);
        env->SetIntArrayRegion(_size, 0, 1, pSize);
        env->SetByteArrayRegion(_flag, 0, 1, pFlag);
    }
    env->ReleaseIntArrayElements(_param, pParam, 0);
    env->ReleaseIntArrayElements(_size, pSize, 0);
    env->ReleaseByteArrayElements(_flag, pFlag, 0);
    env->DeleteLocalRef(objclass);

    return jobjUF_RET_CODE(env, obj, ret);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_CommandSendData
 * Signature: (B[I[I[B[BI)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1CommandSendData
        (JNIEnv *env, jobject obj, jbyte _command, jintArray _param, jintArray _size,
         jbyteArray _flag, jbyteArray _data, jint _dataSize) {
    g_obj = obj;
    BYTE command = (BYTE) _command;
    jint *pParam = env->GetIntArrayElements(_param, 0);
    jint *pSize = env->GetIntArrayElements(_size, 0);
    jbyte *pFlag = env->GetByteArrayElements(_flag, 0);
    jsize len = env->GetArrayLength(_data);
    jbyte *pData = env->GetByteArrayElements(_data, 0);
    UINT32 dataSize = (UINT32) _dataSize;


    UF_RET_CODE ret = UF_CommandSendData(command, reinterpret_cast<UINT32 *>(pParam),
                                         reinterpret_cast<UINT32 *>(pSize),
                                         reinterpret_cast<BYTE *>(pFlag),
                                         reinterpret_cast<BYTE *>(pData), dataSize);


    if (ret == UF_RET_SUCCESS) {
        env->SetIntArrayRegion(_param, 0, 1, pParam);
        env->SetIntArrayRegion(_size, 0, 1, pSize);
        env->SetByteArrayRegion(_flag, 0, 1, pFlag);
        env->SetByteArrayRegion(_data, 0, len, pData);
    }
    env->ReleaseIntArrayElements(_param, pParam, 0);
    env->ReleaseIntArrayElements(_size, pSize, 0);
    env->ReleaseByteArrayElements(_flag, pFlag, 0);
    env->ReleaseByteArrayElements(_data, pData, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_CommandSendDataEx
 * Signature: (B[I[I[B[BILcom/supremainc/sfm_sdk/SFM_SDK_ANDROID/MsgCallback;Z)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1CommandSendDataEx
        (JNIEnv *env, jobject obj, jbyte _command, jintArray _param, jintArray _size,
         jbyteArray _flag, jbyteArray _data, jint _datasize, jobject _callback,
         jboolean _waitUserInput) {
    g_obj = obj;

    BYTE command = (BYTE) _command;
    jint *pParam = env->GetIntArrayElements(_param, 0);
    jint *pSize = env->GetIntArrayElements(_size, 0);
    jbyte *pFlag = env->GetByteArrayElements(_flag, 0);
    jsize len = env->GetArrayLength(_data);
    jbyte *pData = env->GetByteArrayElements(_data, 0);
    UINT32 dataSize = (UINT32) _datasize;
    BOOL (*msgCallback)(BYTE) = NULL;
    BOOL waitUserInput = (BOOL) (_waitUserInput == true ? 1 : 0);

    UINT32 param;
    UINT32 size;
    BYTE flag;

    jclass objclass = env->GetObjectClass(_callback);
    jmethodID method = env->GetMethodID(objclass, "callback", "(B)Z");
    if (method == nullptr) {
        return NULL;
    }
    g_msgCallback_method_id = method;
    g_msgCallback = _callback;

    UF_RET_CODE ret = UF_CommandSendDataEx(command, reinterpret_cast<UINT32 *>(pParam),
                                           reinterpret_cast<UINT32 *>(pSize),
                                           reinterpret_cast<BYTE *>(pFlag),
                                           reinterpret_cast<BYTE *>(pData), dataSize, MsgCallback,
                                           waitUserInput);
    if (ret == UF_RET_SUCCESS) {
        env->SetIntArrayRegion(_param, 0, 1, pParam);
        env->SetIntArrayRegion(_size, 0, 1, pSize);
        env->SetByteArrayRegion(_flag, 0, 1, pFlag);
        env->SetByteArrayRegion(_data, 0, len, pData);
    }
    env->ReleaseIntArrayElements(_param, pParam, 0);
    env->ReleaseIntArrayElements(_size, pSize, 0);
    env->ReleaseByteArrayElements(_flag, pFlag, 0);
    env->ReleaseByteArrayElements(_data, pData, 0);
    env->DeleteLocalRef(objclass);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Cancel
 * Signature: (Z)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Cancel
        (JNIEnv *env, jobject obj, jboolean _receivePacket) {
    g_obj = obj;

    BOOL receivePacket = (BOOL) (_receivePacket == true ? 1 : 0);
    UF_RET_CODE ret = UF_Cancel(receivePacket);

    return jobjUF_RET_CODE(env, obj, ret);
}


/**
 * Module Information
 */
/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetModuleInfo
 * Signature: (Lcom/supremainc/sfm_sdk/structure/UFModuleInfo;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetModuleInfo
        (JNIEnv *env, jobject obj, jobject _info) {
    g_obj = obj;

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/structure/UFModuleInfo");
    jmethodID mid = env->GetMethodID(cls, "<init>",
                                     "(Lcom/supremainc/sfm_sdk/UF_MODULE_TYPE;Lcom/supremainc/sfm_sdk/UF_MODULE_VERSION;Lcom/supremainc/sfm_sdk/UF_MODULE_SENSOR;)V");

    jclass cls_type = env->FindClass("com/supremainc/sfm_sdk/UF_MODULE_TYPE");
    if (cls_type == NULL) return NULL;

    jclass cls_version = env->FindClass("com/supremainc/sfm_sdk/UF_MODULE_VERSION");
    if (cls_version == NULL) return NULL;

    jclass cls_sensorType = env->FindClass("com/supremainc/sfm_sdk/UF_MODULE_SENSOR");
    if (cls_sensorType == NULL) return NULL;

    jmethodID mid_type = env->GetStaticMethodID(cls_type, "ToObjectType",
                                                "(I)Lcom/supremainc/sfm_sdk/UF_MODULE_TYPE;");
    if (mid_type == NULL) return NULL;

    jmethodID mid_version = env->GetStaticMethodID(cls_version, "ToObjectType",
                                                   "(I)Lcom/supremainc/sfm_sdk/UF_MODULE_VERSION;");
    if (mid_version == NULL) return NULL;

    jmethodID mid_sensorType = env->GetStaticMethodID(cls_sensorType, "ToObjectType",
                                                      "(I)Lcom/supremainc/sfm_sdk/UF_MODULE_SENSOR;");
    if (mid_sensorType == NULL) return NULL;

    int type;
    int version;
    int sensorType;

    UF_RET_CODE ret = UF_GetModuleInfo(reinterpret_cast<UF_MODULE_TYPE *>(&type),
                                       reinterpret_cast<UF_MODULE_VERSION *>(&version),
                                       reinterpret_cast<UF_MODULE_SENSOR *>(&sensorType));

    jobject typeObj = env->CallStaticObjectMethod(cls_type, mid_type, type);
    jobject versionObj = env->CallStaticObjectMethod(cls_version, mid_version, version);
    jobject sensorTypeObj = env->CallStaticObjectMethod(cls_sensorType, mid_sensorType, sensorType);

    jobject newObj = env->NewObject(cls, mid, typeObj, versionObj, sensorTypeObj);

    jmethodID mm = env->GetMethodID(cls, "setInfo",
                                    "(Lcom/supremainc/sfm_sdk/structure/UFModuleInfo;)V");

    env->CallVoidMethod(_info, mm, newObj);

    env->DeleteLocalRef(newObj);
    env->DeleteLocalRef(cls_type);
    env->DeleteLocalRef(cls_version);
    env->DeleteLocalRef(cls_sensorType);

    return jobjUF_RET_CODE(env, obj, ret);
}


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetModuleString
 * Signature: (Lcom/supremainc/sfm_sdk/UF_MODULE_TYPE;Lcom/supremainc/sfm_sdk/UF_MODULE_VERSION;Lcom/supremainc/sfm_sdk/UF_MODULE_SENSOR;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetModuleString
        (JNIEnv *env, jobject obj, jobject _type, jobject _version, jobject _sensorType) {
    g_obj = obj;

    jclass cls_type = env->FindClass("com/supremainc/sfm_sdk/UF_MODULE_TYPE");
    if (cls_type == NULL) return NULL;

    jclass cls_version = env->FindClass("com/supremainc/sfm_sdk/UF_MODULE_VERSION");
    if (cls_version == NULL) return NULL;

    jclass cls_sensorType = env->FindClass("com/supremainc/sfm_sdk/UF_MODULE_SENSOR");
    if (cls_sensorType == NULL) return NULL;

    jmethodID mid_type = env->GetMethodID(cls_type, "getValue", "()I");
    if (mid_type == NULL) return NULL;

    jmethodID mid_version = env->GetMethodID(cls_version, "getValue", "()I");
    if (mid_version == NULL) return NULL;

    jmethodID mid_sensorType = env->GetMethodID(cls_sensorType, "getValue", "()I");
    if (mid_sensorType == NULL) return NULL;

    int type = env->CallIntMethod(_type, mid_type);
    int version = env->CallIntMethod(_version, mid_version);
    int sensorType = env->CallIntMethod(_sensorType, mid_sensorType);

    char *moduleString = UF_GetModuleString(static_cast<UF_MODULE_TYPE>(type),
                                            static_cast<UF_MODULE_VERSION>(version),
                                            static_cast<UF_MODULE_SENSOR>(sensorType));

    if (moduleString == NULL)
        return NULL;

    return env->NewStringUTF(moduleString);


}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SearchModule
 * Signature: ([B[I[Z[Lcom/supremainc/sfm_sdk/enumeration/UF_PROTOCOL;[ILcom/supremainc/sfm_sdk/SFM_SDK_ANDROID/SerialCallback;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SearchModule
        (JNIEnv *, jobject, jbyteArray, jintArray, jbooleanArray, jobjectArray, jintArray, jobject);

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SearchModuleID
 * Signature: ([I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SearchModuleID
        (JNIEnv *, jobject, jintArray);

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SearchModuleIDEx
 * Signature: ([SI[S[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SearchModuleIDEx
        (JNIEnv *, jobject, jshortArray, jint, jshortArray, jintArray);

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_CalibrateSensor
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1CalibrateSensor
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_CalibrateSensor();
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Reset
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Reset
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_Reset();

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Lock
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Lock
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_Lock();

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Unlock
 * Signature: ([B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Unlock
        (JNIEnv *env, jobject obj, jbyteArray _password) {
    g_obj = obj;

    jbyte *password = env->GetByteArrayElements(_password, 0);

    if (password == NULL)
        return NULL;

    UF_RET_CODE ret = UF_Unlock(reinterpret_cast<const unsigned char *>(password));

    env->ReleaseByteArrayElements(_password, password, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ChangePassword
 * Signature: ([B[B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ChangePassword
        (JNIEnv *env, jobject obj, jbyteArray _newPassword, jbyteArray _oldPassword) {
    g_obj = obj;

    jbyte *newPassword = env->GetByteArrayElements(_newPassword, 0);
    jbyte *oldPassword = env->GetByteArrayElements(_oldPassword, 0);

    UF_RET_CODE ret = UF_ChangePassword(reinterpret_cast<const unsigned char *>(newPassword),
                                        reinterpret_cast<const unsigned char *>(oldPassword));

    env->ReleaseByteArrayElements(_newPassword, newPassword, 0);
    env->ReleaseByteArrayElements(_oldPassword, oldPassword, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReadChallengeCode
 * Signature: ([B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReadChallengeCode
        (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_WriteChallengeCode
 * Signature: ([B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1WriteChallengeCode
        (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_PowerOff
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1PowerOff
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_PowerOff();

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_InitSysParameter
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1InitSysParameter
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_InitSysParameter();
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetSysParameter
 * Signature: (Lcom/supremainc/sfm_sdk/UF_SYS_PARAM;[J)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetSysParameter
        (JNIEnv *env, jobject obj, jobject _parameter, jintArray _value) {
    g_obj = obj;

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/UF_SYS_PARAM");
    if (cls == NULL) return NULL;

    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");
    if (mid == NULL) return NULL;

    jint parameter = env->CallIntMethod(_parameter, mid);

    env->DeleteLocalRef(cls);

    jint *pValue = env->GetIntArrayElements(_value, 0);


    UF_SYS_PARAM param_id = (UF_SYS_PARAM) parameter;

    UF_RET_CODE ret = UF_GetSysParameter(param_id, reinterpret_cast<UINT32 *>(pValue));

    if (ret == UF_RET_SUCCESS) {
        env->SetIntArrayRegion(_value, 0, 1, pValue);
        LOGI("ParamID in JNI : 0x%02X , value : %X\n", param_id, pValue[0]);

    }
    env->ReleaseIntArrayElements(_value, pValue, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetSysParameter
 * Signature: (Lcom/supremainc/sfm_sdk/UF_SYS_PARAM;I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetSysParameter
        (JNIEnv *env, jobject obj, jobject _parameter, jint _value) {
    g_obj = obj;
    jclass cls = env->FindClass("com/supremainc/sfm_sdk/UF_SYS_PARAM");
    if (cls == NULL) return NULL;

    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");
    if (mid == NULL) return NULL;

    jint parameter = env->CallIntMethod(_parameter, mid);
    UINT32 value = (UINT32) _value;
    env->DeleteLocalRef(cls);

    UF_RET_CODE ret = UF_SetSysParameter((UF_SYS_PARAM) parameter, value);
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetMultiSysParameter
 * Signature: (I[Lcom/supremainc/sfm_sdk/UF_SYS_PARAM;[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetMultiSysParameter
        (JNIEnv *env, jobject obj, jint _parameterCount, jobjectArray _parameters,
         jintArray _values) {

    g_obj = obj;

    int parameterCount = _parameterCount;

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/UF_SYS_PARAM");
    if (cls == NULL) return NULL;

    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");
    if (mid == NULL) return NULL;

    env->DeleteLocalRef(cls);

    jsize paramSize = env->GetArrayLength(_parameters);
    jsize valueSize = env->GetArrayLength(_values);
    jint *values = env->GetIntArrayElements(_values, 0);


    int *parameters = new int[paramSize];

    for (int i = 0; i < paramSize; i++) {
        parameters[i] = (int) env->CallIntMethod(env->GetObjectArrayElement(_parameters, i), mid);
    }

    UF_RET_CODE ret = UF_GetMultiSysParameter(parameterCount, (UF_SYS_PARAM *) parameters,
                                              reinterpret_cast<UINT32 *>(values));

    if (ret == UF_RET_SUCCESS) {
        env->SetIntArrayRegion(_values, 0, valueSize, values);
    }
    env->ReleaseIntArrayElements(_values, values, 0);

    delete[] parameters;

    return jobjUF_RET_CODE(env, obj, ret);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetMultiSysParameter
 * Signature: (I[Lcom/supremainc/sfm_sdk/UF_SYS_PARAM;[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetMultiSysParameter
        (JNIEnv *env, jobject obj, jint _parameterCount, jobjectArray _parameters,
         jintArray _values) {
    g_obj = obj;

    int parameterCount = _parameterCount;

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/UF_SYS_PARAM");
    if (cls == NULL) return NULL;

    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");
    if (mid == NULL) return NULL;

    env->DeleteLocalRef(cls);

    jsize paramSize = env->GetArrayLength(_parameters);
    jsize valueSize = env->GetArrayLength(_values);
    int *parameters = new int[paramSize];
    int *values = new int[valueSize];

    env->GetIntArrayRegion(_values, 0, valueSize, values);

    for (int i = 0; i < paramSize; i++) {
        parameters[i] = (int) env->CallIntMethod(env->GetObjectArrayElement(_parameters, i), mid);
        LOGI("parameters : %02X , values : %02X", parameters[i], values[i]);
    }

    UF_RET_CODE ret = UF_SetMultiSysParameter(parameterCount, (UF_SYS_PARAM *) parameters,
                                              reinterpret_cast<UINT32 *>(values));

    delete[] parameters;
    delete[] values;

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Save
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Save
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_Save();
    return jobjUF_RET_CODE(env, obj, ret);
}

/**
 * Template Management
 */

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetNumOfTemplate
 * Signature: ([I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetNumOfTemplate
        (JNIEnv *env, jobject obj, jintArray _numberOfTemplate) {
    g_obj = obj;

    jint *numberOfTemplate = env->GetIntArrayElements(_numberOfTemplate, 0);

    UF_RET_CODE ret = UF_GetNumOfTemplate(reinterpret_cast<UINT32 *>(numberOfTemplate));

    env->ReleaseIntArrayElements(_numberOfTemplate, numberOfTemplate, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetMaxNumOfTemplate
 * Signature: ([I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetMaxNumOfTemplate
        (JNIEnv *env, jobject obj, jintArray _maxNumOfTemplate) {
    g_obj = obj;

    jint *maxNumOfTemplate = env->GetIntArrayElements(_maxNumOfTemplate, 0);

    UF_RET_CODE ret = UF_GetMaxNumOfTemplate(reinterpret_cast<UINT32 *>(maxNumOfTemplate));

    env->ReleaseIntArrayElements(_maxNumOfTemplate, maxNumOfTemplate, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetAllUserInfo
 * Signature: ([Lcom/supremainc/sfm_sdk/structure/UFUserInfo;[I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetAllUserInfo
        (JNIEnv *env, jobject obj, jobjectArray _userInfo, jintArray _numOfUser,
         jintArray _numOfTemplate) {
    g_obj = obj;
    int i = 0;
    if (_userInfo == NULL)
        return NULL;

    jsize UFUserInfoLen = env->GetArrayLength(_userInfo);
    UFUserInfo *pUserInfo = new UFUserInfo[UFUserInfoLen];
    int numOfUser = 0;
    int numOfTemplate = 0;

    UF_RET_CODE ret = UF_GetAllUserInfo(pUserInfo, reinterpret_cast<UINT32 *>(&numOfUser),
                                        reinterpret_cast<UINT32 *>(&numOfTemplate));

    if (ret == UF_RET_SUCCESS) {
        for (i = 0; i < numOfUser; i++) {

            jobject userInfoObj = env->GetObjectArrayElement(_userInfo, i);
            jclass cls_userInfoObj = env->GetObjectClass(userInfoObj);
            jfieldID fUserID = env->GetFieldID(cls_userInfoObj, "_userID", "I");
            jfieldID fNumOfTemplate = env->GetFieldID(cls_userInfoObj, "_numOfTemplate", "B");
            jfieldID fAdminLevel = env->GetFieldID(cls_userInfoObj, "_adminLevel", "B");
            jfieldID fReserved = env->GetFieldID(cls_userInfoObj, "_reserved", "[B");

            env->SetIntField(userInfoObj, fUserID, pUserInfo[i].userID);
            env->SetByteField(userInfoObj, fNumOfTemplate, pUserInfo[i].numOfTemplate);
            env->SetByteField(userInfoObj, fAdminLevel, pUserInfo[i].adminLevel);
            LOGI("userID : %d numOfTemplate : %d adminLevel : %d", pUserInfo[i].userID,
                 pUserInfo[i].numOfTemplate, pUserInfo[i].adminLevel);

            jobject reserved_obj = env->GetObjectField(userInfoObj, fReserved);
            env->SetByteArrayRegion(static_cast<jbyteArray>(reserved_obj), 0, 2,
                                    reinterpret_cast<const jbyte *>(pUserInfo[i].reserved));
            env->DeleteLocalRef(cls_userInfoObj);
        }

        env->SetIntArrayRegion(_numOfUser, 0, 1, &numOfUser);
        env->SetIntArrayRegion(_numOfTemplate, 0, 1, &numOfTemplate);
    }

    delete[] pUserInfo;
    return jobjUF_RET_CODE(env, obj, ret);

}
/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetAllUserInfoEx
 * Signature: ([Lcom/supremainc/sfm_sdk/structure/UFUserInfoEx;[I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetAllUserInfoEx
        (JNIEnv *env, jobject obj, jobjectArray _userInfo, jintArray _numOfUser,
         jintArray _numOfTemplate) {
    g_obj = obj;
    int i = 0;
    if (_userInfo == NULL)
        return NULL;

    jsize UFUserInfoLen = env->GetArrayLength(_userInfo);
    UFUserInfoEx *pUserInfo = new UFUserInfoEx[UFUserInfoLen];
    int numOfUser = 0;
    int numOfTemplate = 0;

    UF_RET_CODE ret = UF_GetAllUserInfoEx(pUserInfo, reinterpret_cast<UINT32 *>(&numOfUser),
                                          reinterpret_cast<UINT32 *>(&numOfTemplate));

    if (ret == UF_RET_SUCCESS) {
        for (i = 0; i < numOfUser; i++) {

            jobject userInfoObj = env->GetObjectArrayElement(_userInfo, i);
            jclass cls_userInfoObj = env->GetObjectClass(userInfoObj);
            jfieldID fUserID = env->GetFieldID(cls_userInfoObj, "_userID", "I");
            jfieldID fNumOfTemplate = env->GetFieldID(cls_userInfoObj, "_numOfTemplate", "B");
            jfieldID fAdminLevel = env->GetFieldID(cls_userInfoObj, "_adminLevel", "B");
            jfieldID fAuthMode = env->GetFieldID(cls_userInfoObj, "_authMode", "B");
            jfieldID fSecurityLevel = env->GetFieldID(cls_userInfoObj, "_securityLevel", "B");

            jfieldID fChecksum = env->GetFieldID(cls_userInfoObj, "_checkSum", "[I");
            jfieldID fDuress = env->GetFieldID(cls_userInfoObj, "_duress", "[B");


            env->SetIntField(userInfoObj, fUserID, pUserInfo[i].userID);
            env->SetByteField(userInfoObj, fNumOfTemplate, pUserInfo[i].numOfTemplate);
            env->SetByteField(userInfoObj, fAdminLevel, pUserInfo[i].adminLevel);
            env->SetByteField(userInfoObj, fAuthMode, pUserInfo[i].authMode);
            env->SetByteField(userInfoObj, fSecurityLevel, pUserInfo[i].securityLevel);

            jobject checksumArray = env->GetObjectField(userInfoObj, fChecksum);
            jobject duressArray = env->GetObjectField(userInfoObj, fDuress);
            env->SetIntArrayRegion(static_cast<jintArray>(checksumArray), 0, 10,
                                   reinterpret_cast<const jint *>(pUserInfo[i].checksum));

            env->SetByteArrayRegion(static_cast<jbyteArray>(duressArray), 0, 10,
                                    reinterpret_cast<const jbyte *>(pUserInfo[i].duress));

            env->DeleteLocalRef(cls_userInfoObj);
        }

        env->SetIntArrayRegion(_numOfUser, 0, 1, &numOfUser);
        env->SetIntArrayRegion(_numOfTemplate, 0, 1, &numOfTemplate);
    }

    delete[] pUserInfo;
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SortUserInfo
 * Signature: ([Lcom/supremainc/sfm_sdk/structure/UFUserInfo;I)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SortUserInfo
        (JNIEnv *, jobject, jobjectArray, jint);

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetUserInfoCallback
 * Signature: (Lcom/supremainc/sfm_sdk/SFM_SDK_ANDROID/UserInfoCallback;)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetUserInfoCallback
        (JNIEnv *env, jobject obj, jobject _callback) {
    g_obj = obj;

    UF_SetUserInfoCallback(UserInfoCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetAdminLevel
 * Signature: (ILcom/supremainc/sfm_sdk/UF_ADMIN_LEVEL;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetAdminLevel
        (JNIEnv *env, jobject obj, jint _userID, jobject _adminLevel) {
    g_obj = obj;
    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_ADMIN_LEVEL");
    if (cls == NULL) return NULL;
    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");
    if (mid == NULL) return NULL;

    UINT32 userID = static_cast<UINT32>(_userID);
    UF_ADMIN_LEVEL adminLevel = (UF_ADMIN_LEVEL) env->CallIntMethod(_adminLevel, mid);

    UF_RET_CODE ret = UF_SetAdminLevel(userID, adminLevel);

    env->DeleteLocalRef(cls);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetAdminLevel
 * Signature: (I[Lcom/supremainc/sfm_sdk/UF_ADMIN_LEVEL;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetAdminLevel
        (JNIEnv *env, jobject obj, jint _userID, jobjectArray _adminLevel) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    UF_ADMIN_LEVEL adminLevel;

    UF_RET_CODE ret = UF_GetAdminLevel(userID, &adminLevel);

    if (ret == UF_RET_SUCCESS) {
        env->SetObjectArrayElement(_adminLevel, 0, jobjUF_ADMIN_LEVEL(env, obj, adminLevel));
    }

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetSecurityLevel
 * Signature: (ILcom/supremainc/sfm_sdk/UF_USER_SECURITY_LEVEL;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetSecurityLevel
        (JNIEnv *env, jobject obj, jint _userID, jobject _securityLevel) {
    g_obj = obj;
    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_USER_SECURITY_LEVEL");
    if (cls == NULL) return NULL;
    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");
    if (mid == NULL) return NULL;

    UINT32 userID = (UINT32) _userID;
    UF_USER_SECURITY_LEVEL securityLevel = (UF_USER_SECURITY_LEVEL) env->CallIntMethod(
            _securityLevel, mid);

    UF_RET_CODE ret = UF_SetSecurityLevel(userID, securityLevel);

    env->DeleteLocalRef(cls);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetSecurityLevel
 * Signature: (I[Lcom/supremainc/sfm_sdk/UF_USER_SECURITY_LEVEL;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetSecurityLevel
        (JNIEnv *env, jobject obj, jint _userID, jobjectArray _securityLevel) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    UF_USER_SECURITY_LEVEL securityLevel;

    UF_RET_CODE ret = UF_GetSecurityLevel(_userID, &securityLevel);

    if (ret == UF_RET_SUCCESS) {
        env->SetObjectArrayElement(_securityLevel, 0,
                                   jobjUF_USER_SECURITY_LEVEL(env, obj, securityLevel));
    }
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ClearAllAdminLevel
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ClearAllAdminLevel
        (JNIEnv *env, jobject obj) {
    g_obj = obj;

    UF_RET_CODE ret = UF_ClearAllAdminLevel();

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SaveDB
 * Signature: (Ljava/lang/String;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SaveDB
        (JNIEnv *env, jobject obj, jstring _filename) {
    g_obj = obj;
    const char *filename = env->GetStringUTFChars(_filename, 0);

    UF_RET_CODE ret = UF_SaveDB(filename);

    env->ReleaseStringUTFChars(_filename, filename);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_LoadDB
 * Signature: (Ljava/lang/String;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1LoadDB
        (JNIEnv *env, jobject obj, jstring _filename) {
    g_obj = obj;
    const char *filename = env->GetStringUTFChars(_filename, 0);

    UF_RET_CODE ret = UF_LoadDB(filename);

    env->ReleaseStringUTFChars(_filename, filename);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_CheckTemplate
 * Signature: (I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1CheckTemplate
        (JNIEnv *env, jobject obj, jint _userID, jintArray _numOfTemplate) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    UINT32 numOfTemplate = 0;
    UF_RET_CODE ret = UF_CheckTemplate(userID, &numOfTemplate);
    if (ret == UF_RET_SUCCESS) {
        env->SetIntArrayRegion(_numOfTemplate, 0, 1,
                               reinterpret_cast<const jint *>(&numOfTemplate));
    }

    return jobjUF_RET_CODE(env, obj, ret);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReadTemplate
 * Signature: (I[I[B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReadTemplate
        (JNIEnv *env, jobject obj, jint _userID, jintArray _numOfTemplate,
         jbyteArray _templateData) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    UINT32 numOfTemplate = 0;

    jbyte *templateData = env->GetByteArrayElements(_templateData, 0);

    UF_RET_CODE ret = UF_ReadTemplate(userID, &numOfTemplate,
                                      reinterpret_cast<BYTE *>(templateData));

    env->ReleaseByteArrayElements(_templateData, templateData, 0);

    return jobjUF_RET_CODE(env, obj, ret);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReadOneTemplate
 * Signature: (II[B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReadOneTemplate
        (JNIEnv *env, jobject obj, jint _userID, jint _subID, jbyteArray _templateData) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    UINT32 subID = (UINT32) _subID;

    jbyte *templateData = env->GetByteArrayElements(_templateData, 0);

    UF_RET_CODE ret = UF_ReadOneTemplate(userID, subID, reinterpret_cast<BYTE *>(templateData));

    env->ReleaseByteArrayElements(_templateData, templateData, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetScanCallback
 * Signature: (Lcom/supremainc/sfm_sdk/SFM_SDK_ANDROID/ScanCallback;)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetScanCallback
        (JNIEnv *env, jobject obj, jobject _callback) {
    UF_SetScanCallback(ScanCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ScanTemplate
 * Signature: ([B[I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ScanTemplate
        (JNIEnv *env, jobject obj, jbyteArray _templateData, jintArray _templateSize,
         jintArray _imageQuality) {
    g_obj = obj;

    jbyte *templateData = env->GetByteArrayElements(_templateData, 0);
    jint *templateSize = env->GetIntArrayElements(_templateSize, 0);
    jint *imageQuality = env->GetIntArrayElements(_imageQuality, 0);

    UF_RET_CODE ret = UF_ScanTemplate(reinterpret_cast<BYTE *>(templateData),
                                      reinterpret_cast<UINT32 *>(templateSize),
                                      reinterpret_cast<UINT32 *>(imageQuality));

    env->ReleaseByteArrayElements(_templateData, templateData, 0);
    env->ReleaseIntArrayElements(_templateSize, templateSize, 0);
    env->ReleaseIntArrayElements(_imageQuality, imageQuality, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_FixProvisionalTemplate
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1FixProvisionalTemplate
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_FixProvisionalTemplate();
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetAuthType
 * Signature: (ILcom/supremainc/sfm_sdk/UF_AUTH_TYPE;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetAuthType
        (JNIEnv *env, jobject obj, jint _userID, jobject _authType) {
    g_obj = obj;

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_AUTH_TYPE");
    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");

    UINT32 userID = (UINT32) _userID;
    UF_AUTH_TYPE authType = (UF_AUTH_TYPE) (env->CallIntMethod(_authType, mid));

    UF_RET_CODE ret = UF_SetAuthType(userID, authType);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetAuthType
 * Signature: (I[Lcom/supremainc/sfm_sdk/UF_AUTH_TYPE;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetAuthType
        (JNIEnv *env, jobject obj, jint _userID, jobjectArray _authType) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    UF_AUTH_TYPE authType;

    UF_RET_CODE ret = UF_GetAuthType(userID, &authType);

    if (ret == UF_RET_SUCCESS) {
        env->SetObjectArrayElement(_authType, 0, jobjUF_AUTH_TYPE(env, obj, authType));
    }

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetUserIDByAuthType
 * Signature: (Lcom/supremainc/sfm_sdk/UF_AUTH_TYPE;[I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetUserIDByAuthType
        (JNIEnv *env, jobject obj, jobject _authType, jintArray _numOfID, jintArray _userID) {
    g_obj = obj;

    jclass cls = env->FindClass("com/supremainc/sfm_sdk/enumeration/UF_AUTH_TYPE");
    jmethodID mid = env->GetMethodID(cls, "getValue", "()I");

    UF_AUTH_TYPE authType = (UF_AUTH_TYPE) env->CallIntMethod(_authType, mid);
    jint *numOfID = env->GetIntArrayElements(_numOfID, 0);
    jint *userID = env->GetIntArrayElements(_userID, 0);
    UF_RET_CODE ret = UF_GetUserIDByAuthType(authType, numOfID, reinterpret_cast<UINT32 *>(userID));

    env->ReleaseIntArrayElements(_numOfID, numOfID, 0);
    env->ReleaseIntArrayElements(_userID, userID, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ResetAllAuthType
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ResetAllAuthType
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_ResetAllAuthType();

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_AddBlacklist
 * Signature: (I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1AddBlacklist
        (JNIEnv *env, jobject obj, jint _userID, jintArray _numOfBlacklistedID) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    jint *numOfBlacklistedID = env->GetIntArrayElements(_numOfBlacklistedID, 0);

    UF_RET_CODE ret = UF_AddBlacklist(userID, numOfBlacklistedID);

    env->ReleaseIntArrayElements(_numOfBlacklistedID, numOfBlacklistedID, 0);

    return jobjUF_RET_CODE(env, obj, ret);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_DeleteBlacklist
 * Signature: (I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1DeleteBlacklist
        (JNIEnv *env, jobject obj, jint _userID, jintArray _numOfBlacklistedID) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    jint *numOfBlacklistedID = env->GetIntArrayElements(_numOfBlacklistedID, 0);
    UF_RET_CODE ret = UF_DeleteBlacklist(userID, numOfBlacklistedID);

    env->ReleaseIntArrayElements(_numOfBlacklistedID, numOfBlacklistedID, 0);

    return jobjUF_RET_CODE(env, obj, ret);

}


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetBlacklist
 * Signature: ([I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetBlacklist
        (JNIEnv *env, jobject obj, jintArray _numOfBlacklistedID, jintArray _userID) {
    g_obj = obj;

    jint *numOfBlacklistedID = env->GetIntArrayElements(_numOfBlacklistedID, 0);
    jint *userID = env->GetIntArrayElements(_userID, 0);

    UF_RET_CODE ret = UF_GetBlacklist(numOfBlacklistedID, reinterpret_cast<UINT32 *>(userID));

    env->ReleaseIntArrayElements(_numOfBlacklistedID, numOfBlacklistedID, 0);
    env->ReleaseIntArrayElements(_userID, userID, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_DeleteAllBlacklist
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1DeleteAllBlacklist
        (JNIEnv *env, jobject obj) {
    g_obj = obj;

    UF_RET_CODE ret = UF_ResetAllAuthType();
    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetEntranceLimit
 * Signature: (II)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetEntranceLimit
        (JNIEnv *env, jobject obj, jint _userID, jint _entranceLimit) {
    g_obj = obj;

    UINT32 userID = _userID;
    int entranceLimit = (int) _entranceLimit;

    UF_RET_CODE ret = UF_SetEntranceLimit(userID, entranceLimit);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_GetEntranceLimit
 * Signature: (I[I[I)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1GetEntranceLimit
        (JNIEnv *env, jobject obj, jint _userID, jintArray _entranceLimit,
         jintArray _entranceCount) {
    g_obj = obj;

    UINT32 userID = (UINT32) _userID;
    jint *entranceLimit = env->GetIntArrayElements(_entranceLimit, 0);
    jint *entranceCount = env->GetIntArrayElements(_entranceCount, 0);

    UF_RET_CODE ret = UF_GetEntranceLimit(userID, entranceLimit, entranceCount);

    env->ReleaseIntArrayElements(_entranceLimit, entranceLimit, 0);
    env->ReleaseIntArrayElements(_entranceCount, entranceCount, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ClearAllEntranceLimit
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ClearAllEntranceLimit
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    UF_RET_CODE ret = UF_ClearAllEntranceLimit();
    return jobjUF_RET_CODE(env, obj, ret);
}

/**
 * Image
 */

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SaveImage
 * Signature: (Ljava/lang/String;[Lcom/supremainc/sfm_sdk/structure/UFImage;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SaveImage
        (JNIEnv *env, jobject obj, jstring _filename, jobjectArray _image) {

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_LoadImage
 * Signature: (Ljava/lang/String;[Lcom/supremainc/sfm_sdk/structure/UFImage;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1LoadImage
        (JNIEnv *env, jobject obj, jstring _filename, jobjectArray _image) {

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ReadImage
 * Signature: ([Lcom/supremainc/sfm_sdk/structure/UFImage;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ReadImage
        (JNIEnv *env, jobject obj, jobjectArray _image) {
    g_obj = obj;

    UFImage *image = (UFImage *) malloc(UF_IMAGE_HEADER_SIZE * sizeof(int) + UF_MAX_IMAGE_SIZE);

    UF_RET_CODE ret = UF_ReadImage(image);

    if (ret == UF_RET_SUCCESS) {
        jobject imageObj = env->GetObjectArrayElement(_image, 0);
        jclass cls_imageObj = env->GetObjectClass(imageObj);
        jfieldID width = env->GetFieldID(cls_imageObj, "_width", "I");
        jfieldID height = env->GetFieldID(cls_imageObj, "_height", "I");
        jfieldID compressed = env->GetFieldID(cls_imageObj, "_compressed", "I");
        jfieldID encrypted = env->GetFieldID(cls_imageObj, "_encrypted", "I");
        jfieldID format = env->GetFieldID(cls_imageObj, "_format", "I");
        jfieldID templateLen = env->GetFieldID(cls_imageObj, "_templateLen", "I");
        jfieldID buffer = env->GetFieldID(cls_imageObj, "_buffer", "[B");

        env->SetIntField(imageObj, width, image->width);
        env->SetIntField(imageObj, height, image->height);
        env->SetIntField(imageObj, compressed, image->compressed);
        env->SetIntField(imageObj, encrypted, image->encrypted);
        env->SetIntField(imageObj, format, image->format);
        env->SetIntField(imageObj, templateLen, image->templateLen);

        jobject imageBuffer = env->GetObjectField(imageObj, buffer);

        env->SetByteArrayRegion(static_cast<jbyteArray>(imageBuffer), 0, image->templateLen,
                                reinterpret_cast<const jbyte *>(image->buffer));

        env->DeleteLocalRef(cls_imageObj);
    }

    free(image);
    return jobjUF_RET_CODE(env, obj, ret);

}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_ScanImage
 * Signature: ([Lcom/supremainc/sfm_sdk/structure/UFImage;)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1ScanImage
        (JNIEnv *env, jobject obj, jobjectArray _image) {
    g_obj = obj;

    UFImage *image = (UFImage *) malloc(UF_IMAGE_HEADER_SIZE * sizeof(int) + UF_MAX_IMAGE_SIZE);

    UF_RET_CODE ret = UF_ScanImage(image);

    if (ret == UF_RET_SUCCESS) {
        jobject imageObj = env->GetObjectArrayElement(_image, 0);
        jclass cls_imageObj = env->GetObjectClass(imageObj);
        jfieldID width = env->GetFieldID(cls_imageObj, "_width", "I");
        jfieldID height = env->GetFieldID(cls_imageObj, "_height", "I");
        jfieldID compressed = env->GetFieldID(cls_imageObj, "_compressed", "I");
        jfieldID encrypted = env->GetFieldID(cls_imageObj, "_encrypted", "I");
        jfieldID format = env->GetFieldID(cls_imageObj, "_format", "I");
        jfieldID templateLen = env->GetFieldID(cls_imageObj, "_templateLen", "I");
        jfieldID buffer = env->GetFieldID(cls_imageObj, "_buffer", "[B");

        env->SetIntField(imageObj, width, image->width);
        env->SetIntField(imageObj, height, image->height);
        env->SetIntField(imageObj, compressed, image->compressed);
        env->SetIntField(imageObj, encrypted, image->encrypted);
        env->SetIntField(imageObj, format, image->format);
        env->SetIntField(imageObj, templateLen, image->templateLen);

        jobject imageBuffer = env->GetObjectField(imageObj, buffer);

        env->SetByteArrayRegion(static_cast<jbyteArray>(imageBuffer), 0, image->templateLen,
                                reinterpret_cast<const jbyte *>(image->buffer));

        env->DeleteLocalRef(cls_imageObj);
    }

    free(image);
    return jobjUF_RET_CODE(env, obj, ret);
}


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_DeleteAll
 * Signature: ()Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1DeleteAll
        (JNIEnv *env, jobject obj) {
    g_obj = obj;

    UF_RET_CODE ret = UF_DeleteAll();

    return jobjUF_RET_CODE(env, obj, ret);
}

/**
 * Identify
 */

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_SetIdentifyCallback
 * Signature: (Lcom/supremainc/sfm_sdk/SFM_SDK_ANDROID/IdentifyCallback;)V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1SetIdentifyCallback
        (JNIEnv *env, jobject obj, jobject _callback) {
    UF_SetIdentifyCallback(IdentifyCallback);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_Identify
 * Signature: ([I[B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1Identify
        (JNIEnv *env, jobject obj, jintArray _userID, jbyteArray _subID) {
    g_obj = obj;

    jint *userID = env->GetIntArrayElements(_userID, 0);
    jbyte *subID = env->GetByteArrayElements(_subID, 0);

    UF_RET_CODE ret = UF_Identify(reinterpret_cast<UINT32 *>(userID),
                                  reinterpret_cast<BYTE *>(subID));

    env->ReleaseIntArrayElements(_userID, userID, 0);
    env->ReleaseByteArrayElements(_subID, subID, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_IdentifyTemplate
 * Signature: (I[B[I[B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1IdentifyTemplate
        (JNIEnv *env, jobject obj, jint _templateSize, jbyteArray _templateData, jintArray _userID,
         jbyteArray _subID) {
    g_obj = obj;

    UINT32 templateSize = (UINT32) _templateSize;
    jbyte *templateData = env->GetByteArrayElements(_templateData, 0);
    jint *userID = env->GetIntArrayElements(_userID, 0);
    jbyte *subID = env->GetByteArrayElements(_subID, 0);

    UF_RET_CODE ret = UF_IdentifyTemplate(templateSize, reinterpret_cast<BYTE *>(templateData),
                                          reinterpret_cast<UINT32 *>(userID),
                                          reinterpret_cast<BYTE *>(subID));

    env->ReleaseByteArrayElements(_templateData, templateData, 0);
    env->ReleaseIntArrayElements(_userID, userID, 0);
    env->ReleaseByteArrayElements(_subID, subID, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}

/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    UF_IdentifyImage
 * Signature: (I[B[I[B)Lcom/supremainc/sfm_sdk/enumeration/UF_RET_CODE;
 */
JNIEXPORT jobject JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_UF_1IdentifyImage
        (JNIEnv *env, jobject obj, jint _imageSize, jbyteArray _imageData, jintArray _userID,
         jbyteArray _subID) {
    g_obj = obj;

    UINT32 imageSize = (UINT32) _imageSize;
    jbyte *imageData = env->GetByteArrayElements(_imageData, 0);
    jint *userID = env->GetIntArrayElements(_userID, 0);
    jbyte *subID = env->GetByteArrayElements(_subID, 0);

    UF_RET_CODE ret = UF_IdentifyImage(imageSize, reinterpret_cast<BYTE *>(imageData),
                                       reinterpret_cast<UINT32 *>(userID),
                                       reinterpret_cast<BYTE *>(subID));

    env->ReleaseByteArrayElements(_imageData, imageData, 0);
    env->ReleaseIntArrayElements(_userID, userID, 0);
    env->ReleaseByteArrayElements(_subID, subID, 0);

    return jobjUF_RET_CODE(env, obj, ret);
}


/*
 * Class:     com_supremainc_sfm_sdk_SFM_SDK_ANDROID
 * Method:    NDKCallback_Test
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_supremainc_sfm_1sdk_SFM_1SDK_1ANDROID_NDKCallback_1Test
        (JNIEnv *env, jobject obj) {
    g_obj = obj;
    jobjUF_AUTH_TYPE(env, obj, UF_AUTH_FINGERPRINT);
    jobjUF_RET_CODE(env, obj, UF_RET_SUCCESS);
    ReadSerialCallback((unsigned char *) "test", 4, 1);
    LOGI("Test done");
}


#ifdef __cplusplus
}
#endif
