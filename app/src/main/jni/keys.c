#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_randonautica_app_MyRandonautFragment_getApiKey(JNIEnv *env, jobject instance) {

    return (*env)->  NewStringUTF(env, "cGsuZXlKMUlqb2laR0YyYVdSbVlXeGpiMjRpTENKaElqb2lZMnMxZGpSM01uaDJNSE51T1ROa2JEZG9iMjloWjNWeGFTSjkucWZWeUxxaVEzVGlxR3R2ZEVCWnAwZw==");
}

JNIEXPORT jstring JNICALL
Java_com_randonautica_app_MyRandonautFragment_getBaseApi(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "aHR0cHM6Ly9hcGkucmFuZG9uYXV0cy5jb20v");
}