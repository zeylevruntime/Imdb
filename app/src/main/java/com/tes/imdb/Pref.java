package com.tes.imdb;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {
    private static Pref mSharedPreferenceUtils;
    protected Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor spEdit;
    private Pref(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("IMDB", Context.MODE_PRIVATE);
        spEdit = mSharedPreferences.edit();
    }
    public static synchronized Pref getInstance(Context context) {

        if (mSharedPreferenceUtils == null) {
            mSharedPreferenceUtils = new Pref(context.getApplicationContext());
        }
        return mSharedPreferenceUtils;
    }

    public void clear() {
        spEdit.clear();
        spEdit.commit();
    }

    public String getSearch() {
        return mSharedPreferences.getString("search", "flash");
    }
    public void setSearch(String v) {
        spEdit.putString("search", v);
        spEdit.commit();
    }
    public String getType() {
        return mSharedPreferences.getString("type", null);
    }
    public void setType(String v) {
        spEdit.putString("type", v);
        spEdit.commit();
    }
    public String getYear() {
        return mSharedPreferences.getString("year", null);
    }
    public void setYear(String v) {
        spEdit.putString("year", v);
        spEdit.commit();
    }


}
