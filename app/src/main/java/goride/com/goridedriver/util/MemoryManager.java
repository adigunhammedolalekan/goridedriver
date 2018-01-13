package goride.com.goridedriver.util;

import android.content.SharedPreferences;
import android.location.Location;
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

    public void photo(String photo) {
        mEditor.putString("photo_uri", photo)
                .apply();
    }

    public void putLocation(Location location) {
        mSharedPreferences.edit()
                .putString("location_latitude", String.valueOf(location.getLatitude()))
                .putString("location_longitude", String.valueOf(location.getLongitude()))
                .apply();
    }
    public Location getLocation() {

        Location location = new Location("");
        try {
            double lat = Double.valueOf(mSharedPreferences.getString("location_latitude", "0.0"));
            double lon = Double.valueOf(mSharedPreferences.getString("location_longitude", "0.0"));

            location.setLatitude(lat);
            location.setLongitude(lon);
        }catch (Exception ex) {}

        return location;

    }
    public String photo() {
        return mSharedPreferences.getString("photo_uri", "");
    }
}
