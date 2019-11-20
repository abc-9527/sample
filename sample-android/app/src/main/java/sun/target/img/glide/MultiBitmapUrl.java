package sun.target.img.glide;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * created by sfx on 2017/12/28.
 */

public class MultiBitmapUrl extends MultiUrl {
    private static final ExecutorService workerSchedulers = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(),
            60L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    private final static int LOAD_TIMEOUT = 30;
    private final static int parentWidth = 192;//父组件宽
    private final static int parentHeight = 192;//父组件高
    private final static int mGap = 8; //宫格间距
    private RequestManager manager;
    private DataFetcher.DataCallback<? super InputStream> dataCallback;

    public MultiBitmapUrl(Drawable errorDrawable, String... urls) {
        super(errorDrawable, urls);
    }


    void loadFaceInputStream(RequestManager requestManager, DataFetcher.DataCallback<? super InputStream> callback) {
        dataCallback = callback;
        this.manager = requestManager;
        cleanup();
        startPreLoad();
        startMultiTask();
    }


    private Drawable getBitmap(RequestManager requestManager, int index) {
        final String url = getSafeStringUrl(index);
        if (TextUtils.isEmpty(url)) return null;
        try {
            return requestManager
                    .load(url)
                    .apply(RequestOptions.centerCropTransform().skipMemoryCache(true).override(parentWidth, parentHeight))
                    .submit().get(LOAD_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    void cleanup() {

    }


    private void startMultiTask() {
        if (dataCallback != null) {
            workerSchedulers.execute(new MultiWorker(this));
        }
    }


    private void startPreLoad() {
        if (manager != null) {
            workerSchedulers.execute(new PreLoadWorker(this));
        }
    }

    private void multiInner() {
        try {
            final Bitmap bitmap = multiBitmap(manager);
            final InputStream inputStream = convertBitmapToInputStream(bitmap);
            bitmap.recycle();
            if (dataCallback != null) {
                dataCallback.onDataReady(inputStream);
            }
        } catch (Exception e) {
            if (dataCallback != null) {
                dataCallback.onLoadFailed(new Exception(e));
            }
        }
    }

    private final AtomicBoolean isPreLoaded = new AtomicBoolean(false);

    private void preLoad() {
        if (manager != null) {
            if (isPreLoaded.getAndSet(true)) {
                return;
            }
            try {
                final int length = size();
                final int childrenCount = length <= 9 ? length : 9;
                for (int i = 0; i <= childrenCount - 1; i++) {
                    final String url = getSafeStringUrl(i);
                    manager.asFile().load(url).preload();
                }
            } catch (Exception e) {
                isPreLoaded.set(false);
            }
        }
    }

    private Bitmap multiBitmap(RequestManager requestManager) {
        final int length = size();
        final int childrenCount = length <= 9 ? length : 9;
        final int mColumnCount = calculateColumn(childrenCount);
        final Bitmap multiBitmap = Bitmap.createBitmap(parentWidth, parentHeight, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(multiBitmap);
        canvas.drawColor(Color.parseColor("#EEEEEE"));
        for (int i = 0; i <= childrenCount - 1; i++) {
            int rowNum = i / mColumnCount;//当前行数
            int columnNum = i % mColumnCount;//当前列数
            int mImageSize = (parentWidth - (mColumnCount + 1) * mGap) / mColumnCount;//图片尺寸

            int t_center = (parentHeight + mGap) / 2;//中间位置以下的顶点（有宫格间距）
            int b_center = (parentHeight - mGap) / 2;//中间位置以上的底部（有宫格间距）
            int l_center = (parentWidth + mGap) / 2;//中间位置以右的左部（有宫格间距）
            int r_center = (parentWidth - mGap) / 2;//中间位置以左的右部（有宫格间距）
            int center = (parentHeight - mImageSize) / 2;//中间位置以上顶部（无宫格间距）

            int left = mImageSize * columnNum + mGap * (columnNum + 1);
            int top = mImageSize * rowNum + mGap * (rowNum + 1);
            int right = left + mImageSize;
            int bottom = top + mImageSize;
            final Drawable bitmap = getBitmap(requestManager, i);
            /*
             * 不同子view情况下的不同显示
             */
            if (childrenCount == 1) {
                drawBitmapAt(canvas, bitmap, left, top, right, bottom);
            } else if (childrenCount == 2) {
                drawBitmapAt(canvas, bitmap, left, center, right, center + mImageSize);
            } else if (childrenCount == 3) {
                if (i == 0) {
                    drawBitmapAt(canvas, bitmap, center, top, center + mImageSize, bottom);
                } else {
                    drawBitmapAt(canvas, bitmap, mGap * i + mImageSize * (i - 1), t_center, mGap * i + mImageSize * i, t_center + mImageSize);
                }
            } else if (childrenCount == 4) {
                drawBitmapAt(canvas, bitmap, left, top, right, bottom);
            } else if (childrenCount == 5) {
                if (i == 0) {
                    drawBitmapAt(canvas, bitmap, r_center - mImageSize, r_center - mImageSize, r_center, r_center);
                } else if (i == 1) {
                    drawBitmapAt(canvas, bitmap, l_center, r_center - mImageSize, l_center + mImageSize, r_center);
                } else {
                    drawBitmapAt(canvas, bitmap, mGap * (i - 1) + mImageSize * (i - 2), t_center, mGap * (i - 1) + mImageSize * (i - 1), t_center + mImageSize);
                }
            } else if (childrenCount == 6) {
                if (i < 3) {
                    drawBitmapAt(canvas, bitmap, mGap * (i + 1) + mImageSize * i, b_center - mImageSize, mGap * (i + 1) + mImageSize * (i + 1), b_center);
                } else {
                    drawBitmapAt(canvas, bitmap, mGap * (i - 2) + mImageSize * (i - 3), t_center, mGap * (i - 2) + mImageSize * (i - 2), t_center + mImageSize);
                }
            } else if (childrenCount == 7) {
                if (i == 0) {
                    drawBitmapAt(canvas, bitmap, center, mGap, center + mImageSize, mGap + mImageSize);
                } else if (i > 0 && i < 4) {
                    drawBitmapAt(canvas, bitmap, mGap * i + mImageSize * (i - 1), center, mGap * i + mImageSize * i, center + mImageSize);
                } else {
                    drawBitmapAt(canvas, bitmap, mGap * (i - 3) + mImageSize * (i - 4), t_center + mImageSize / 2, mGap * (i - 3) + mImageSize * (i - 3), t_center + mImageSize / 2 + mImageSize);
                }
            } else if (childrenCount == 8) {
                if (i == 0) {
                    drawBitmapAt(canvas, bitmap, r_center - mImageSize, mGap, r_center, mGap + mImageSize);
                } else if (i == 1) {
                    drawBitmapAt(canvas, bitmap, l_center, mGap, l_center + mImageSize, mGap + mImageSize);
                } else if (i > 1 && i < 5) {
                    drawBitmapAt(canvas, bitmap, mGap * (i - 1) + mImageSize * (i - 2), center, mGap * (i - 1) + mImageSize * (i - 1), center + mImageSize);
                } else {
                    drawBitmapAt(canvas, bitmap, mGap * (i - 4) + mImageSize * (i - 5), t_center + mImageSize / 2, mGap * (i - 4) + mImageSize * (i - 4), t_center + mImageSize / 2 + mImageSize);
                }
            } else if (childrenCount == 9) {
                drawBitmapAt(canvas, bitmap, left, top, right, bottom);
            }
        }
        return multiBitmap;
    }

    private void drawBitmapAt(Canvas canvas, Drawable bitmap, int left, int top, int right, int bottom) {
        if (bitmap != null) {
            bitmap.setBounds(left, top, right, bottom);
            bitmap.draw(canvas);
        } else {
            drawError(canvas, left, top, right, bottom);
        }
    }

    private int calculateColumn(int imagesSize) {
        if (imagesSize < 3) {
            return imagesSize;
        } else if (imagesSize <= 4) {
            return 2;
        } else {
            return 3;
        }
    }

    private InputStream convertBitmapToInputStream(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    static final class PreLoadWorker implements Runnable {
        private final MultiBitmapUrl multiBitmapUrl;

        PreLoadWorker(MultiBitmapUrl multiBitmapUrl) {
            this.multiBitmapUrl = multiBitmapUrl;
        }

        @Override
        public void run() {
            multiBitmapUrl.preLoad();
        }
    }

    static final class MultiWorker implements Runnable {
        private final MultiBitmapUrl multiBitmapUrl;

        MultiWorker(MultiBitmapUrl multiBitmapUrl) {
            this.multiBitmapUrl = multiBitmapUrl;
        }

        @Override
        public void run() {
            multiBitmapUrl.multiInner();
        }
    }
}
