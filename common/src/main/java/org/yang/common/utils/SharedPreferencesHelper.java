package org.yang.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONObject;

/**
 * 与应用有关的属性设置类
 * created by Gxy on 2015/03/02
 */
public class SharedPreferencesHelper {

    private static SharedPreferencesHelper ourInstance;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    private SharedPreferencesHelper(Context c, String name) {
        context = c;
        sp = context.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        editor = sp.edit();
    }

    public static SharedPreferencesHelper getInstance(Context context, String name) {
        if (ourInstance == null) {
            return ourInstance = new SharedPreferencesHelper(context, name);
        } else {
            return ourInstance;
        }
    }

    public boolean putValue(String key, Object value) {

        if (sp.contains(key)) {
            editor.remove(key);
        }
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof JSONObject) {
            editor.putString(key, value.toString());
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            editor.apply();
            return editor.commit();
        } else {
            return editor.commit();
        }
    }

    public boolean remove(String key) {
        if (sp.contains(key)) {
            return editor.remove(key).commit();
        } else {
            return false;
        }
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    public String getValue(String key) {
        return sp.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public Integer getInteger(String key) {
        return sp.getInt(key, 0);
    }

    public String getValue(String key, String defValue) {
        return sp.getString(key, defValue);
    }
}
