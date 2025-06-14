#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "NativeObfuscation"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_com_aengussong_evilappshowcase_obfuscation_NativeLibrariesExecutor_noObfuscation(JNIEnv *env, jobject thiz) {
    int n = 50;
    unsigned long long a = 0, b = 1, c;
    for (int i = 2; i <= n; ++i) {
        c = a + b;
        a = b;
        b = c;
    }

    LOGI("No Obfuscation: Fibonacci number at position %d is: %llu", n, b);
}