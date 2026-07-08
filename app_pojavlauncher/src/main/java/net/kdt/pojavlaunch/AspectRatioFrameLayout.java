package net.kdt.pojavlaunch;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;

/**
 * A FrameLayout that forces its single child to be measured and laid out
 * inside a fixed 16:9 box, centered within whatever space this view is
 * given. Any leftover space (top/bottom or left/right, depending on the
 * screen's actual aspect ratio) is left as plain black background,
 * producing a letterboxed/pillarboxed look.
 *
 * This view is meant to be inserted once, as the direct parent of the
 * view passed to Activity#setContentView, so every screen in the app
 * gets the same fixed-ratio treatment without each screen's layout
 * needing to know about it.
 */
public class AspectRatioFrameLayout extends FrameLayout {
    private static final float TARGET_ASPECT_RATIO = 16f / 9f;

    public AspectRatioFrameLayout(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int availableWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (availableWidth <= 0 || availableHeight <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int targetWidth;
        int targetHeight;

        float availableRatio = (float) availableWidth / (float) availableHeight;
        if (availableRatio > TARGET_ASPECT_RATIO) {
            // Screen is wider than 16:9 relative to its height: pillarbox (bars on left/right).
            targetHeight = availableHeight;
            targetWidth = Math.round(targetHeight * TARGET_ASPECT_RATIO);
        } else {
            // Screen is taller than 16:9 relative to its width: letterbox (bars on top/bottom).
            targetWidth = availableWidth;
            targetHeight = Math.round(targetWidth / TARGET_ASPECT_RATIO);
        }

        int childWidthSpec = MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY);
        int childHeightSpec = MeasureSpec.makeMeasureSpec(targetHeight, MeasureSpec.EXACTLY);

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(childWidthSpec, childHeightSpec);
        }

        setMeasuredDimension(
                MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int parentWidth = right - left;
        int parentHeight = bottom - top;

        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int childLeft = (parentWidth - childWidth) / 2;
            int childTop = (parentHeight - childHeight) / 2;

            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }
}
