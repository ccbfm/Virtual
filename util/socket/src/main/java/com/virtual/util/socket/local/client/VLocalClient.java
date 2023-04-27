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
    private Handler mHandler;
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
     * 处理接收信息 Looper
     *
     * @return Looper
     */
    @NonNull
    protected Looper handleLooper() {
        HandlerThread handlerThread = new HandlerThread("local-client-result");
        handlerThread.start();
        return handlerThread.getLooper();
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

        while (isRunning()) {
            String result = mReader.readLine();
            if (result == null) {
                close();
                break;
            }
            if (result.startsWith("connected")) {
                send("connected" + name());
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
        VLocalWorkClientPool.instance().executor().execute(this);
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
        Log.d("VLocalClient", "close: " + name + " " + Process.myUid() + " " + Process.myPid());
        super.close();
        VLocalWorkClientPool.instance().removeClient(name);
        try {
            if (mWriter != null) {
                mWriter.close();
            }
            if (mReader != null) {
                mReader.close();
            }
            if (mClientSocket != null) {
                mClientSocket.close();
            }
        } catch (Throwable throwable) {
            Log.e("VLocalClient", "close Throwable: ", throwable);
        }
    }
}
