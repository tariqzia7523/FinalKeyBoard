#define LOG_TAG "LatinIME: jni: Session"

#include "io_github_otobikb_inputmethod_latin_DicTraverseSession.h"

#include "defines.h"
#include "dictionary/property/ngram_context.h"
#include "jni.h"
#include "jni_common.h"
#include "suggest/core/session/dic_traverse_session.h"

namespace latinime {
class Dictionary;
static jlong latinime_setDicTraverseSession(JNIEnv *env, jclass clazz, jstring localeJStr,
        jlong dictSize) {
    void *traverseSession = DicTraverseSession::getSessionInstance(env, localeJStr, dictSize);
    return reinterpret_cast<jlong>(traverseSession);
}

static void latinime_initDicTraverseSession(JNIEnv *env, jclass clazz, jlong traverseSession,
        jlong dictionary, jintArray previousWord, jint previousWordLength) {
    DicTraverseSession *ts = reinterpret_cast<DicTraverseSession *>(traverseSession);
    if (!ts) {
        return;
    }
    Dictionary *dict = reinterpret_cast<Dictionary *>(dictionary);
    if (!previousWord) {
        NgramContext emptyNgramContext;
        ts->init(dict, &emptyNgramContext, 0 /* suggestOptions */);
        return;
    }
    int prevWord[previousWordLength];
    env->GetIntArrayRegion(previousWord, 0, previousWordLength, prevWord);
    NgramContext ngramContext(prevWord, previousWordLength, false /* isStartOfSentence */);
    ts->init(dict, &ngramContext, 0 /* suggestOptions */);
}

static void latinime_releaseDicTraverseSession(JNIEnv *env, jclass clazz, jlong traverseSession) {
    DicTraverseSession *ts = reinterpret_cast<DicTraverseSession *>(traverseSession);
    DicTraverseSession::releaseSessionInstance(ts);
}

static const JNINativeMethod sMethods[] = {
    {
        const_cast<char *>("setDicTraverseSessionNative"),
        const_cast<char *>("(Ljava/lang/String;J)J"),
        reinterpret_cast<void *>(latinime_setDicTraverseSession)
    },
    {
        const_cast<char *>("initDicTraverseSessionNative"),
        const_cast<char *>("(JJ[II)V"),
        reinterpret_cast<void *>(latinime_initDicTraverseSession)
    },
    {
        const_cast<char *>("releaseDicTraverseSessionNative"),
        const_cast<char *>("(J)V"),
        reinterpret_cast<void *>(latinime_releaseDicTraverseSession)
    }
};

int register_DicTraverseSession(JNIEnv *env) {
    const char *const kClassPathName = "com/german/keyboard/app/free/latin/DicTraverseSession";
    return registerNativeMethods(env, kClassPathName, sMethods, NELEMS(sMethods));
}
} // namespace latinime
