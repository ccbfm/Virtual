package com.virtual.util.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import java.io.File;

public class InstallUtils {

    public static boolean setInstallPermission(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先判断是否有安装未知来源应用的权限
            return mContext.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    public static boolean installAppPermission(Activity activity) {
        if (!setInstallPermission(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ToastUtils.makeTextShortShow(activity, "需要打开允许来自此来源,请在设置中开启此权限");
                Uri packageURI = Uri.parse("package:" + activity.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                activity.startActivityForResult(intent, 0x31);
            }
            return false;
        }
        return true;
    }

    public static void installMyApk(Context context, String uri_authority, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, uri_authority, new File(path));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(
                    Uri.fromFile(new File(path)),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
