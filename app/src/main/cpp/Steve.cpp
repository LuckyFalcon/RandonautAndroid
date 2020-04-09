//
// Created by David on 29-3-2020.
//

#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>

#include "SteveLib/TemporalLib.h"

using namespace std;

static BookHitter *steve;

extern "C" JNIEXPORT jstring JNICALL
Java_com_randonautica_app_MyRandonautFragment_hitBooks(JNIEnv *env, jobject instance, jint size) {
    unsigned char *buffer = (unsigned char *) malloc(size);
    char *hexOutput = (char *) malloc(size*2);
    char *ret = hexOutput;
    const unsigned char *ptr = buffer;

    steve = bh_create();
    bh_config(steve)->Channel = 1;
    bh_hitbooks(steve, buffer, size);

    for (int i = 0 ; i < size; i++) {
        sprintf(hexOutput, "%02x", *ptr);
        hexOutput++; hexOutput++;
        ptr++;
    }

    free(buffer);

    //__android_log_print(ANDROID_LOG_VERBOSE, "SteveLib", "returning: %s", ret);
    return env->NewStringUTF(ret);
}
