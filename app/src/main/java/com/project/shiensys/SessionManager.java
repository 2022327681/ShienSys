package com.project.shiensys;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "shiensys_session";
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void save(String token, int userId, String name, String email, String role) {
        editor.putString("token", token);
        editor.putInt("user_id", userId);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("role", role);
        editor.putBoolean("logged_in", true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("logged_in", false);
    }

    public String token() {
        return prefs.getString("token", "");
    }

    public int userId() {
        return prefs.getInt("user_id", 0);
    }

    public String role() {
        return prefs.getString("role", "");
    }

    public String email() {
        return prefs.getString("email", "");
    }

    public String name() {
        return prefs.getString("name", "");
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
