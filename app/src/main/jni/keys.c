#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_randonautica_app_RandonautFragment_getBaseApi(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "aHR0cHM6Ly9hcGkyLnJhbmRvbmF1dHMuY29tLw==");

}