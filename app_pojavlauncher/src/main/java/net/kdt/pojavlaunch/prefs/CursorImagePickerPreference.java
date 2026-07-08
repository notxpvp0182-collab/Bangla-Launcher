package net.kdt.pojavlaunch.prefs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import com.banglalauncher.app.R;

import java.io.File;

/**
 * Displays the current custom cursor image status and, when clicked, notifies
 * a registered listener so the hosting fragment can launch a system image
 * picker (the actual file-picking Intent must be launched from a Fragment/
 * Activity to use the modern ActivityResult API, hence the callback).
 */
public class CursorImagePickerPreference extends Preference {

    public interface OnPickRequestedListener {
        void onPickRequested();
        void onClearRequested();
    }

    private OnPickRequestedListener mListener;

    public CursorImagePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnPickRequestedListener(OnPickRequestedListener listener) {
        mListener = listener;
    }

    /** Call after the stored path preference changes, to refresh the summary text. */
    public void refreshSummary() {
        String path = getSharedPreferences() != null
                ? getSharedPreferences().getString(getKey(), null)
                : null;
        if (path != null && new File(path).exists()) {
            setSummary(R.string.cursor_image_picker_current);
        } else {
            setSummary(R.string.cursor_image_picker_none);
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        String path = getSharedPreferences() != null
                ? getSharedPreferences().getString(getKey(), null)
                : null;
        boolean hasImage = path != null && new File(path).exists();

        if (hasImage) {
            // Offer to clear the existing image rather than immediately reopening the picker.
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle(getTitle())
                    .setItems(new CharSequence[]{
                            getContext().getString(R.string.cursor_image_picker_clear),
                            getContext().getString(R.string.preference_cursor_custom_image_title)
                    }, (dialog, which) -> {
                        if (which == 0 && mListener != null) {
                            mListener.onClearRequested();
                        } else if (mListener != null) {
                            mListener.onPickRequested();
                        }
                    }).show();
        } else if (mListener != null) {
            mListener.onPickRequested();
        }
    }
}
