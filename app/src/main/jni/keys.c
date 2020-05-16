#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_randonautica_app_RandonautFragment_getBaseApi(JNIEnv *env, jobject instance) {


    //https://Api2
    return (*env)->NewStringUTF(env, "aHR0cHM6Ly9yYW5kb25hdXRpY2EuYXp1cmUtYXBpLm5ldC8=");


}