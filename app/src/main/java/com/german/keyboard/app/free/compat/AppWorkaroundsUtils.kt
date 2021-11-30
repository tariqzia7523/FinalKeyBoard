package com.german.keyboard.app.free.inputmethod.compat

import android.content.pm.PackageInfo

class AppWorkaroundsUtils(private val mPackageInfo: PackageInfo?) {
    override fun toString(): String {
        if (mPackageInfo?.applicationInfo == null) {
            return ""
        }
        val s = StringBuilder()
        s.append("Target application : ")
                .append(mPackageInfo.applicationInfo.name)
                .append("\nPackage : ")
                .append(mPackageInfo.applicationInfo.packageName)
                .append("\nTarget app sdk version : ")
                .append(mPackageInfo.applicationInfo.targetSdkVersion)
        return s.toString()
    }

}