package com.virtual.util.socket.local.server;

import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.socket.local.work.VLocalWork;
import com.virtual.util.socket.local.work.VLocalWorkPool;

public abstract class VLocalServer extends VLocalWork {
    private LocalServerSocket mServerSocket;

    @NonNull
    public abstract String address();

    protected Looper acceptLooper() {
        return null;
    }

    protected Looper sendLooper() {
        return null;
    }

    protected abstract VLocalServerConnect createVServerConnect(int uid, int pid, LocalSocket localSocket, Looper acceptLooper, Looper sendLooper);

    @Override
    protected void doWork() throws Throwable {
        mServerSocket = new LocalServerSocket(address());
        Log.d("VLocalServer", "doWork mServerSocket: " + mServerSocket);
        linkSuccess(mServerSocket);
        while (isRunning()) {
            LocalSocket localSocket = mServerSocket.accept();
            Log.d("VLocalServer", "doWork localSocket: " + localSocket);
            if (localSocket != null) {
                Credentials credentials = localSocket.getPeerCredentials();
                int uid = credentials.getUid();
                int pid = credentials.getPid();
                Log.d("VLocalServer", "doWork uid: " + uid + " pid: " + pid);

                VLocalServerConnect connect = createVServerConnect(uid, pid, localSocket, acceptLooper(), sendLooper());
                connect.start();
            }
        }
    }

    protected void linkSuccess(LocalServerSocket serverSocket) {

    }

    @Override
    protected void doThrowable() {
        close();
    }

    @Override
    public void start() {
        VLocalWorkPool.instance().startServer(this);
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
            Log.e("VLocalServer", "close Throwable: ", throwable);
        }
    }
}
