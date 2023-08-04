package com.virtual.util.socket.net.work;

import android.text.TextUtils;
import android.util.Log;

import com.virtual.util.socket.net.server.VServer;
import com.virtual.util.socket.net.server.VServerConnect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class VWorkPool {

    private static final class Singleton {
        private static final VWorkPool INSTANCE = new VWorkPool();
    }

    public static VWorkPool instance() {
        return Singleton.INSTANCE;
    }

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadPoolExecutor mExecutor;

    private VWorkPool() {
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

    private final HashMap<String, VServer> mServerMap = new HashMap<>();

    public void startServer(VServer server) {
        synchronized (mServerMap) {
            String address = server.address();
            VServer exist = mServerMap.get(address);
            if (exist != null) {
                Log.d("VWorkPool", address + " startServer is exist.");
                return;
            }
            executor().execute(server);
            mServerMap.put(address, server);
        }
    }

    public void stopServer(VServer server) {
        stopServer(server.address());
    }

    public void stopServer(String address) {
        synchronized (mServerMap) {
            VServer exist = mServerMap.remove(address);
            if (exist == null) {
                Log.d("VWorkPool", address + " stopServer is not exist.");
                return;
            }
            exist.close();
        }
    }

    public void toStringServer() {
        Log.d("VWorkPool", "toStringServer start: " + mServerMap.size());
        for (Map.Entry<String, VServer> entry : mServerMap.entrySet()) {
            Log.d("VWorkPool", entry.getKey() + " : " + entry.getValue());
        }
        Log.d("VWorkPool", "toStringServer end.");
    }

    private final HashMap<String, VServerConnect> mConnectMap = new HashMap<>();

    private String recordKey(String name, int userId) {
        if (TextUtils.isEmpty(name) || userId < 0) {
            return null;
        }
        return "[" + name + "][" + userId + "]";
    }

    public void recordConnect(String name, int userId, VServerConnect connect) {
        VServerConnect exist = removeConnect(name, userId);
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

    public VServerConnect getConnect(String name, int userId) {
        String key = recordKey(name, userId);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        synchronized (mConnectMap) {
            return mConnectMap.get(key);
        }
    }

    public VServerConnect removeConnect(String name, int userId) {
        String key = recordKey(name, userId);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        synchronized (mConnectMap) {
            return mConnectMap.remove(key);
        }
    }

    public void clearConnect(String name, int userId) {
        removeConnect(name, userId);
    }

    public void toStringConnect() {
        Log.d("VWorkPool", "toStringConnect start: " + mConnectMap.size());
        for (Map.Entry<String, VServerConnect> entry : mConnectMap.entrySet()) {
            Log.d("VWorkPool", entry.getKey() + " : " + entry.getValue());
        }
        Log.d("VWorkPool", "toStringConnect end.");
    }
}
