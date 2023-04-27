package com.virtual.util.socket.local.server;

import android.net.LocalSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.socket.local.work.VLocalWork;
import com.virtual.util.socket.local.work.VLocalWorkPool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public abstract class VLocalServerConnect extends VLocalWork {

    private Handler mHandler;
    private final int mUid;
    private final int mPid;
    private final LocalSocket mLocalSocket;
    private PrintWriter mWriter;
    private BufferedReader mReader;

    private String mName;
    private int mUserId = -1;

    public VLocalServerConnect(int uid, int pid, LocalSocket localSocket) {
        mUid = uid;
        mPid = pid;
        mLocalSocket = localSocket;
    }

    /**
     * 处理接收信息 Looper
     *
     * @return Looper
     */
    @NonNull
    protected Looper handleLooper() {
        HandlerThread handlerThread = new HandlerThread("local-server-connect-result");
        handlerThread.start();
        return handlerThread.getLooper();
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
                recordConnect(mName, mUserId);
            } else {
                if (mHandler == null) {
                    mHandler = new Handler(handleLooper()) {
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
                mHandler.sendMessage(message);
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

    public void send(final String message) {
        if (mWriter != null) {
            mWriter.println(message);
            mWriter.flush();
        }
    }

    @Override
    public void close() {
        Log.d("VLocalServerConnect", "close: " + mName + " " + mUid + " " + mPid);
        super.close();
        VLocalWorkPool.instance().removeConnect(mName, mUserId);
        try {
            if (mWriter != null) {
                mWriter.close();
            }
            if (mReader != null) {
                mReader.close();
            }
            if (mLocalSocket != null) {
                mLocalSocket.close();
            }
        } catch (Throwable throwable) {
            Log.e("VLocalServerConnect", "close Throwable: ", throwable);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "VLocalServerConnect{" + "mUid=" + mUid + ", mName='" + mName + '\'' + '}';
    }
}
