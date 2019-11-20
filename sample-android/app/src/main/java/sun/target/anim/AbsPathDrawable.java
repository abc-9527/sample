package sun.target.anim;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.animation.ValueAnimator.INFINITE;
import static android.animation.ValueAnimator.RESTART;
import static android.graphics.PixelFormat.OPAQUE;

public abstract class AbsPathDrawable extends Drawable implements Animatable {

    private final int stroke;
    private final int endColor;
    private final int stokeColor;

    private Path mBgPath;
    private ValueAnimator animator;
    private LinearGradient leftGradient;
    private LinearGradient rightGradient;
    private volatile boolean isDrawing = false;
    //
    private Paint mRunningPaint;
    private PathMeasure pathMeasure;
    private float pathLength;
    private Path leftPath;
    private Path rightPath;
    //
    private int spreadColor;
    private float spreadScale = 1;
    private Paint mSpreadPaint;
    private int centerX;
    private int centerY;
    //


    public AbsPathDrawable(int stroke, @ColorInt int stokeColor, @ColorInt int endColor, boolean running, boolean spread) {
        this.stroke = stroke;
        if (running) {
            this.mRunningPaint = new Paint();
            initPaint(mRunningPaint);
            if (mBgPath == null) {
                mBgPath = new Path();
            }
            this.mRunningPaint.setStrokeWidth(stroke);
        }
        if (spread) {
            mSpreadPaint = new Paint();
            initPaint(mSpreadPaint);
            if (mBgPath == null) {
                mBgPath = new Path();
            }
        }

        this.stokeColor = stokeColor;
        this.endColor = endColor;
        this.spreadColor = stokeColor;
    }

    private void initPaint(Paint paint) {
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
    }

    protected abstract void drawPath(Path bgPath, int left, int top, int right, int bottom, final int stroke);

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (mBgPath != null) {
            final int width = right - left;
            final int height = bottom - top;
            mBgPath.reset();
            if (mSpreadPaint != null) {
                centerX = width / 2;
                centerY = height / 2;
                final int l = centerX / 2 + left;
                final int t = centerY / 2 + top;
                drawPath(mBgPath, l, t, l + centerX, t + centerY, stroke);
                updateSpread(1);
            } else {
                drawPath(mBgPath, left, top, right, bottom, stroke);
            }

            if (mRunningPaint != null) {
                pathMeasure = new PathMeasure();
                pathMeasure.setPath(mBgPath, false);
                pathLength = pathMeasure.getLength();
                updateRun(0);
            }


            // init anim
            animator = ValueAnimator.ofFloat(0, 1);
            animator.setDuration(2200);
            animator.setRepeatCount(INFINITE);
            animator.setRepeatMode(RESTART);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (isDrawing) return;
                    Object object = animation.getAnimatedValue();
                    if (object instanceof Float) {
                        float v = (float) object;
                        if (mSpreadPaint != null) {
                            float factor = v * 1.9f;
                            updateSpread(factor >= 1 ? 1 : factor);
                        }
                        if (mRunningPaint != null) {
                            updateRun(v * pathLength);
                        }
                        invalidateSelf();
                    }
                }
            });
        }
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mSpreadPaint != null) {
            final int saveCount = canvas.save();
            // 扩散部分
            if (spreadScale != 1) {
                canvas.scale(spreadScale, spreadScale, centerX, centerY);
            }
            mSpreadPaint.setStrokeWidth(stroke * spreadScale);
            mSpreadPaint.setColor(spreadColor);
            canvas.drawPath(mBgPath, mSpreadPaint);
            canvas.restoreToCount(saveCount);
        }
        if (mRunningPaint != null) {
            // running 部分
            mRunningPaint.setShader(null);
            mRunningPaint.setColor(stokeColor);
            canvas.drawPath(mBgPath, mRunningPaint);
            //
            mRunningPaint.setColor(Color.WHITE);
            mRunningPaint.setShader(leftGradient);
            canvas.drawPath(leftPath, mRunningPaint);
            //
            mRunningPaint.setShader(rightGradient);
            canvas.drawPath(rightPath, mRunningPaint);
        }
        isDrawing = false;
    }

    private void updateRun(float value) {
        float lineLength = pathLength / 2;
        leftRunLine(lineLength, value);
        rightRunLine(lineLength, (value + pathLength / 2) % pathLength);
    }

    private void updateSpread(float v) {
        spreadScale = v + 1f;
        spreadColor = argbEvaluator(v * 0.3f + 0.7f, stokeColor, endColor);
    }

    private int argbEvaluator(float fraction, int startInt, int endInt) {

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

    //
    private void rightRunLine(float lineLength, float factor) {
        float dFactor = factor + lineLength - pathLength;
        float[] startPoint = new float[2];
        float[] endPoint = new float[2];
        pathMeasure.getPosTan(factor, endPoint, null);
        final float endDistance = (factor + lineLength) % pathLength;
        pathMeasure.getPosTan(endDistance, startPoint, null);
        if (rightPath == null) {
            rightPath = new Path();
        } else {
            rightPath.reset();
        }
        if (dFactor <= 0) {
            pathMeasure.getSegment(factor, factor + lineLength, rightPath, true);
        } else {
            pathMeasure.getSegment(0, dFactor, rightPath, true);
            pathMeasure.getSegment(factor, pathLength, rightPath, true);
        }
        rightGradient = new LinearGradient(startPoint[0], startPoint[1]
                , endPoint[0], endPoint[1]
                , new int[]{Color.parseColor("#E2E2E2"), Color.parseColor("#00E2E2E2")}
                , new float[]{0f, 0.8f}
                , Shader.TileMode.CLAMP);
    }

    //
    private void leftRunLine(float lineLength, float factor) {
        float dFactor = factor + lineLength - pathLength;
        float[] startPoint = new float[2];
        float[] endPoint = new float[2];
        pathMeasure.getPosTan(factor, endPoint, null);
        final float endDistance = (factor + lineLength) % pathLength;
        pathMeasure.getPosTan(endDistance, startPoint, null);
        //
        if (leftPath == null) {
            leftPath = new Path();
        } else {
            leftPath.reset();
        }
        if (dFactor <= 0) {
            pathMeasure.getSegment(factor, factor + lineLength, leftPath, true);
        } else {
            pathMeasure.getSegment(0, dFactor, leftPath, true);
            pathMeasure.getSegment(factor, pathLength, leftPath, true);
        }
        leftGradient = new LinearGradient(startPoint[0], startPoint[1]
                , endPoint[0], endPoint[1]
                , new int[]{Color.parseColor("#E2E2E2"), Color.parseColor("#00E2E2E2")}
                , new float[]{0f, 0.8f}
                , Shader.TileMode.CLAMP);
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

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }
}
