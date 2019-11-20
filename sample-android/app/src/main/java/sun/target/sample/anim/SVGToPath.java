package sun.target.sample.anim;


import android.graphics.Path;

/**
 * created by sfx on 2018/4/20.
 */

public class SVGToPath {
    private final Path path;
    private final float dx;
    private final float dy;
    private final float factorX;
    private final float factorY;

    private SVGToPath(Path path, float srcWidth, float srcHeight, float tagWidth, float tagHeight, float factorX, float factorY) {
        this.path = path;
        this.factorX = factorX;
        this.factorY = factorY;
        this.dx = tagWidth / srcWidth;
        this.dy = tagHeight / srcHeight;
    }

    private void moveTo(float x, float y) {
        path.moveTo(factorX + x * dx, factorY + y * dy);
    }

    private void cubicTo(float x1, float y1
            , float x2, float y2
            , float x3, float y3) {
        path.cubicTo(factorX + x1 * dx, factorY + y1 * dy
                , factorX + x2 * dx, factorY + y2 * dy
                , factorX + x3 * dx, factorY + y3 * dy);
    }

    public static void drawPolygon(Path path, float tagWidth, float tagHeight, float factorX, float factorY) {
        SVGToPath svg = new SVGToPath(path, 128, 128, tagWidth, tagHeight, factorX, factorY);
        svg.moveTo(55.65f, 7.84f);
        svg.cubicTo(66.71f, 5.97f, 77.91f, 8.38f, 88.58f, 11.26f);
        svg.cubicTo(96.06f, 13.51f, 103.44f, 16.13f, 110.55f, 19.35f);
        svg.cubicTo(112.79f, 20.23f, 112.63f, 22.97f, 112.69f, 24.95f);
        svg.cubicTo(112.64f, 38.97f, 113.59f, 53.04f, 112.29f, 67.04f);
        svg.cubicTo(111.83f, 76.71f, 109.46f, 86.63f, 103.67f, 94.57f);
        svg.cubicTo(96.74f, 104.15f, 86.63f, 110.74f, 76.65f, 116.78f);
        svg.cubicTo(72.59f, 119.05f, 68.62f, 121.86f, 63.93f, 122.57f);
        svg.cubicTo(59.53f, 121.93f, 55.80f, 119.30f, 52.00f, 117.18f);
        svg.cubicTo(40.59f, 110.35f, 28.76f, 102.79f, 21.89f, 91.02f);
        svg.cubicTo(16.21f, 81.37f, 15.60f, 69.89f, 15.00f, 58.99f);
        svg.cubicTo(14.98f, 47.30f, 14.96f, 35.60f, 15.17f, 23.91f);
        svg.cubicTo(15.09f, 21.91f, 15.89f, 19.66f, 18.00f, 19.06f);
        svg.cubicTo(30.09f, 14.00f, 42.60f, 9.53f, 55.65f, 7.84f);
    }

    public static void drawTriangle(Path path, float tagWidth, float tagHeight, float factorX, float factorY) {
        SVGToPath svg = new SVGToPath(path, 300, 300, tagWidth, tagHeight, factorX, factorY);
        svg.moveTo(43.19f, 51.24f);
        svg.cubicTo(46.8f, 50.87f, 50.43f, 51.01f, 54.05f, 51);
        svg.cubicTo(120.39f, 51f, 186.72f, 51f, 253.06f, 51);
        svg.cubicTo(260.25f, 50.99f, 268.14f, 53.49f, 272.27f, 59.76f);
        svg.cubicTo(278.22f, 67.22f, 278.29f, 78.27f, 273.17f, 86.19f);
        svg.cubicTo(241.55f, 138.55f, 209.85f, 190.88f, 178.23f, 243.24f);
        svg.cubicTo(173.77f, 250.42f, 167.4f, 256.86f, 158.97f, 259f);
        svg.cubicTo(145.26f, 263.79f, 129.67f, 256.67f, 122.54f, 244.45f);
        svg.cubicTo(91.98f, 194f, 61.49f, 143.5f, 30.95f, 93.03f);
        svg.cubicTo(27.43f, 87.16f, 23.03f, 81.22f, 23.07f, 74.05f);
        svg.cubicTo(22.24f, 62.67f, 31.99f, 52.13f, 43.19f, 51.24f);
    }

}
