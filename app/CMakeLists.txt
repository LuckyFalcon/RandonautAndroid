cmake_minimum_required(VERSION 3.6.0)
project( steve-jni )

set( SOURCE_FILES
        Steve.cpp )

add_library(Steve SHARED
        src/main/cpp/Steve.cpp)

include_directories( ${CMAKE_SOURCE_DIR}/src/main/cpp/SteveLib )

add_library(Temporal
        STATIC
        IMPORTED)

set_target_properties(Temporal
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libTemporal.a)

include_directories(${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/)

target_link_libraries(Steve
        Temporal)