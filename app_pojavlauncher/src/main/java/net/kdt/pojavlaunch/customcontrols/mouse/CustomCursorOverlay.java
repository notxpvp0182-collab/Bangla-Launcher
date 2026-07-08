package net.kdt.pojavlaunch.customcontrols.mouse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import git.artdeell.dnbootstrap.glfw.GLFW;

/**
 * A standalone overlay that renders a user-selectable cursor image, positioned
 * by reading GLFW.cursorX / GLFW.cursorY (public static fields already written
 * to by TouchEventProcessor). This view does not write to GLFW state and does
 * not depend on GLFWCursorView internals - it is purely an additional visual
 * layer that can be shown instead of the default cursor view when the user
 * picks a custom cursor style or image.
 */
public class CustomCursorOverlay extends View {
    private static final long FRAME_INTERVAL_MS = 16; // ~60fps polling of GLFW.cursorX/Y

    private Bitmap mCursorBitmap;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mRunning = false;

    private final Runnable mFrameTick = new Runnable() {
        @Override
        public void run() {
            if (!mRunning) return;
            invalidate();
            mHandler.postDelayed(this, FRAME_INTERVAL_MS);
        }
    };

    public CustomCursorOverlay(Context context) {
        super(context);
        init();
    }

    public CustomCursorOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFocusable(false);
        setClickable(false);
        // Overlay must never intercept touches; it is purely visual.
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        reloadCursorBitmap();
    }

    /** Re-reads the current cursor preference (preset drawable or custom image path) and rebuilds the bitmap used for drawing. */
    public void reloadCursorBitmap() {
        Bitmap newBitmap = null;
        try {
            String customPath = LauncherPreferences.DEFAULT_PREF.getString("customCursorImagePath", null);
            if (customPath != null && !customPath.isEmpty()) {
                newBitmap = BitmapFactory.decodeFile(customPath);
            }
            if (newBitmap == null) {
                int drawableRes = CursorStylePresets.resolvePresetDrawableRes(
                        LauncherPreferences.DEFAULT_PREF.getString("cursorStylePreset", "classic"));
                Drawable d = getContext().getDrawable(drawableRes);
                if (d != null) {
                    int w = Math.max(1, d.getIntrinsicWidth());
                    int h = Math.max(1, d.getIntrinsicHeight());
                    newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(newBitmap);
                    d.setBounds(0, 0, w, h);
                    d.draw(c);
                }
            }
        } catch (Exception e) {
            // Fall back silently to no custom bitmap; the default GLFWCursorView remains usable.
            newBitmap = null;
        }
        mCursorBitmap = newBitmap;
        invalidate();
    }

    public void startTracking() {
        if (mRunning) return;
        mRunning = true;
        mHandler.post(mFrameTick);
    }

    public void stopTracking() {
        mRunning = false;
        mHandler.removeCallbacks(mFrameTick);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCursorBitmap == null || mCursorBitmap.isRecycled()) return;
        if (getWidth() == 0 || getHeight() == 0) return;

        float cursorScale = LauncherPreferences.PREF_MOUSESCALE;
        float drawW = mCursorBitmap.getWidth() * cursorScale;
        float drawH = mCursorBitmap.getHeight() * cursorScale;

        float px = GLFW.cursorX * getWidth();
        float py = GLFW.cursorY * getHeight();

        // Anchor at the top-left tip of the cursor image, matching typical arrow-cursor hotspot behavior.
        android.graphics.RectF dest = new android.graphics.RectF(px, py, px + drawW, py + drawH);
        canvas.drawBitmap(mCursorBitmap, null, dest, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTracking();
    }
}
