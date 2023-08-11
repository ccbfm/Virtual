package com.virtual.util.common;

import android.annotation.SuppressLint;
import android.os.RemoteException;
import android.util.Log;

public class StartService {

    private android.os.IBinder mRemote;
    private android.os.Parcel mServiceData;
    private static final int transactCode;

    static {
        switch (android.os.Build.VERSION.SDK_INT) {
            case 26:
            case 27:
                transactCode = 26;
                break;
            case 28:
                transactCode = 30;
                break;
            case 29:
                transactCode = 24;
                break;
            default:
                transactCode = 34;
                break;
        }
    }


    @SuppressLint("PrivateApi")
    public StartService initAmsBinder() {
        Class<?> activityManagerNative;
        try {
            activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Object amn = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
            if (amn == null) {
                return null;
            }
            java.lang.reflect.Field mRemoteField = amn.getClass().getDeclaredField("mRemote");
            mRemoteField.setAccessible(true);
            mRemote = (android.os.IBinder) mRemoteField.get(amn);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return this;
    }

    public StartService initServiceParcel(android.content.Context context, String packageName, String serviceName) {
        android.content.Intent intent = new android.content.Intent();
        android.content.ComponentName component = new android.content.ComponentName(packageName, serviceName);
        intent.setComponent(component);

        android.os.Parcel parcel = android.os.Parcel.obtain();
        intent.writeToParcel(parcel, 0);

        mServiceData = android.os.Parcel.obtain();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            /* Android 8.1 frameworks/base/core/java/android/app/IActivityManager.aidl
             * ComponentName startService(in IApplicationThread caller, in Intent service,
             *    in String resolvedType, boolean requireForeground, in String callingPackage, int userId);
             *
             * frameworks/base/services/core/java/com/android/server/am/ActiveServices.java
             * if (fgRequired) {
             *     final int mode = mAm.mAppOpsService.checkOperation(
             *             AppOpsManager.OP_START_FOREGROUND, r.appInfo.uid, r.packageName);
             *     switch (mode) {
             *         case AppOpsManager.MODE_ALLOWED:
             *         case AppOpsManager.MODE_DEFAULT: // All okay.
             *             break;
             *         case AppOpsManager.MODE_IGNORED:
             *             // Not allowed, fall back to normal start service, failing siliently if background check restricts that.
             *             fgRequired = false;
             *             forceSilentAbort = true;
             *             break;
             *         default:
             *             return new ComponentName("!!", "foreground not allowed as per app op");
             *     }
             * }
             * requireForeground 要求启动service之后，调用service.startForeground()显示一个通知，不然会崩溃
             */
            mServiceData.writeInterfaceToken("android.app.IActivityManager");
            mServiceData.writeStrongBinder(null);
            mServiceData.writeInt(1);
            intent.writeToParcel(mServiceData, 0);
            mServiceData.writeString(null); // resolvedType
            //mServiceData.writeInt(context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.O ? 1 : 0);
            mServiceData.writeInt(0);//requireForeground
            mServiceData.writeString(context.getPackageName()); // callingPackage
            mServiceData.writeInt(0); // userId
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // http://aospxref.com/android-7.1.2_r36/xref/frameworks/base/core/java/android/app/ActivityManagerNative.java
            /* ActivityManagerNative#START_SERVICE_TRANSACTION
             *  case START_SERVICE_TRANSACTION: {
             *             data.enforceInterface(IActivityManager.descriptor);
             *             IBinder b = data.readStrongBinder();
             *             IApplicationThread app = ApplicationThreadNative.asInterface(b);
             *             Intent service = Intent.CREATOR.createFromParcel(data);
             *             String resolvedType = data.readString();
             *             String callingPackage = data.readString();
             *             int userId = data.readInt();
             *             ComponentName cn = startService(app, service, resolvedType, callingPackage, userId);
             *             reply.writeNoException();
             *             ComponentName.writeToParcel(cn, reply);
             *             return true;
             *         }
             */
            mServiceData.writeInterfaceToken("android.app.IActivityManager");
            mServiceData.writeStrongBinder(null);
            intent.writeToParcel(mServiceData, 0);
            mServiceData.writeString(null);  // resolvedType
            mServiceData.writeString(context.getPackageName()); // callingPackage
            mServiceData.writeInt(0); // userId
        } else {
            /* Android4.4 ActivityManagerNative#START_SERVICE_TRANSACTION
             * case START_SERVICE_TRANSACTION: {
             *             data.enforceInterface(IActivityManager.descriptor);
             *             IBinder b = data.readStrongBinder();
             *             IApplicationThread app = ApplicationThreadNative.asInterface(b);
             *             Intent service = Intent.CREATOR.createFromParcel(data);
             *             String resolvedType = data.readString();
             *             int userId = data.readInt();
             *             ComponentName cn = startService(app, service, resolvedType, userId);
             *             reply.writeNoException();
             *             ComponentName.writeToParcel(cn, reply);
             *             return true;
             *         }
             */
            mServiceData.writeInterfaceToken("android.app.IActivityManager");
            mServiceData.writeStrongBinder(null);
            intent.writeToParcel(mServiceData, 0);
            mServiceData.writeString(null);  // resolvedType
            mServiceData.writeInt(0); // userId
        }
        return this;
    }

    public boolean startServiceByAmsBinder() {
        try {
            if (mRemote == null || mServiceData == null) {
                Log.e("Daemon", "REMOTE IS NULL or PARCEL IS NULL !!!");
                return false;
            }
            mRemote.transact(transactCode, mServiceData, null, 1); // flag=FLAG_ONEWAY=1
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
