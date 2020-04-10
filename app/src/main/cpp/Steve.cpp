//
// Created by David on 29-3-2020.
//

#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>

#include "SteveLib/TemporalLib.h"

using namespace std;

typedef unsigned char u8;

extern "C" JNIEXPORT jstring JNICALL
Java_com_randonautica_app_MyRandonautFragment_hitBooks(JNIEnv *env, jobject instance, jint size) {
    static BookHitter *steve;
    if (!steve) {
        steve = bh_create();
        bh_config(steve)->Channel = 1;
    }

    static std::string byte_steve;
    static std::string hex_steve;
    byte_steve.resize(size/2);
    hex_steve.resize(size);

    bh_hitbooks(steve, (u8*)byte_steve.c_str(), size);

    const char* hex_digits  =  "0123456789abcdef";
    int i = 0;
    for (u8 c : byte_steve) {
        hex_steve[i++] = hex_digits[c >> 4];
        hex_steve[i++] = hex_digits[c & 15];
    }

    //__android_log_print(ANDROID_LOG_VERBOSE, "SteveLib", "returning: %s", hex_steve.c_str());

    return env->NewStringUTF(hex_steve.c_str()); // this copies the string. No need to free anything then!
}
