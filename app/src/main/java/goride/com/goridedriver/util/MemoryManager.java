package goride.com.goridedriver.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import goride.com.goridedriver.GoDriverApplication;

/**
 * Created by root on 11/21/17.
 */

public class MemoryManager {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static MemoryManager manager;

    public static synchronized MemoryManager manager() {
        if(manager == null) manager = new MemoryManager();

        return manager;
    }
    private MemoryManager() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(GoDriverApplication.getApplication().getApplicationContext());
        mEditor = mSharedPreferences.edit();
    }
    public MemoryManager phoneNumber(String phone) {
        mEditor.putString("phone_number", phone)
                .apply();
        return this;
    }
    public String phoneNumber() {
        return mSharedPreferences.getString("phone_number", "");
    }
}
