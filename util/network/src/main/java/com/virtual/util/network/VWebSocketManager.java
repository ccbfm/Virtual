package com.virtual.util.network;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class VWebSocketManager {

    private static final class Singleton {
        private static final VWebSocketManager INSTANCE = new VWebSocketManager();
    }

    public static VWebSocketManager instance() {
        return VWebSocketManager.Singleton.INSTANCE;
    }

    private VWebSocketManager() {
    }

    private final HashMap<String, Builder> mWebSocketMap = new HashMap<>();

    public void createWebSocket(@NonNull String key, @NonNull Builder builder) {
        Builder oldBuilder = mWebSocketMap.get(key);
        if (oldBuilder != null) {
            Log.w("VWebSocketManager", "createWebSocket key " + key + " is exist.");
            return;
        }
        mWebSocketMap.put(key, builder);
    }

    public void sendMessage(@NonNull String url, String jsonStr) {
        Builder builder = mWebSocketMap.get(url);
        if (builder == null) {
            Log.w("VWebSocketManager", "createWebSocket url " + url + " is not exist.");
            return;
        }
        builder.send(jsonStr);
    }

    public static class Builder {
        private WebSocket webSocket;
        private OkHttpClient client;
        private Request request;
        private final String url;
        private final WebSocketListener listener;

        private long readTimeout, writeTimeout, connectTimeout;

        private ScheduledThreadPoolExecutor pingScheduled;
        private String pingString;
        private long pingInterval;

        private final Handler wsHandler;

        private long retryConnectTime = 0;

        private @WsStatus int wsStatus = WsStatus.DISCONNECTED;

        private WsStatusListener wsStatusListener;
        private final WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
                setWsStatus(WsStatus.DISCONNECTED);
                retryConnect();
                if (Builder.this.listener != null) {
                    Builder.this.listener.onClosed(webSocket, code, reason);
                }
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosing(webSocket, code, reason);
                if (Builder.this.listener != null) {
                    Builder.this.listener.onClosing(webSocket, code, reason);
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                setWsStatus(WsStatus.DISCONNECTED);
                retryConnect();
                if (Builder.this.listener != null) {
                    Builder.this.listener.onFailure(webSocket, t, response);
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                super.onMessage(webSocket, text);
                if (Builder.this.listener != null) {
                    Builder.this.listener.onMessage(webSocket, text);
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (Builder.this.listener != null) {
                    Builder.this.listener.onMessage(webSocket, bytes);
                }
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
                setWsStatus(WsStatus.CONNECTED);
                if (Builder.this.listener != null) {
                    Builder.this.listener.onOpen(webSocket, response);
                }
            }
        };

        public Builder(@NonNull String url, @NonNull WebSocketListener listener) {
            this.url = url;
            this.listener = listener;
            this.wsHandler = new Handler(Looper.getMainLooper());
        }

        public Builder setClient(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder setRequest(Request request) {
            this.request = request;
            return this;
        }

        public Builder setWsStatusListener(WsStatusListener wsStatusListener) {
            this.wsStatusListener = wsStatusListener;
            return this;
        }

        /**
         * @param readTimeout    读超时时间 单位秒
         * @param writeTimeout   写超时时间 单位秒
         * @param connectTimeout 连接超时时间 单位秒
         * @return Builder
         */
        public Builder setTimeout(long readTimeout, long writeTimeout, long connectTimeout) {
            this.readTimeout = readTimeout;
            this.writeTimeout = writeTimeout;
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * @param pingInterval 心跳时间 单位秒
         * @return Builder
         */
        public Builder setPingParam(long pingInterval) {
            return setPingParam(null, pingInterval);
        }

        /**
         * @param pingString   心跳内容
         * @param pingInterval 心跳时间 单位秒
         * @return Builder
         */
        public Builder setPingParam(String pingString, long pingInterval) {
            this.pingString = pingString;
            this.pingInterval = pingInterval;
            return this;
        }

        /**
         * @param retryConnectTime 重试连接时间 单位秒
         * @return Builder
         */
        public Builder setRetryConnectTime(long retryConnectTime) {
            this.retryConnectTime = TimeUnit.SECONDS.toMillis(retryConnectTime);
            return this;
        }

        private void setWsStatus(@WsStatus int wsStatus) {
            this.wsStatus = wsStatus;
            if (this.wsStatusListener != null) {
                this.wsStatusListener.change(wsStatus);
            }
        }

        public Builder build() {
            if (this.client == null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                //设置读取超时时间
                if (this.readTimeout > 0) {
                    builder.readTimeout(this.readTimeout, TimeUnit.SECONDS);
                }
                //设置写的超时时间
                if (this.writeTimeout > 0) {
                    builder.writeTimeout(this.writeTimeout, TimeUnit.SECONDS);
                }
                //设置连接超时时间
                if (this.connectTimeout > 0) {
                    builder.connectTimeout(this.connectTimeout, TimeUnit.SECONDS);
                }
                if (this.pingInterval > 0) {
                    if (TextUtils.isEmpty(this.pingString)) {
                        builder.pingInterval(this.pingInterval, TimeUnit.SECONDS);
                    } else {
                        this.pingScheduled = new ScheduledThreadPoolExecutor(1);
                        this.pingScheduled.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                sendPing();
                            }
                        }, this.pingInterval, this.pingInterval, TimeUnit.SECONDS);
                    }
                }
                this.client = builder.build();
            }
            if (this.request == null) {
                this.request = new Request.Builder().get().url(url).build();
            }
            setWsStatus(WsStatus.CONNECTING);
            this.client.dispatcher().cancelAll();
            this.webSocket = this.client.newWebSocket(this.request, this.webSocketListener);
            return this;
        }

        private void retryConnect() {
            if (this.wsStatus == WsStatus.DISCONNECTED
                    && this.retryConnectTime > 0L) {
                this.wsStatus = WsStatus.RECONNECT;
                this.wsHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        build();
                    }
                }, this.retryConnectTime);
            }
        }

        private void sendPing() {
            send(this.pingString);
        }

        public void send(String jsonStr) {
            if (this.webSocket != null) {
                if (TextUtils.isEmpty(jsonStr)) {
                    Log.w("VWebSocketManager", "webSocket send is null.");
                    return;
                }
                this.webSocket.send(jsonStr);
            } else {
                Log.w("VWebSocketManager", "webSocket is null.");
            }
        }

        public void close() {
            Log.w("VWebSocketManager", "webSocket close.");
            if (this.webSocket != null) {
                this.webSocket.cancel();
                this.webSocket.close(0, "close");
            }
            if (this.pingScheduled != null) {
                this.pingScheduled.shutdownNow();
            }
            this.retryConnectTime = 0L;
            this.wsHandler.removeCallbacksAndMessages(null);
        }
    }

    private @interface WsStatus {
        int DISCONNECTED = 0;
        int CONNECTING = 1;
        int CONNECTED = 2;
        int RECONNECT = 3;
    }

    public interface WsStatusListener {
        void change(@WsStatus int status);
    }
}
