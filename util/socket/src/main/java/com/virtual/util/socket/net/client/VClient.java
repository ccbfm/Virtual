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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
     *
     * @return port
     */
    protected abstract int port();

    @NonNull
    public abstract String name();

    protected int timeout() {
        return 0;
    }

    /**
     * 开启ping
     *
     * @return ping间隔时间 单位毫秒 0不开启；
     */
    protected int pingTime() {
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

    public Handler getAcceptHandler() {
        if (mAcceptHandler == null) {
            mAcceptHandler = new Handler(acceptLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    int what = msg.what;
                    if (what == HWhat.MESSAGE) {
                        String result = (String) msg.obj;
                        handleResult(result);
                    } else if (what == HWhat.PING_TIMEOUT) {
                        Log.d("VClient", "ping timeout: " + name());
                        close();
                    }
                }
            };
        }
        return mAcceptHandler;
    }

    public Handler getSendHandler() {
        if (mSendHandler == null) {
            mSendHandler = new Handler(sendLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == HWhat.MESSAGE) {
                        String text = (String) msg.obj;
                        if (mWriter != null) {
                            mWriter.println(text);
                            mWriter.flush();
                        }
                    }
                }
            };
        }
        return mSendHandler;
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
        linkSuccess(mSocket);
        while (isRunning()) {
            String result = mReader.readLine();
            if (result == null) {
                close();
                break;
            }
            if (result.startsWith("connected")) {
                send("connected" + name() + "#*#" + Process.myUid());
                sendPing();
            } else if (result.startsWith("ping")) {
                sendPing();
            } else {
                Handler acceptHandler = getAcceptHandler();
                Message message = Message.obtain();
                message.what = HWhat.MESSAGE;
                message.obj = result;
                acceptHandler.sendMessage(message);
            }
        }
    }

    private void sendPing() {
        int pingTime = pingTime();
        if (pingTime > 0) {
            Handler acceptHandler = getAcceptHandler();
            acceptHandler.removeMessages(HWhat.PING_TIMEOUT);
            acceptHandler.sendEmptyMessageDelayed(HWhat.PING_TIMEOUT, (pingTime + 30_000));
            send("ping", pingTime);
        }
    }

    protected void linkSuccess(Socket socket) {

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
        send(text, 0L);
    }

    public void send(final String text, long delayMillis) {
        if (mWriter == null) {
            return;
        }
        Handler sendHandler = getSendHandler();
        Message message = Message.obtain();
        message.what = HWhat.MESSAGE;
        message.obj = text;
        sendHandler.sendMessageDelayed(message, delayMillis);
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

    @Retention(RetentionPolicy.SOURCE)
    private @interface HWhat {
        int MESSAGE = 1;
        int PING_TIMEOUT = MESSAGE + 1;
    }
}
