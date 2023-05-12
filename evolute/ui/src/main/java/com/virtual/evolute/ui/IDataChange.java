package com.virtual.evolute.ui;

import androidx.annotation.NonNull;

public interface IDataChange<Data> {
    void onChange(@NonNull Data data);
}
