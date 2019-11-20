package sun.target.img.glide;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Key;


import java.security.MessageDigest;

import okhttp3.HttpUrl;

/**
 * created by sfx on 2017/12/27.
 */

class MultiUrl implements Key {
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private String[] urls;
    @Nullable
    private volatile byte[] cacheKeyBytes;
    private int hashCode;
    @Nullable
    private String[] safeUrls;
    private final Drawable errorDrawable;

    MultiUrl(Drawable errorDrawable, String... urls) {
        this.errorDrawable = errorDrawable;
        this.urls = urls;
    }


    final void drawError(Canvas canvas, int left, int top, int right, int bottom) {
        if (canvas != null && errorDrawable != null) {
            errorDrawable.setBounds(left, top, right, bottom);
            errorDrawable.draw(canvas);
        } else {
            Log.e("Multi", "drawError " + canvas + " -- " + errorDrawable);
        }
    }

    int size() {
        return urls == null ? 0 : urls.length;
    }

    String getSafeStringUrl(int i) {
        if (i >= size()) return null;
        if (i < 0) return null;
        if (safeUrls == null) {
            this.safeUrls = new String[size()];
        }
        if (TextUtils.isEmpty(safeUrls[i])) {
            String url = urls[i];
            if (!TextUtils.isEmpty(url)) {
                HttpUrl parsed = HttpUrl.parse(Uri.encode(url, ALLOWED_URI_CHARS));
                if (parsed != null) {
                    safeUrls[i] = parsed.toString();
                }
            }
        }
        return safeUrls[i];
    }

    private String getCacheKey() {
        if (urls == null) return "";
        int size = urls.length;
        StringBuilder builder = new StringBuilder();
        String temp;
        for (int i = 0; i < size; i++) {
            temp = urls[i];
            builder.append(i).append(TextUtils.isEmpty(temp) ? "" : temp);
        }
        return builder.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return getCacheKey();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(getCacheKeyBytes());
    }

    private byte[] getCacheKeyBytes() {
        if (cacheKeyBytes == null) {
            cacheKeyBytes = getCacheKey().getBytes(CHARSET);
        }
        return cacheKeyBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MultiUrl) {
            MultiUrl other = (MultiUrl) o;
            return getCacheKey().equals(other.getCacheKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = getCacheKey().hashCode();
            hashCode = 31 * hashCode;
        }
        return hashCode;
    }
}
