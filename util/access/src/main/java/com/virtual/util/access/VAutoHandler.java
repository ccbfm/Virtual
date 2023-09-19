package com.virtual.util.access;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class VAutoHandler extends Handler {
    private static final String TAG = "VAutoHandler";

    public VAutoHandler(@NonNull Looper looper) {
        super(looper);
    }

    protected boolean isExeApp() {
        return checkApp(VAccessManager.instance().getPackageName());
    }

    protected Context context() {
        return VAccessManager.instance().context();
    }

    protected boolean mIsRunning = false;

    public void start() {
        mIsRunning = true;
    }

    public void stop() {
        mIsRunning = false;
        removeCallbacksAndMessages();
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    protected int mAHWhat = AHWhat.NONE, mNextAHWhat = AHWhat.NONE;
    protected VMessageData mWaitExeMessage;

    protected boolean hasWaitExeMessage() {
        return mWaitExeMessage != null;
    }

    public void setAHWhat(int AHWhat) {
        mAHWhat = AHWhat;
    }

    public void setNextAHWhat(int nextAHWhat) {
        mNextAHWhat = nextAHWhat;
    }

    public int getAHWhat() {
        return mAHWhat;
    }

    public int getNextAHWhat() {
        return mNextAHWhat;
    }

    private LinkedBlockingQueue<String> mExeDataQueue;

    public void offerString(String str) {
        if (mExeDataQueue == null) {
            mExeDataQueue = new LinkedBlockingQueue<>();
        }
        mExeDataQueue.offer(str);
        if (getNextAHWhat() == AHWhat.IDLE) {
            sendCheckEmptyMessageDelayed(AHWhat.IDLE, toIdleDelayTime());
        }
    }

    public String pollString() {
        return mExeDataQueue == null ? "" : mExeDataQueue.poll();
    }

    public int stringDataSize() {
        return mExeDataQueue == null ? 0 : mExeDataQueue.size();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        try {
            Context context = context();
            if (context == null) {
                removeCallbacksAndMessages();
                return;
            }
            if (!isExeApp()) {
                Message message = Message.obtain(msg);
                Log.i(TAG, "handleMessage not exe message " + message.what + " "
                        + message.arg1 + " " + message.arg2 + " " + message.obj);
                sendCheckMessageDelayed(message, 0L);
                return;
            }
            int what = msg.what;
            setAHWhat(what);
            operateMessage(context, what, msg.arg1, msg.arg2, msg.obj);
        } catch (Throwable throwable) {
            Log.e(TAG, "handleMessage Throwable", throwable);
        }
    }

    public void checkHandleEvent(CharSequence packageName,
                                 CharSequence className,
                                 String viewIdStr,
                                 AccessibilityNodeInfo nodeInfo) {
        try {
            if (isExeApp()) {
                if (!isRunning()) {
                    return;
                }
                if ((getNextAHWhat() == AHWhat.NONE)) {
                    sendCheckEmptyMessageDelayed(AHWhat.IDLE, toIdleDelayTime());
                    return;
                } else if (hasWaitExeMessage()) {
                    Message message = Message.obtain(mWaitExeMessage.message);
                    Log.i(TAG, "checkHandle message " + message.what + " " +
                            message.arg1 + " " + message.arg2 + " " + message.obj);
                    sendCheckMessageDelayed(message, mWaitExeMessage.delayMillis);
                    return;
                }
                handleEvent(packageName, className, viewIdStr, nodeInfo);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "checkHandleEvent Throwable", throwable);
        }
    }

    protected boolean hasNextWhat(int nextWhat) {
        return getNextAHWhat() == nextWhat;
    }

    protected boolean hasNextWhatAndNoHandler(int nextWhat) {
        return getNextAHWhat() == nextWhat && !hasMessages(nextWhat);
    }

    protected void sendCheckEmptyMessageDelayed(int what, long delayMillis) {
        sendCheckEmptyMessageDelayed(what, delayMillis, true);
    }

    protected void sendCheckEmptyMessageDelayed(int what, long delayMillis, boolean setNext) {
        if (isRunning()) {
            Message message = Message.obtain();
            message.what = what;
            sendCheckMessageDelayed(message, delayMillis, setNext);
        }
    }

    protected void sendCheckRemoveMessageDelayed(Message message, long delayMillis) {
        removeMessages(message.what);
        sendCheckMessageDelayed(message, delayMillis);
    }

    protected void sendCheckMessageDelayed(Message message, long delayMillis) {
        sendCheckMessageDelayed(message, delayMillis, true);
    }

    protected void sendCheckMessageDelayed(Message message, long delayMillis, boolean setNext) {
        if (isRunning()) {
            int what = message.what;
            if (what != AHWhat.IDLE && hasMessages(what)) {
                return;
            }
            if (setNext) {
                setNextAHWhat(what);
            }

            removeMessages(AHWhat.IDLE);
            if (isExeApp()) {
                mWaitExeMessage = null;
                sendMessageDelayed(message, delayMillis);
                if (what != AHWhat.IDLE) {
                    sendEmptyMessageDelayed(AHWhat.IDLE, (delayMillis + noOpsDelayTime()));
                }
            } else {
                Log.i(TAG, "waitExeApp message " + message.what + " " +
                        message.arg1 + " " + message.arg2 + " " + message.obj);
                mWaitExeMessage = new VMessageData(message, delayMillis);
            }
        }
    }

    protected long toIdleDelayTime() {
        return 2222L;
    }

    protected long noOpsDelayTime() {
        return 20000L;
    }

    protected void removeCallbacksAndMessages() {
        setAHWhat(AHWhat.NONE);
        setNextAHWhat(AHWhat.NONE);
        removeCallbacksAndMessages(null);
    }

    protected abstract boolean checkApp(String currentPackageName);

    protected abstract void handleEvent(CharSequence packageName,
                                        CharSequence className,
                                        String viewIdStr,
                                        AccessibilityNodeInfo nodeInfo) throws Throwable;

    protected abstract void operateMessage(Context context, int what, int arg1, int arg2, Object obj) throws Throwable;

    protected void toast(String msg) {
        VAccessManager.instance().toast(msg);
    }

    protected void back() {
        VAccessManager.instance().back();
    }

    protected void clickByNode(AccessibilityNodeInfo nodeInfo) {
        VAccessManager.instance().clickByNode(nodeInfo);
    }

    protected void scrollVerticalByNode(AccessibilityNodeInfo nodeInfo, boolean up) {
        VAccessManager.instance().scrollVerticalByNode(nodeInfo, up);
    }

    protected interface AHWhat {
        int NONE = 0;
        int IDLE = NONE + 1;
    }
}
