#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_randonautica_app_MyRandonautFragment_getApiKey(JNIEnv *env, jobject instance) {

    return (*env)->  NewStringUTF(env, "cGsuZXlKMUlqb2laR0YyYVdSbVlXeGpiMjRpTENKaElqb2lZMnMxZGpSM01uaDJNSE51T1ROa2JEZG9iMjloWjNWeGFTSjkucWZWeUxxaVEzVGlxR3R2ZEVCWnAwZw==");
}

JNIEXPORT jstring JNICALL
Java_com_randonautica_app_MyRandonautFragment_getBaseApi(JNIEnv *env, jobject instance) {

    // https://api.randonauts.com
   // return (*env)->NewStringUTF(env, "aHR0cHM6Ly9hcGkucmFuZG9uYXV0cy5jb20v");
    // https://devapi.randonauts.com
    //return (*env)->NewStringUTF(env, "aHR0cHM6Ly9kZXZhcGkucmFuZG9uYXV0cy5jb20v");
    // https://192.168.1.117/
    //return (*env)->NewStringUTF(env, "aHR0cDovLzE5Mi4xNjguMS4xMTc6MzAwMC8=");
    //https://Api2
    return (*env)->NewStringUTF(env, "aHR0cHM6Ly9hcGkyLnJhbmRvbmF1dHMuY29tLw==");


}