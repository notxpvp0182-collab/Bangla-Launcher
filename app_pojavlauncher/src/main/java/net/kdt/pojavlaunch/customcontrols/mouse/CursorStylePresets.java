package net.kdt.pojavlaunch.customcontrols.mouse;

import com.banglalauncher.app.R;

/** Maps cursor style preference values to their drawable resources. */
public final class CursorStylePresets {
    private CursorStylePresets() {}

    public static final String KEY_CLASSIC = "classic";
    public static final String KEY_GREEN = "green";
    public static final String KEY_DOT = "dot";
    public static final String KEY_CROSS = "cross";

    public static int resolvePresetDrawableRes(String key) {
        if (key == null) return R.drawable.ic_cursor_classic;
        switch (key) {
            case KEY_GREEN:
                return R.drawable.ic_cursor_green;
            case KEY_DOT:
                return R.drawable.ic_cursor_dot;
            case KEY_CROSS:
                return R.drawable.ic_cursor_cross;
            case KEY_CLASSIC:
            default:
                return R.drawable.ic_cursor_classic;
        }
    }
}
