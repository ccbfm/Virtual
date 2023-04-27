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

    private Handler mHandler;
    private Socket mSocket;
    private PrintWriter mWriter;
    private BufferedReader mReader;

    @NonNull
    protected abstract String hostname();

    protected abstract int port();

    @NonNull
    public abstract String name();

    protected int timeout() {
        return 0;
    }

    /**
     * 处理接收信息 Looper
     * android.os.NetworkOnMainThreadException
     *
     * @return Looper
     */
    @NonNull
    protected Looper handleLooper() {
        HandlerThread handlerThread = new HandlerThread("client-result");
        handlerThread.start();
        return handlerThread.getLooper();
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

    @Override
    public void start() {
        VWorkClientPool.instance().executor().execute(this);
    }

    public void send(final String message) {
        if (mWriter != null) {
            mWriter.println(message);
            mWriter.flush();
        }
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
            }
            if (mReader != null) {
                mReader.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (Throwable throwable) {
            Log.e("VClient", "close Throwable: ", throwable);
        }
    }
}
