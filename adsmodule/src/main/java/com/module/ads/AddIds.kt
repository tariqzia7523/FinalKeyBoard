package com.module.ads

object AddIds {
    // orignal id
    // test id
    val bannerID: String
        get() {
            var id = ""
            id = if (BuildConfig.DEBUG) {
                "ca-app-pub-3940256099942544/6300978111" // test id
            } else {
                "ca-app-pub-8046980262605578/8088415068" // orignal id
            }
            return id
        }// orignal id

    // test id
    val interstialId: String
        get() {
            var id = ""
            id = if (BuildConfig.DEBUG) {
                "ca-app-pub-3940256099942544/1033173712" // test id
            } else {
                "ca-app-pub-8046980262605578/4149170052" // orignal id
            }
            return id
        }// orignal id

    // test id
    val nativeId: String
        get() {
            var id = ""
            id = if (BuildConfig.DEBUG) {
                "ca-app-pub-3940256099942544/2247696110" // test id
            } else {
                "ca-app-pub-8046980262605578/9209925042" // orignal id
            }
            return id
        }// orignal id

    // test id
    @JvmStatic
    val openId: String
        get() {
            var id = ""
            id = if (BuildConfig.DEBUG) {
                "ca-app-pub-3940256099942544/3419835294" // test id
            } else {
                "ca-app-pub-8046980262605578/5270680030" // orignal id
            }
            return id
        }
}