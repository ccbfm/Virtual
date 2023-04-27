package com.virtual.util.socket.net;


import com.virtual.util.socket.net.client.VClient;
import com.virtual.util.socket.net.server.VServer;
import com.virtual.util.socket.net.server.VServerConnect;
import com.virtual.util.socket.net.work.VWorkClientPool;
import com.virtual.util.socket.net.work.VWorkPool;

public final class VNetSocket {

    public static void startServer(VServer server) {
        VWorkPool.instance().startServer(server);
    }

    public static VServerConnect getConnect(String name, int userId) {
        return VWorkPool.instance().getConnect(name, userId);
    }

    public static void startClient(VClient client) {
        VWorkClientPool.instance().startClient(client);
    }

    public static VClient getClient(String name) {
        return VWorkClientPool.instance().getClient(name);
    }
}
