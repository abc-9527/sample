package sun.target.sample.anim;

import android.graphics.Path;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import sun.target.anim.AbsPathDrawable;

public class PathAnimDrawable extends AbsPathDrawable {
    public static final int SHAPE_POLYGON = 0;
    public static final int SHAPE_TRIANGLE = 1;

    @IntDef({SHAPE_POLYGON, SHAPE_TRIANGLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface shape {
    }

    private final int shape;

    private PathAnimDrawable(int stroke, int stokeColor, int endColor, boolean running, boolean spread, int shape) {
        super(stroke, stokeColor, endColor, running, spread);
        this.shape = shape;
    }

    @Override
    protected void drawPath(Path bgPath, int left, int top, int right, int bottom, int stroke) {
        final int width = right - left;
        final int height = bottom - top;
        if (shape == SHAPE_POLYGON) {
            SVGToPath.drawPolygon(bgPath, width, height, left, top);
        } else if (shape == SHAPE_TRIANGLE) {
            SVGToPath.drawTriangle(bgPath, width, height, left, top);
        }
    }

    public final static class Builder {
        private int stroke;
        private int stokeColor;
        private int endColor;
        private int shape = PathAnimDrawable.SHAPE_POLYGON;
        private boolean running = true;
        private boolean spread = true;

        public Builder setStroke(int stroke) {
            this.stroke = stroke;
            return this;
        }

        public Builder setStokeColor(int stokeColor) {
            this.stokeColor = stokeColor;
            return this;
        }

        public Builder setEndColor(int endColor) {
            this.endColor = endColor;
            return this;
        }

        public Builder setShape(@shape int shape) {
            this.shape = shape;
            return this;
        }


        public Builder setRunning(boolean running) {
            this.running = running;
            return this;
        }

        public Builder setSpread(boolean spread) {
            this.spread = spread;
            return this;
        }

        public PathAnimDrawable build() {
            return new PathAnimDrawable(stroke, stokeColor, endColor, running, spread, shape);
        }
    }
}
