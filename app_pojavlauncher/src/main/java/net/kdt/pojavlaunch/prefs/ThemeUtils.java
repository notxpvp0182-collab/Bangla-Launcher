package net.kdt.pojavlaunch.prefs;

import android.app.Activity;

import com.banglalauncher.app.R;

/**
 * Central place for resolving and applying the user's selected theme preset.
 * Kept independent of LauncherPreferences' bulk load/save cycle since the
 * theme must be applied very early (before super.onCreate()) in every
 * activity, via BaseActivity.
 */
public final class ThemeUtils {
    private ThemeUtils() {}

    public static final String PREF_KEY = "themePreset";

    public static final String THEME_DEFAULT = "default";
    public static final String THEME_BLUE = "blue";
    public static final String THEME_ORANGE = "orange";
    public static final String THEME_CRIMSON = "crimson";
    public static final String THEME_TEAL = "teal";

    public static void applySelectedTheme(Activity activity) {
        String preset = THEME_DEFAULT;
        try {
            if (LauncherPreferences.DEFAULT_PREF != null) {
                preset = LauncherPreferences.DEFAULT_PREF.getString(PREF_KEY, THEME_DEFAULT);
            }
            // else: preferences not loaded yet (e.g. storage permission not granted
            // on first launch) - fall back to the default theme, do not crash.
        } catch (Exception e) {
            preset = THEME_DEFAULT;
        }
        activity.setTheme(resolveStyleRes(preset));
    }

    public static int resolveStyleRes(String preset) {
        if (preset == null) return R.style.AppTheme;
        switch (preset) {
            case THEME_BLUE:
                return R.style.AppTheme_Blue;
            case THEME_ORANGE:
                return R.style.AppTheme_Orange;
            case THEME_CRIMSON:
                return R.style.AppTheme_Crimson;
            case THEME_TEAL:
                return R.style.AppTheme_Teal;
            case THEME_DEFAULT:
            default:
                return R.style.AppTheme;
        }
    }

    /**
     * Resolves a themeXxx attribute (declared in attributes.xml) against the given
     * context's current theme, returning the actual ARGB color int. Use this from
     * Java code instead of Resources.getColor(R.color.xxx) for any of the six
     * customizable theme colors, so the Theme Customizer applies everywhere.
     */
    public static int resolveThemeColor(android.content.Context context, int attrResId) {
        android.util.TypedValue typedValue = new android.util.TypedValue();
        context.getTheme().resolveAttribute(attrResId, typedValue, true);
        return typedValue.data;
    }
}
