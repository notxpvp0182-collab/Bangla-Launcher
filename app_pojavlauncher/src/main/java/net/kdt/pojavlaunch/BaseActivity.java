package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.appcompat.app.*;
import net.kdt.pojavlaunch.utils.*;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_IGNORE_NOTCH;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this);
        Tools.setInsetsMode(this, setFullscreen(), shouldIgnoreNotch());
        Tools.getDisplayMetrics(this);
    }

    /**
     * Wraps every screen's content in a fixed 16:9 letterbox container before
     * attaching it as usual. This keeps the whole app -- settings, instance
     * list, etc -- constrained to a consistent 16:9 box on any device shape,
     * with black bars filling the rest of the screen.
     */
    @Override
    public void setContentView(int layoutResID) {
        if (!useAspectRatioLock()) {
            super.setContentView(layoutResID);
            return;
        }
        AspectRatioFrameLayout aspectRatioContainer = new AspectRatioFrameLayout(this);
        aspectRatioContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutInflater.from(this).inflate(layoutResID, aspectRatioContainer, true);
        super.setContentView(aspectRatioContainer);
    }

    /**
     * @return Whether this activity should be constrained to the fixed 16:9
     * box. Defaults to true for the whole app; override to return false for
     * a specific activity if it needs raw fullscreen (e.g. a game surface
     * that manages its own coordinate mapping).
     */
    protected boolean useAspectRatioLock() {
        return true;
    }

    /** @return Whether the activity should be set as a fullscreen one */
    public boolean setFullscreen(){
        return true;
    }


    @Override
    public void startActivity(Intent i) {
        super.startActivity(i);
        //new Throwable("StartActivity").printStackTrace();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.checkStorageInteractive(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Tools.setInsetsMode(this, setFullscreen(), shouldIgnoreNotch());
        Tools.getDisplayMetrics(this);
    }

    /** @return Whether or not the notch should be ignored */
    protected boolean shouldIgnoreNotch(){
        return PREF_IGNORE_NOTCH;
    }
}
