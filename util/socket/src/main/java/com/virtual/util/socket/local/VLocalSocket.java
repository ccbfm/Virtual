package com.virtual.util.socket.local;


import com.virtual.util.socket.local.client.VLocalClient;
import com.virtual.util.socket.local.server.VLocalServer;
import com.virtual.util.socket.local.server.VLocalServerConnect;
import com.virtual.util.socket.local.work.VLocalWorkClientPool;
import com.virtual.util.socket.local.work.VLocalWorkPool;

/**
 * 虽然不是真正的网络传输，但是也需要声明 android.permission.INTERNET 权限，
 * 要不然同样会报java.net.SocketException: Permission denied异常
 * <p>
 * 虽然可以通过LocalSocket和framework层直接通信，但是如果系统打开了 SeLinux 就会出现Permission denied异常
 * SELinux通常有如下三种模式：disable、permissive、enforcing。通常发布版都将使用强制模式Enforcing。
 * <p>
 * disable 禁止SELinux功能
 * permissive 使能SELinux功能，但是 SELinux 不会拒绝方法但会打印日志
 * enforcing 使能SELinux，并强制按照 SELinux 的规则来进行权限访问
 * <p>
 * 可以通过 getenfoce 来获取当前工作模式，
 * 还可以通过 setenforce 0 来设置当前模式为Permissive；
 * 也可以通过 setenforce 1 来设置当前模式为Enforcing
 * <p>
 * setenforce 0  //关闭 SeLinux
 * getenforce
 */
public final class VLocalSocket {

    public static void startServer(VLocalServer server) {
        VLocalWorkPool.instance().startServer(server);
    }

    public static VLocalServerConnect getConnect(String name, int userId) {
        return VLocalWorkPool.instance().getConnect(name, userId);
    }

    public static void startClient(VLocalClient client) {
        VLocalWorkClientPool.instance().startClient(client);
    }

    public static VLocalClient getClient(String name) {
        return VLocalWorkClientPool.instance().getClient(name);
    }


}
