package com.virtual.util.socket.net.server;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.socket.net.work.VWork;
import com.virtual.util.socket.net.work.VWorkPool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class VServerConnect extends VWork {

    private Handler mHandler;
    private final int mPort;
    private final Socket mSocket;
    private PrintWriter mWriter;
    private BufferedReader mReader;

    private String mName;
    private int mUid;
    private int mUserId = -1;

    public VServerConnect(int port, Socket socket) {
        mPort = port;
        mSocket = socket;
    }

    /**
     * 处理接收信息 Looper
     * android.os.NetworkOnMainThreadException
     *
     * @return Looper
     */
    @NonNull
    protected Looper handleLooper() {
        HandlerThread handlerThread = new HandlerThread("server-connect-result");
        handlerThread.start();
        return handlerThread.getLooper();
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
        VWorkPool.instance().recordConnect(name, userId, this);
    }

    @Override
    public void start() {
        VWorkPool.instance().executor().execute(this);
    }

    public void send(final String message) {
        if (mWriter != null) {
            mWriter.println(message);
            mWriter.flush();
        }
    }

    @Override
    public void close() {
        Log.d("VServerConnect", "close: " + mName + " " + mUid + " " + mPort);
        super.close();
        VWorkPool.instance().removeConnect(mName, mUserId);
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
