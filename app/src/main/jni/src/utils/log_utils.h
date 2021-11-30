#ifndef LATINIME_LOG_UTILS_H
#define LATINIME_LOG_UTILS_H

#include "defines.h"
#include "jni.h"

namespace latinime {

class LogUtils {
 public:
    static void logToJava(JNIEnv *const env, const char *const format, ...)
#ifdef __GNUC__
        __attribute__ ((format (printf, 2, 3)))
#endif // __GNUC__
        ;

 private:
    DISALLOW_COPY_AND_ASSIGN(LogUtils);
};
} // namespace latinime
#endif // LATINIME_LOG_UTILS_H
