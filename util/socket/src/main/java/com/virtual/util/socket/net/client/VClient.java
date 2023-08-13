package com.virtual.util.socket.net.client;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.socket.net.work.VWork;
import com.virtual.util.socket.net.work.VWorkClientPool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class VClient extends VWork {

    private Handler mAcceptHandler;
    private Handler mSendHandler;
    private HandlerThread mHandlerThread;
    private Socket mSocket;
    private PrintWriter mWriter;
    private BufferedReader mReader;

    @NonNull
    protected String hostname() {
        return "localhost";
    }

    /**
     * A valid port value is between 0 and 65535
     * @return port
     */
    protected abstract int port();

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
            mHandlerThread = new HandlerThread("client-connect");
            mHandlerThread.start();
        }
    }

    protected abstract void handleResult(String result);

    @Override
    protected void doWork() throws Throwable {
        mSocket = new Socket();
        mSocket.connect(new InetSocketAddress(hostname(), port()));
        int timeout = timeout();
        if (timeout > 0) {
            mSocket.setSoTimeout(timeout);
        }

        mWriter = new PrintWriter(mSocket.getOutputStream());
        mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

        while (isRunning()) {
            String result = mReader.readLine();
            if (result == null) {
                close();
                break;
            }
            if (result.startsWith("connected")) {
                send("connected" + name() + "#*#" + Process.myUid());
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

    @Override
    public void start() {
        VWorkClientPool.instance().startClient(this);
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
        Log.d("VClient", "close: " + name + " " + Process.myUid() + " " + Process.myPid());
        super.close();
        VWorkClientPool.instance().removeClient(name);
        try {
            if (mWriter != null) {
                mWriter.close();
                mWriter = null;
            }
            if (mReader != null) {
                mReader.close();
                mReader = null;
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            if (mHandlerThread != null) {
                mHandlerThread.quit();
                mHandlerThread = null;
            }
        } catch (Throwable throwable) {
            Log.e("VClient", "close Throwable: ", throwable);
        }
    }
}
