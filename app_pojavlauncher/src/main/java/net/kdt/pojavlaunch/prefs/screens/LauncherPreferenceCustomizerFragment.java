package net.kdt.pojavlaunch.prefs.screens;

import android.os.Bundle;

import androidx.preference.ListPreference;

import com.banglalauncher.app.R;

import net.kdt.pojavlaunch.prefs.ThemeUtils;

public class LauncherPreferenceCustomizerFragment extends LauncherPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_customizer);

        ListPreference themePreference = requirePreference(ThemeUtils.PREF_KEY, ListPreference.class);
        themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            // Persist first, then recreate so the whole activity stack picks up
            // the new theme attrs immediately.
            getPreferenceManager().getSharedPreferences().edit()
                    .putString(ThemeUtils.PREF_KEY, (String) newValue)
                    .apply();
            if (getActivity() != null) {
                getActivity().recreate();
            }
            return true;
        });
    }
}
