#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_virtual_util_native_1shell_NativeShell_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_virtual_util_native_1shell_NativeShell_imei(JNIEnv *env, jobject thiz) {
    // TODO: implement imei()
    try {
        system(R"(service call iphonesubinfo 1 | grep -o '[0-9a-f]\{8\} ' | tail -n+3 | while read a; do echo -n \\u${a:4:4}\\u${a:0:4}; done)");
    } catch (std::exception& e) {

    }
    return env->NewStringUTF("");
}