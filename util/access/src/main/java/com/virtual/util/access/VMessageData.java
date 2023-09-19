package com.virtual.util.access;

import android.os.Message;

public class VMessageData {
    public Message message;
    public long delayMillis;

    public VMessageData(Message message, long delayMillis) {
        this.message = message;
        this.delayMillis = delayMillis;
    }
}
