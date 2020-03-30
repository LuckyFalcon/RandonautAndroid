//
// Created by David on 29-3-2020.
//

#include <jni.h>
#include "SteveLib/TemporalLib.h"

static BookHitter *steve;

JNIEXPORT jstring JNICALL
Java_com_randonautica_app_MyRandonautFragment_steveString(JNIEnv *env, jobject instance) {
    steve = bh_create();
    bh_config(steve)->Channel = 1;
    unsigned char* buffer = new unsigned char[1500];

    bh_hitbooks(steve, buffer, 123);

    return reinterpret_cast<jstring>(123);

}
