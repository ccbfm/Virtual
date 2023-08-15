package com.virtual.util.socket.local.client;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.socket.local.work.VLocalWork;
import com.virtual.util.socket.local.work.VLocalWorkClientPool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public abstract class VLocalClient extends VLocalWork {
    private Handler mAcceptHandler;
    private Handler mSendHandler;
    private HandlerThread mHandlerThread;
    private LocalSocket mClientSocket;
    private PrintWriter mWriter;
    private BufferedReader mReader;

    @NonNull
    protected abstract String address();

    @NonNull
    public abstract String name();

    protected int timeout() {
        return 0;
    }

    /**
     * android.os.NetworkOnMainThreadException
     *
     * @return Looper
     */
    @NonNull
    protected Looper acceptLooper() {
        checkHandlerThread();
        return mHandlerThread.getLooper();
    }

    @NonNull
    protected Looper sendLooper() {
        checkHandlerThread();
        return mHandlerThread.getLooper();
    }

    private void checkHandlerThread() {
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("local-server-connect");
            mHandlerThread.start();
        }
    }

    protected abstract void handleResult(String result);

    @Override
    protected void doWork() throws Throwable {
        mClientSocket = new LocalSocket();
        mClientSocket.connect(new LocalSocketAddress(address()));
        int timeout = timeout();
        if (timeout > 0) {
            mClientSocket.setSoTimeout(timeout);
        }

        mWriter = new PrintWriter(mClientSocket.getOutputStream());
        mReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
        linkSuccess(mClientSocket);
        while (isRunning()) {
            String result = mReader.readLine();
            if (result == null) {
                close();
                break;
            }
            if (result.startsWith("connected")) {
                send("connected" + name());
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

    protected void linkSuccess(LocalSocket socket) {

    }

    @Override
    protected void doThrowable() {
        close();
    }

    @Override
    public void start() {
        VLocalWorkClientPool.instance().startClient(this);
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
        String name = name();
        Log.d("VLocalClient", "close: " + name + " " + Process.myUid() + " " + Process.myPid());
        super.close();
        VLocalWorkClientPool.instance().removeClient(name);
        try {
            if (mWriter != null) {
                mWriter.close();
                mWriter = null;
            }
            if (mReader != null) {
                mReader.close();
                mReader = null;
            }
            if (mClientSocket != null) {
                mClientSocket.close();
                mClientSocket = null;
            }
            if (mHandlerThread != null) {
                mHandlerThread.quit();
                mHandlerThread = null;
            }
        } catch (Throwable throwable) {
            Log.e("VLocalClient", "close Throwable: ", throwable);
        }
    }
}
