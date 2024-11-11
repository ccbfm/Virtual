package com.virtual.util.socket.net.server;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.virtual.util.socket.net.work.VWork;
import com.virtual.util.socket.net.work.VWorkPool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class VServerConnect extends VWork {

    private final int mPort;
    private Socket mSocket;
    private Looper mAcceptLooper;
    private Looper mSendLooper;

    private Handler mAcceptHandler;
    private Handler mSendHandler;
    private HandlerThread mHandlerThread;
    private PrintWriter mWriter;
    private BufferedReader mReader;

    private String mName;
    private int mUid;
    private int mUserId = -1;

    public VServerConnect(int port, Socket socket) {
        mPort = port;
        mSocket = socket;
    }

    public VServerConnect(int port, Socket socket,
                          Looper acceptLooper, Looper sendLooper) {
        mPort = port;
        mSocket = socket;
        mAcceptLooper = acceptLooper;
        mSendLooper = sendLooper;
    }

    public String getName() {
        return mName;
    }

    public int getUid() {
        return mUid;
    }

    public int getUserId() {
        return mUserId;
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

    public Handler getAcceptHandler() {
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
        return mAcceptHandler;
    }

    public Handler getSendHandler() {
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
        return mSendHandler;
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
        final Socket socket = mSocket;
        mWriter = new PrintWriter(socket.getOutputStream());
        mReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        send("connected");
        while (isRunning()) {
            String result = mReader.readLine();
            if (result == null) {
                close();
                break;
            }
            if (result.startsWith("connected")) {
                String info = result.replaceFirst("connected", "");
                Log.d("VServerConnect", "connected info: " + info);
                String[] infoArr = info.split("#*#");
                mName = infoArr[0];
                mUid = Integer.parseInt(infoArr[2]);
                mUserId = mUid / 100000;
                if (TextUtils.isEmpty(mName) || mUserId < 0) {
                    throw new NullPointerException("VServerConnect connected is null.{ " + mName + " , " + mUserId + " }");
                }
                recordConnect(mName, mUserId);
            } else if (result.startsWith("ping")) {
                send("ping");
            } else {
                Handler acceptHandler = getAcceptHandler();
                Message message = Message.obtain();
                message.what = 1;
                message.obj = result;
                acceptHandler.sendMessage(message);
            }
        }
    }

    @Override
    protected void doThrowable() {
        close();
    }

    @CallSuper
    protected void recordConnect(String name, int userId) {
        VWorkPool.instance().recordConnect(name, userId, this);
    }

    @Override
    public void start() {
        VWorkPool.instance().executor().execute(this);
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
        message.what = 1;
        message.obj = text;
        sendHandler.sendMessageDelayed(message, delayMillis);
    }

    @Override
    public void close() {
        Log.d("VServerConnect", "close: " + mName + " " + mUid + " " + mPort);
        super.close();
        VWorkPool.instance().removeConnect(mName, mUserId);
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
            Log.e("VServerConnect", "close Throwable: ", throwable);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "VServerConnect{" +
                "mPort=" + mPort +
                ", mName='" + mName + '\'' +
                ", mUid=" + mUid +
                '}';
    }
}
