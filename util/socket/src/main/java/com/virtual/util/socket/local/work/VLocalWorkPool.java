package com.virtual.util.socket.local.work;

import android.text.TextUtils;
import android.util.Log;

import com.virtual.util.socket.local.server.VLocalServer;
import com.virtual.util.socket.local.server.VLocalServerConnect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class VLocalWorkPool {

    private static final class Singleton {
        private static final VLocalWorkPool INSTANCE = new VLocalWorkPool();
    }

    public static VLocalWorkPool instance() {
        return Singleton.INSTANCE;
    }

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadPoolExecutor mExecutor;

    private VLocalWorkPool() {
        mExecutor = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread result = new Thread(r, "socket-server-work-" + POOL_NUMBER.getAndIncrement());
                result.setDaemon(false);
                return result;
            }
        });
    }

    public ThreadPoolExecutor executor() {
        return mExecutor;
    }

    private final HashMap<String, VLocalServer> mServerMap = new HashMap<>();

    public void startServer(VLocalServer server) {
        synchronized (mServerMap) {
            String address = server.address();
            VLocalServer exist = mServerMap.get(address);
            if (exist != null) {
                Log.d("VLocalWorkPool", address + " startServer is exist.");
                return;
            }
            server.start();
            mServerMap.put(address, server);
        }
    }

    public void stopServer(VLocalServer server) {
        stopServer(server.address());
    }

    public void stopServer(String address) {
        synchronized (mServerMap) {
            VLocalServer exist = mServerMap.remove(address);
            if (exist == null) {
                Log.d("VLocalWorkPool", address + " stopServer is not exist.");
                return;
            }
            exist.close();
        }
    }

    public void toStringServer() {
        Log.d("VLocalWorkPool", "toStringServer start: " + mServerMap.size());
        for (Map.Entry<String, VLocalServer> entry : mServerMap.entrySet()) {
            Log.d("VLocalWorkPool", entry.getKey() + " : " + entry.getValue());
        }
        Log.d("VLocalWorkPool", "toStringServer end.");
    }

    private final HashMap<String, VLocalServerConnect> mConnectMap = new HashMap<>();

    private String recordKey(String name, int userId) {
        if (TextUtils.isEmpty(name) || userId < 0) {
            return null;
        }
        return "[" + name + "][" + userId + "]";
    }

    public void recordConnect(String name, int userId, VLocalServerConnect connect) {
        VLocalServerConnect exist = removeConnect(name, userId);
        if (exist != null) {
            exist.close();
        }
        if (connect != null) {
            String key = recordKey(name, userId);
            if (TextUtils.isEmpty(key)) {
                return;
            }
            synchronized (mConnectMap) {
                mConnectMap.put(key, connect);
            }
        }
    }

    public VLocalServerConnect getConnect(String name, int userId) {
        String key = recordKey(name, userId);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        synchronized (mConnectMap) {
            return mConnectMap.get(key);
        }
    }

    public VLocalServerConnect removeConnect(String name, int userId) {
        String key = recordKey(name, userId);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        synchronized (mConnectMap) {
            return mConnectMap.remove(key);
        }
    }

    public void clearConnect(String name, int userId) {
        recordConnect(name, userId, null);
    }

    public void toStringConnect() {
        Log.d("VLocalWorkPool", "toStringConnect start: " + mConnectMap.size());
        for (Map.Entry<String, VLocalServerConnect> entry : mConnectMap.entrySet()) {
            Log.d("VLocalWorkPool", entry.getKey() + " : " + entry.getValue());
        }
        Log.d("VLocalWorkPool", "toStringConnect end.");
    }
}
