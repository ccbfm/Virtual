package com.virtual.util.context;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class VProcess {

    public static boolean isMain() {
        return isMain(VContextHolder.instance().getContext());
    }

    public static boolean isMain(Context context) {
        String processName = getProcessName();
        return !TextUtils.isEmpty(processName) && processName.equals(context.getPackageName());
    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
