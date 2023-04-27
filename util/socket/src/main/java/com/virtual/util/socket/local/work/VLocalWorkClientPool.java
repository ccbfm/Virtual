package com.virtual.util.socket.local.work;

import android.util.Log;

import com.virtual.util.socket.local.client.VLocalClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class VLocalWorkClientPool {

    private static final class Singleton {
        private static final VLocalWorkClientPool INSTANCE = new VLocalWorkClientPool();
    }

    public static VLocalWorkClientPool instance() {
        return Singleton.INSTANCE;
    }

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadPoolExecutor mExecutor;

    private VLocalWorkClientPool() {
        mExecutor = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread result = new Thread(r, "socket-client-work-" + POOL_NUMBER.getAndIncrement());
                result.setDaemon(false);
                return result;
            }
        });
    }

    public ThreadPoolExecutor executor() {
        return mExecutor;
    }

    private final HashMap<String, VLocalClient> mClientMap = new HashMap<>();

    public void startClient(VLocalClient client) {
        synchronized (mClientMap) {
            String name = client.name();
            VLocalClient exist = mClientMap.get(name);
            if (exist != null) {
                Log.d("VWorkClientPool", name + " startClient is exist.");
                return;
            }
            executor().execute(client);
            mClientMap.put(name, client);
        }
    }

    public VLocalClient getClient(String name) {
        synchronized (mClientMap) {
            return mClientMap.get(name);
        }
    }

    public void stopClient(VLocalClient client) {
        stopClient(client.name());
    }

    public void stopClient(String name) {
        VLocalClient exist = removeClient(name);
        if (exist == null) {
            Log.d("VWorkClientPool", name + " stopClient is not exist.");
            return;
        }
        exist.close();
    }

    public VLocalClient removeClient(String name) {
        synchronized (mClientMap) {
            return mClientMap.remove(name);
        }
    }

    public void toStringClient() {
        Log.d("VWorkClientPool", "toStringClient start: " + mClientMap.size());
        for (Map.Entry<String, VLocalClient> entry : mClientMap.entrySet()) {
            Log.d("VWorkClientPool", entry.getKey() + " : " + entry.getValue());
        }
        Log.d("VWorkClientPool", "toStringClient end.");
    }
}
