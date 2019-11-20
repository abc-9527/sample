package sun.target.sample.widget;

import android.util.DisplayMetrics;

public class DisplayUtils {

    public static int dip2px(float dipValue, DisplayMetrics metrics) {
        return (int) (dipValue * metrics.density + 0.5f);
    }
}
