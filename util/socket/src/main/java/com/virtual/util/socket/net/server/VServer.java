package com.virtual.util.socket.net.server;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.socket.net.work.VWork;
import com.virtual.util.socket.net.work.VWorkPool;

import java.net.ServerSocket;
import java.net.Socket;

public abstract class VServer extends VWork {
    private ServerSocket mServerSocket;

    @NonNull
    public abstract String address();

    /**
     * A valid port value is between 0 and 65535
     *
     * @return port
     */
    public abstract int port();

    protected Looper acceptLooper() {
        return null;
    }

    protected Looper sendLooper() {
        return null;
    }

    protected abstract VServerConnect createVServerConnect(int port, Socket socket,
                                                           Looper acceptLooper, Looper sendLooper);

    @Override
    protected void doWork() throws Throwable {
        mServerSocket = new ServerSocket(port());
        Log.d("VServer", "doWork mServerSocket: " + mServerSocket);
        linkSuccess(mServerSocket);
        while (isRunning()) {
            Socket socket = mServerSocket.accept();
            Log.d("VServer", "doWork socket: " + socket);
            if (socket != null) {
                int port = socket.getPort();
                VServerConnect connect = createVServerConnect(port, socket, acceptLooper(), sendLooper());
                connect.start();
            }
        }
    }

    protected void linkSuccess(ServerSocket serverSocket) {

    }

    @Override
    protected void doThrowable() {
        close();
    }

    @Override
    public void start() {
        VWorkPool.instance().startServer(this);
    }

    @Override
    public void close() {
        super.close();
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (Throwable throwable) {
            Log.e("VServer", "close Throwable: ", throwable);
        }
    }
}
