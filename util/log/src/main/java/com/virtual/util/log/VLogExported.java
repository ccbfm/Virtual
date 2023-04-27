package com.virtual.util.log;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.DeadObjectException;
import android.util.Log;

import com.virtual.util.context.VContextHolder;
import com.virtual.util.log.provider.VLogProvider;
import com.virtual.util.thread.VThread;
import com.virtual.util.thread.model.VSimpleTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 跨应用输出log记录在本应用目录中
 */

public final class VLogExported {

    public static void init(String packageName) {
        init(providerUri(packageName));
    }

    public static void init(Uri uri) {
        Manager.instance().init(uri);
    }

    public static void d(String tag, String msg) {
        Manager.instance().offer(new LogData(VLevel.D, tag, msg));
    }

    public static void i(String tag, String msg) {
        Manager.instance().offer(new LogData(VLevel.I, tag, msg));
    }

    public static void w(String tag, String msg) {
        Manager.instance().offer(new LogData(VLevel.W, tag, msg));
    }

    public static void e(String tag, String msg) {
        Manager.instance().offer(new LogData(VLevel.E, tag, msg));
    }

    public static void e(String tag, String msg, Throwable tr) {
        Manager.instance().offer(new LogData(VLevel.E, tag, msg + " Throwable: " + Log.getStackTraceString(tr)));
    }


    public static Uri providerUri(String packageName) {
        String authority = packageName + ".provider.VLogProvider";
        return Uri.parse("content://" + authority + "/" + VLogProvider.Config.PATH_V_LOG);
    }

    private static class LogData {
        @VLevel
        private final int level;
        private final String tag;
        private final String msg;

        public LogData(@VLevel int level, String tag, String msg) {
            this.level = level;
            this.tag = tag;
            this.msg = msg;
        }
    }

    private static class Manager {
        private Manager() {
        }

        private static final class Singleton {
            private static final Manager INSTANCE = new Manager();
        }

        public static Manager instance() {
            return Manager.Singleton.INSTANCE;
        }

        private final LinkedBlockingQueue<LogData> mLogDataQueue = new LinkedBlockingQueue<>();
        private LogTask mLogTask;
        private Uri mProviderUri;

        public void init(Uri uri) {
            mProviderUri = uri;
        }

        private Uri checkProvider(Context context) {
            if (mProviderUri == null) {
                mProviderUri = providerUri(context.getPackageName());
            }
            return mProviderUri;
        }

        public void offer(LogData logData) {
            mLogDataQueue.offer(logData);
            if (mLogTask == null) {
                mLogTask = new LogTask();
                VThread.executeAtFixRate(VThread.getCachedPool(), mLogTask, 1, TimeUnit.MILLISECONDS);
            } else {
                synchronized (mLogDataQueue) {
                    mLogDataQueue.notify();
                }
            }
        }

        private void exportedProvider(@VLevel int level, String tag, String msg) {
            Context context = VContextHolder.instance().getContext();
            Uri uri = checkProvider(context);
            try (ContentProviderClient client = context.getContentResolver()
                    .acquireUnstableContentProviderClient(uri)) {

                ContentValues values = new ContentValues();
                values.put(VLogProvider.Config.KEY_LEVEL, level);
                values.put(VLogProvider.Config.KEY_TAG, tag);
                values.put(VLogProvider.Config.KEY_MESSAGE, msg);
                client.insert(uri, values);
            } catch (DeadObjectException exception) {
                Log.e("VLogExported", "exportedProvider DeadObjectException.");
            } catch (Throwable throwable) {
                Log.e("VLogExported", "exportedProvider Throwable: " + throwable.getMessage());
            }
        }

        public /*static*/ class LogTask extends VSimpleTask<Void> {
            @Override
            protected Void doTask() throws Throwable {

                synchronized (mLogDataQueue) {
                    if (mLogDataQueue.size() == 0) {
                        mLogDataQueue.wait();
                    }
                }

                LogData logData = mLogDataQueue.poll();
                if (logData != null) {
                    exportedProvider(logData.level, logData.tag, logData.msg);
                }

                return null;
            }

            @Override
            protected void onSuccess(Void result) {

            }
        }
    }

}
