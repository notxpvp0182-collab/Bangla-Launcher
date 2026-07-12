package net.kdt.pojavlaunch.premium.account;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.security.MessageDigest;
import java.util.Base64;

public class SecurityManager {
    private Context context;
    private SharedPreferences prefs;
    private Gson gson;
    private static final String SECURITY_PREFS = "bangla_security";

    public SecurityManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(SECURITY_PREFS, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void setPinLock(String pin) {
        String hashed = hashString(pin);
        prefs.edit().putString("pin_lock", hashed).apply();
        prefs.edit().putBoolean("pin_enabled", true).apply();
    }

    public boolean verifyPin(String pin) {
        String stored = prefs.getString("pin_lock", null);
        if (stored == null) return false;
        return stored.equals(hashString(pin));
    }

    public void disablePinLock() {
        prefs.edit().putBoolean("pin_enabled", false).apply();
        prefs.edit().remove("pin_lock").apply();
    }

    public boolean isPinLockEnabled() {
        return prefs.getBoolean("pin_enabled", false);
    }

    public void enableFingerprintLock() {
        prefs.edit().putBoolean("fingerprint_enabled", true).apply();
    }

    public void disableFingerprintLock() {
        prefs.edit().putBoolean("fingerprint_enabled", false).apply();
    }

    public boolean isFingerprintLockEnabled() {
        return prefs.getBoolean("fingerprint_enabled", false);
    }

    public String encryptToken(String token) {
        try {
            byte[] encoded = Base64.getEncoder().encode(token.getBytes());
            return new String(encoded);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptToken(String encryptedToken) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedToken.getBytes());
            return new String(decoded);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
