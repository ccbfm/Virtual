package com.virtual.util.persist.model;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

public abstract class VModel<T> {

    public VModel() {

    }

    protected abstract T getModel();

    public String toJson() {
        JSONObject jsonObject = toJsonObject();
        return jsonObject != null ? jsonObject.toString() : "";
    }

    public JSONObject toJsonObject() {
        T model = getModel();
        if (model == null) {
            Log.e("VModel", "toJson getModel is null.");
            return null;
        }
        try {
            Class<?> clazz = model.getClass();
            Field[] fields = clazz.getFields();
            JSONObject jsonObject = new JSONObject();
            for (Field field : fields) {
                Class<?> type = field.getType();
                String name = field.getName();
                //Log.d("VModel", "toJson-type=" + type + " , " + name);
                if (type.getSuperclass() == VModel.class) {
                    VModel<?> childModel = (VModel<?>) field.get(this);
                    //Log.d("VModel", "childModel=" + childModel);
                    if (childModel != null) {
                        //Log.d("VModel", "childModel.toJson()=" + childModel.toJsonObject());
                        jsonObject.put(name, childModel.toJsonObject());
                    }
                } else if (type == List.class) {
                    //todo
                } else {
                    field.setAccessible(true);
                    jsonObject.put(name, field.get(this));
                }
            }
            return jsonObject;
        } catch (Throwable th) {
            Log.e("VModel", "toJson-Throwable: ", th);
        }
        return null;
    }

    public T fromJson(String json) {
        T model = getModel();
        if (model == null) {
            Log.e("VModel", "fromJson getModel is null.");
            return null;
        }
        if (TextUtils.isEmpty(json)) {
            return model;
        }
        try {
            Class<?> clazz = model.getClass();
            Field[] fields = clazz.getFields();
            JSONObject jsonObject = new JSONObject(json);
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                String name = field.getName();
                //Log.d("VModel", "type=" + type + " , " + name);
                if (type == String.class) {
                    String v = jsonObject.optString(name, defaultString());
                    field.set(model, v);
                } else if (type == int.class) {
                    int v = jsonObject.optInt(name, defaultInt());
                    field.setInt(model, v);
                } else if (type == long.class) {
                    long v = jsonObject.optLong(name, defaultLong());
                    field.setLong(model, v);
                } else if (type == float.class) {
                    float v = (float) jsonObject.optDouble(name, defaultFloat());
                    field.setFloat(model, v);
                } else if (type == double.class) {
                    double v = jsonObject.optDouble(name, defaultDouble());
                    field.setDouble(model, v);
                } else if (type == boolean.class) {
                    boolean v = jsonObject.optBoolean(name, defaultBoolean());
                    field.setBoolean(model, v);
                } else if (type == Integer.class || type == Long.class
                        || type == Float.class || type == Double.class
                        || type == Boolean.class) {
                    field.set(model, jsonObject.opt(name));
                } else if (type.getSuperclass() == VModel.class) {
                    String childString = jsonObject.optString(name);
                    if (!TextUtils.isEmpty(childString)) {
                        VModel<?> childModel = (VModel<?>) type.newInstance();
                        //Log.d("VModel", "childModel=" + childModel);
                        //Log.d("VModel", "jsonObject.optString(name)=" + jsonObject.optString(name));
                        childModel.fromJson(childString);
                        field.set(model, childModel);
                    }
                } else if (type == List.class) {
                    //todo
                }
            }
            return model;
        } catch (Throwable th) {
            Log.e("VModel", "toJson-Throwable: ", th);
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return toJson();
    }

    protected String defaultString() {
        return "";
    }

    protected int defaultInt() {
        return 0;
    }

    protected long defaultLong() {
        return 0L;
    }

    protected float defaultFloat() {
        return 0F;
    }

    protected double defaultDouble() {
        return 0D;
    }

    protected boolean defaultBoolean() {
        return false;
    }
}
