package com.virtual.util.socket.local.server;

import android.net.LocalSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.socket.local.work.VLocalWork;
import com.virtual.util.socket.local.work.VLocalWorkPool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public abstract class VLocalServerConnect extends VLocalWork {
    private final int mUid;
    private final int mPid;
    private LocalSocket mLocalSocket;
    private Looper mAcceptLooper;
    private Looper mSendLooper;

    private Handler mAcceptHandler;
    private Handler mSendHandler;
    private HandlerThread mHandlerThread;
    private PrintWriter mWriter;
    private BufferedReader mReader;

    private String mName;
    private int mUserId = -1;

    public VLocalServerConnect(int uid, int pid, LocalSocket localSocket) {
        mUid = uid;
        mPid = pid;
        mLocalSocket = localSocket;
    }

    public VLocalServerConnect(int uid, int pid, LocalSocket localSocket,
                               Looper acceptLooper, Looper sendLooper) {
        mUid = uid;
        mPid = pid;
        mLocalSocket = localSocket;
        mAcceptLooper = acceptLooper;
        mSendLooper = sendLooper;
    }

    /**
     * android.os.NetworkOnMainThreadException
     *
     * @return Looper
     */
    @NonNull
    protected Looper acceptLooper() {
        if (mAcceptLooper != null) {
            return mAcceptLooper;
        }
        checkHandlerThread();
        return mHandlerThread.getLooper();
    }

    @NonNull
    protected Looper sendLooper() {
        if (mSendLooper != null) {
            return mSendLooper;
        }
        checkHandlerThread();
        return mHandlerThread.getLooper();
    }

    private void checkHandlerThread() {
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("server-connect");
            mHandlerThread.start();
        }
    }

    protected abstract void handleResult(String result);

    @Override
    protected void doWork() throws Throwable {
        final LocalSocket localSocket = mLocalSocket;
        mWriter = new PrintWriter(localSocket.getOutputStream());
        mReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));

        send("connected");
        while (isRunning()) {
            String result = mReader.readLine();
            if (result == null) {
                close();
                break;
            }
            if (result.startsWith("connected")) {
                String info = result.replaceFirst("connected", "");
                Log.d("VLocalServerConnect", "connected info: " + info);
                mName = info;
                mUserId = mUid / 100000;
                if (TextUtils.isEmpty(mName) || mUserId < 0) {
                    throw new NullPointerException("VLocalServerConnect connected is null.{ " + mName + " , " + mUserId + " }");
                }
                recordConnect(mName, mUserId);
            } else {
                if (mAcceptHandler == null) {
                    mAcceptHandler = new Handler(acceptLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            if (msg.what == 1) {
                                String result = (String) msg.obj;
                                handleResult(result);
                            }
                        }
                    };
                }

                Message message = Message.obtain();
                message.what = 1;
                message.obj = result;
                mAcceptHandler.sendMessage(message);
            }
        }
    }

    @Override
    protected void doThrowable() {
        close();
    }

    protected void recordConnect(String name, int userId) {
        VLocalWorkPool.instance().recordConnect(name, userId, this);
    }

    @Override
    public void start() {
        VLocalWorkPool.instance().executor().execute(this);
    }

    public void send(final String text) {
        if (mWriter == null) {
            return;
        }
        if (mSendHandler == null) {
            mSendHandler = new Handler(sendLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 1) {
                        String text = (String) msg.obj;
                        if (mWriter != null) {
                            mWriter.println(text);
                            mWriter.flush();
                        }
                    }
                }
            };
        }
        Message message = Message.obtain();
        message.what = 1;
        message.obj = text;
        mSendHandler.sendMessage(message);
    }

    @Override
    public void close() {
        Log.d("VLocalServerConnect", "close: " + mName + " " + mUid + " " + mPid);
        super.close();
        VLocalWorkPool.instance().removeConnect(mName, mUserId);
        try {
            if (mWriter != null) {
                mWriter.close();
                mWriter = null;
            }
            if (mReader != null) {
                mReader.close();
                mReader = null;
            }
            if (mLocalSocket != null) {
                mLocalSocket.close();
                mLocalSocket = null;
            }
            if (mHandlerThread != null) {
                mHandlerThread.quit();
                mHandlerThread = null;
            }
        } catch (Throwable throwable) {
            Log.e("VLocalServerConnect", "close Throwable: ", throwable);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "VLocalServerConnect{" +
                "mUid=" + mUid +
                ", mName='" + mName + '\'' +
                '}';
    }
}
