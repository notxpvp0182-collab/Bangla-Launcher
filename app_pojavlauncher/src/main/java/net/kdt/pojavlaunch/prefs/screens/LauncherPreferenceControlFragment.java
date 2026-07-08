package net.kdt.pojavlaunch.prefs.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.PreferenceCategory;

import com.banglalauncher.app.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreference;
import net.kdt.pojavlaunch.prefs.CursorImagePickerPreference;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class LauncherPreferenceControlFragment extends LauncherPreferenceFragment {
    private boolean mGyroAvailable = false;

    private final ActivityResultLauncher<String> mImagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onCursorImagePicked);

    private void onCursorImagePicked(Uri uri) {
        if (uri == null) return;
        if (!isAdded()) return;
        Context context = getContext();
        if (context == null) return;
        try {
            File destDir = new File(context.getFilesDir(), "cursor");
            if (!destDir.exists() && !destDir.mkdirs()) {
                throw new java.io.IOException("Failed to create cursor image directory");
            }
            File destFile = new File(destDir, "custom_cursor.png");
            try (InputStream in = context.getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(destFile)) {
                if (in == null) throw new java.io.IOException("Failed to open picked image");
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            getPreferenceManager().getSharedPreferences().edit()
                    .putString("customCursorImagePath", destFile.getAbsolutePath())
                    .apply();
            CursorImagePickerPreference picker = requirePreference("customCursorImagePath",
                    CursorImagePickerPreference.class);
            picker.refreshSummary();
            Toast.makeText(context, R.string.cursor_image_picker_current, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreatePreferences(Bundle b, String str) {
        // Get values
        int longPressTrigger = LauncherPreferences.PREF_LONGPRESS_TRIGGER;
        int prefButtonSize = (int) LauncherPreferences.PREF_BUTTONSIZE;
        int mouseScale = (int) (LauncherPreferences.PREF_MOUSESCALE * 100);
        int gyroSampleRate = LauncherPreferences.PREF_GYRO_SAMPLE_RATE;
        float mouseSpeed = LauncherPreferences.PREF_MOUSESPEED;
        float gyroSpeed = LauncherPreferences.PREF_GYRO_SENSITIVITY;
        float joystickDeadzone = LauncherPreferences.PREF_DEADZONE_SCALE;


        //Triggers a write for some reason which resets the value
        addPreferencesFromResource(R.xml.pref_control);

        CustomSeekBarPreference seek2 = requirePreference("timeLongPressTrigger",
                CustomSeekBarPreference.class);
        seek2.setValue(longPressTrigger);
        seek2.setSuffix(" ms");

        CustomSeekBarPreference seek3 = requirePreference("buttonscale",
                CustomSeekBarPreference.class);
        seek3.setValue(prefButtonSize);
        seek3.setSuffix(" %");

        CustomSeekBarPreference seek4 = requirePreference("mousescale",
                CustomSeekBarPreference.class);
        seek4.setValue(mouseScale);
        seek4.setSuffix(" %");

        CustomSeekBarPreference seek6 = requirePreference("mousespeed",
                CustomSeekBarPreference.class);
        seek6.setValue((int)(mouseSpeed *100f));
        seek6.setSuffix(" %");

        CustomSeekBarPreference deadzoneSeek = requirePreference("gamepad_deadzone_scale",
                CustomSeekBarPreference.class);
        deadzoneSeek.setValue((int) (joystickDeadzone * 100f));
        deadzoneSeek.setSuffix(" %");


        Context context = getContext();
        if(context != null) {
            mGyroAvailable = Tools.deviceSupportsGyro(context);
        }
        PreferenceCategory gyroCategory =  requirePreference("gyroCategory",
                PreferenceCategory.class);
        gyroCategory.setVisible(mGyroAvailable);

        CustomSeekBarPreference gyroSensitivitySeek = requirePreference("gyroSensitivity",
                CustomSeekBarPreference.class);
        gyroSensitivitySeek.setValue((int) (gyroSpeed*100f));
        gyroSensitivitySeek.setSuffix(" %");

        CustomSeekBarPreference gyroSampleRateSeek = requirePreference("gyroSampleRate",
                CustomSeekBarPreference.class);
        gyroSampleRateSeek.setValue(gyroSampleRate);
        gyroSampleRateSeek.setSuffix(" ms");

        CursorImagePickerPreference cursorImagePicker = requirePreference("customCursorImagePath",
                CursorImagePickerPreference.class);
        cursorImagePicker.refreshSummary();
        cursorImagePicker.setOnPickRequestedListener(new CursorImagePickerPreference.OnPickRequestedListener() {
            @Override
            public void onPickRequested() {
                mImagePickerLauncher.launch("image/*");
            }

            @Override
            public void onClearRequested() {
                getPreferenceManager().getSharedPreferences().edit()
                        .remove("customCursorImagePath")
                        .apply();
                Context ctx = getContext();
                if (ctx != null) {
                    File f = new File(ctx.getFilesDir(), "cursor/custom_cursor.png");
                    //noinspection ResultOfMethodCallIgnored
                    f.delete();
                }
                cursorImagePicker.refreshSummary();
            }
        });

        computeVisibility();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);
        computeVisibility();
    }

    private void computeVisibility(){
        requirePreference("timeLongPressTrigger").setVisible(!LauncherPreferences.PREF_DISABLE_GESTURES);
        requirePreference("gyroSensitivity").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        requirePreference("gyroSampleRate").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        requirePreference("gyroInvertX").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        requirePreference("gyroInvertY").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        requirePreference("gyroSmoothing").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
    }

}
