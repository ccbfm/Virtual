package com.virtual.ui.plane;

import androidx.annotation.NonNull;

public interface IDataChange<Data> {
    void onChange(@NonNull Data data);
}
