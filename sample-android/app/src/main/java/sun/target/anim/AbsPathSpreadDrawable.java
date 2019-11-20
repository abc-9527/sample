package sun.target.anim;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.animation.ValueAnimator.INFINITE;
import static android.animation.ValueAnimator.RESTART;
import static android.graphics.PixelFormat.OPAQUE;

/**
 * created by sfx on 2018/5/2.
 */
@Deprecated
public abstract class AbsPathSpreadDrawable extends Drawable implements Animatable {
    private final Paint mPaint = new Paint();
    private final int stroke;
    private final Path mBgPath = new Path();
    private int centerX;
    private int centerY;
    private final int startColor;
    private final int endColor;
    private int stokeColor;

    private ValueAnimator animator;

    public AbsPathSpreadDrawable(int stroke, @ColorInt int startColor, @ColorInt int endColor) {
        this.stroke = stroke;
        this.startColor = startColor;
        this.endColor = endColor;
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mBgPath.reset();
        centerX = (right - left) / 2;
        centerY = (bottom - top) / 2;
        int w = centerX / 2;
        int h = centerY / 2;
        drawPath(mBgPath, left + w, top + h, right - w, bottom - h, stroke);
        animator = ValueAnimator.ofFloat(1f, 1.5f);
        animator.setDuration(1600);
        animator.setRepeatCount(INFINITE);
        animator.setRepeatMode(RESTART);
        animator.setInterpolator(new DecelerateInterpolator(1.1f));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isDrawing) return;
                scale = (float) animation.getAnimatedValue();
                update(scale);
                invalidateSelf();
            }
        });
        update(1f);
    }

    private void update(float scale) {
        stokeColor = argbEvaluator((scale - 1) + 0.5f, startColor, endColor);
    }

    private float scale = 1;

    protected abstract void drawPath(Path bgPath, int left, int top, int right, int bottom, final int stroke);

    private volatile boolean isDrawing = false;

    @Override
    public final void invalidateSelf() {
        isDrawing = true;
        super.invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public final void draw(@NonNull Canvas canvas) {
        canvas.save();
        if (scale != 1) {
            canvas.scale(scale, scale, centerX, centerY);
        }
        mPaint.setStrokeWidth(stroke * scale);
        mPaint.setColor(stokeColor);
        canvas.drawPath(mBgPath, mPaint);
        canvas.restore();
        isDrawing = false;
    }


    @Override
    public int getOpacity() {
        return OPAQUE;
    }

    @Override
    public void start() {
        if (animator != null && !animator.isRunning()) {
            animator.start();
        }
    }

    @Override
    public void stop() {
        if (animator != null && animator.isRunning()) {
            animator.end();
        }
    }

    @Override
    public boolean isRunning() {
        return animator != null && animator.isRunning();
    }

    private static int argbEvaluator(float fraction, int startInt, int endInt) {

        // startInt
        float startA = ((startInt >> 24) & 0xff) / 255.0f;
        float startR = ((startInt >> 16) & 0xff) / 255.0f;
        float startG = ((startInt >> 8) & 0xff) / 255.0f;
        float startB = ((startInt) & 0xff) / 255.0f;
        // endInt
        float endA = ((endInt >> 24) & 0xff) / 255.0f;
        float endR = ((endInt >> 16) & 0xff) / 255.0f;
        float endG = ((endInt >> 8) & 0xff) / 255.0f;
        float endB = ((endInt) & 0xff) / 255.0f;

        // convert from sRGB to linear
        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);

        // compute the interpolated color in linear space
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }
}
