package co.id.idpay.ektp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created      : Rahman on 8/25/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.helper.
 * Copyright    : idpay.com 2017.
 */
public class PrefManager {
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "IdPayManifest";

    // All Shared Preferences Keys
    private static final String KEY_PCID = "pcid";
    private static final String KEY_CONF = "conf";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLogin(String user, String pass) {
        editor.putString(KEY_NAME, user);
        editor.putString(KEY_EMAIL, pass);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public void setPcid(String pcid) {
        editor.putString(KEY_PCID, pcid);
        editor.commit();
    }

    public String getPcid() {
        return pref.getString(KEY_PCID, null);
    }
    public void setConf(String conf) {
        editor.putString(KEY_CONF, conf);
        editor.commit();
    }

    public String getConf() {
        return pref.getString(KEY_CONF, null);
    }



}
